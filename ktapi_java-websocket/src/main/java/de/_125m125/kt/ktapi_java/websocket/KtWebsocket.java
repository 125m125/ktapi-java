package de._125m125.kt.ktapi_java.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.entities.User;

public abstract class KtWebsocket implements KtNotificationManager {
    public static final String                               SERVER_ENDPOINT_URI = "wss://kt.125m125.de/api/websocket";

    private final Map<String, Map<String, SubscriptionList>> subscriptions       = new HashMap<>();
    private final Map<Integer, Consumer<ResponseMessage>>    waiting             = new HashMap<>();

    private boolean                                          active              = false;
    private final AtomicInteger                              lastRequestId       = new AtomicInteger();
    private Thread                                           restart_wait_thread;

    private final MessageParser                              parser;

    public KtWebsocket() {
        this.parser = new MessageParser();
    }

    public synchronized void stop() {
        this.active = false;
        if (this.restart_wait_thread != null && this.restart_wait_thread.isAlive()) {
            this.restart_wait_thread.interrupt();
        }
        close();
    }

    protected abstract void close();

    public synchronized void start() {
        if (this.active) {
            return;
        }
        this.active = true;
        reconnect(500);
    }

    private synchronized void reconnect(final long previousDelay) {
        if (!this.active) {
            return;
        }
        if (!connect()) {
            reConnectDelayed(previousDelay == 0 ? 500 : previousDelay * 2);
        }
    }

    protected abstract boolean connect();

    private synchronized void reConnectDelayed(final long delay) {
        if (this.restart_wait_thread != null && this.restart_wait_thread.isAlive()
                && this.restart_wait_thread != Thread.currentThread()) {
            throw new IllegalStateException("this instance is already waiting for a reconnect");
        }
        this.restart_wait_thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            reconnect(delay);
        });
        this.restart_wait_thread.setDaemon(true);
        this.restart_wait_thread.start();
    }

    @Override
    public void subscribeToMessages(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequest request = new SubscriptionRequest("rMessages", user, selfCreated);
        subscribe(request, "messages", user.getUID(), user, listener);
    }

    @Override
    public void subscribeToTrades(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequest request = new SubscriptionRequest("rOrders", user, selfCreated);
        subscribe(request, "trades", user.getUID(), user, listener);
    }

    @Override
    public void subscribeToItems(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequest request = new SubscriptionRequest("rItems", user, selfCreated);
        subscribe(request, "items", user.getUID(), user, listener);
    }

    @Override
    public void subscribeToPayouts(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequest request = new SubscriptionRequest("rPayouts", user, selfCreated);
        subscribe(request, "payouts", user.getUID(), user, listener);
    }

    @Override
    public void subscribeToOrderbook(final NotificationListener listener) {
        final SubscriptionRequest request = new SubscriptionRequest("orderbook");
        subscribe(request, "orderbook", null, null, listener);
    }

    @Override
    public void subscribeToHistory(final NotificationListener listener) {
        final SubscriptionRequest request = new SubscriptionRequest("history");
        subscribe(request, "history", null, null, listener);
    }

    public ResponseMessage subscribe(final SubscriptionRequest request, final String source, final String key,
            final User owner, final NotificationListener listener) {
        final Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("subscribe", request);
        try {
            final ResponseMessage sendAndAwait = sendAndAwait(requestMap);
            if (sendAndAwait.success()) {
                SubscriptionList subList;
                synchronized (this.subscriptions) {
                    subList = this.subscriptions.computeIfAbsent(source, n -> new HashMap<>()).computeIfAbsent(key,
                            n -> new SubscriptionList(owner));
                }
                subList.addListener(listener, request.isSelfCreated());
            }
            return sendAndAwait;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseMessage("error getting response from server", e);
        }
    }

    @Override
    public void disconnect() {
        stop();
    }

    public synchronized void onClose(final boolean reconnect) {
        if (this.active && reconnect) {
            reConnectDelayed(0L);
        }
    }

    protected void onOpen() {
        synchronized (this.subscriptions) {
            // TODO restore session or resubscribe
        }
    }

    public void onMessage(final String message) {
        System.out.println("received: " + message);
        final Optional<Object> parsedMessage = this.parser.parse(message);
        parsedMessage.ifPresent(obj -> {
            if (obj instanceof ResponseMessage) {
                final ResponseMessage responseMessage = (ResponseMessage) obj;
                responseMessage.getRequestId().map(this.waiting::get).ifPresent(c -> c.accept(responseMessage));
            } else if (obj instanceof UpdateNotification) {
                SubscriptionList keyList = null;
                SubscriptionList unkeyedList = null;
                final UpdateNotification notificationMessage = (UpdateNotification) obj;
                synchronized (this.subscriptions) {
                    final Map<String, SubscriptionList> sourceMap = this.subscriptions
                            .get(notificationMessage.getSource());
                    if (sourceMap != null) {
                        keyList = sourceMap.get(notificationMessage.getKey());
                        unkeyedList = sourceMap.get(null);
                    }
                }
                if (keyList != null) {
                    keyList.notifyListeners(notificationMessage);
                }
                if (unkeyedList != null) {
                    unkeyedList.notifyListeners(notificationMessage);
                }
            }
        });
    }

    public abstract void sendMessage(final String message);

    public void send(final Object data, final Consumer<ResponseMessage> callback) {
        final Gson gson = new Gson();
        final JsonElement jsonData = gson.toJsonTree(data);
        int rid = -1;
        if (callback != null) {
            rid = this.lastRequestId.incrementAndGet();
            ((JsonObject) jsonData).addProperty("rid", rid);
        }
        try {
            sendMessage(gson.toJson(jsonData));
            if (callback != null) {
                synchronized (this.waiting) {
                    this.waiting.put(rid, callback);
                }
            }
        } catch (final SendFailedException e) {
            if (callback != null) {
                callback.accept(new ResponseMessage("failed to send subscription message", e));
            } else {
                throw e;
            }
        }
    }

    public ResponseMessage sendAndAwait(final Object data) throws InterruptedException, ExecutionException {
        final CompletableFuture<ResponseMessage> cf = new CompletableFuture<>();
        send(data, cf::complete);
        return cf.get();
    }

}
