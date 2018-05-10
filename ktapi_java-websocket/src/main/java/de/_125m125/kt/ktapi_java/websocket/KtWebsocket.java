package de._125m125.kt.ktapi_java.websocket;

import java.io.IOException;
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
import de._125m125.kt.ktapi_java.websocket.requests.SessionRequestData;
import de._125m125.kt.ktapi_java.websocket.requests.SubscriptionRequestData;
import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi_java.websocket.responses.SessionResponse;
import de._125m125.kt.ktapi_java.websocket.responses.UpdateNotification;

/**
 * This class manages websockets to the Kadcontrade server. The actual Websocket
 * is managed in implementing classes, while this abstract class manages the
 * creation and interpretation of messages to and from the server and websocket
 * sessions. It also handles the reconnection attempts when a websocket was
 * disconnected.
 */
public abstract class KtWebsocket implements KtNotificationManager {

    /** The URL endpoint for Kadcontrade websockets. */
    public static final String                               SERVER_ENDPOINT_URI = "wss://kt.125m125.de/api/websocket";

    /**
     * all active subscriptions. First key: channel. Second key: key. Null in
     * the second key means, that the client is interested in all events on this
     * channel.
     */
    private final Map<String, Map<String, SubscriptionList>> subscriptions       = new HashMap<>();

    /**
     * Callbacks for previous requests that have not been answered so far. The
     * key represents the Request id.
     */
    private final Map<Integer, Consumer<ResponseMessage>>    waiting             = new HashMap<>();

    /** The id of the websocket session. */
    private String                                           sessionId;

    /** True, if the websocket is currently active. */
    private volatile boolean                                 active              = false;

    /** The request id of the last sent request. */
    private final AtomicInteger                              lastRequestId       = new AtomicInteger();

    /**
     * The Thread waiting to restart the websocket after connection was lost.
     */
    private Thread                                           restart_wait_thread;

    /** The last delay before a reconnection attempt was made. */
    private volatile long                                    lastDelay           = 0;

    /** The Messageparser for incoming messages. */
    private final MessageParser                              parser;

    /** True, if websocket sessions should be used. */
    private final boolean                                    useSession;

    /**
     * Instantiates a new KtWebsocket.
     */
    public KtWebsocket() {
        this(true);
    }

    /**
     * Instantiates a new ktWebsocket.
     *
     * @param session
     *            true, if the websocket should use sessions
     */
    public KtWebsocket(final boolean session) {
        this.useSession = session;
        this.parser = new MessageParser();
    }

    /**
     * Stops the websocket and terminates the connection.
     */
    public synchronized void stop() {
        this.active = false;
        if (this.restart_wait_thread != null && this.restart_wait_thread.isAlive()) {
            this.restart_wait_thread.interrupt();
        }
        close();
    }

    /**
     * Closes the websocket connection.
     */
    protected abstract void close();

    /**
     * Starts a new websocket connection.
     */
    public synchronized void start() {
        if (this.active) {
            return;
        }
        this.lastDelay = 0;
        this.active = true;
        reconnect();
    }

    /**
     * attempt to reconnect the websocket.
     */
    private synchronized void reconnect() {
        if (!this.active) {
            return;
        }
        if (!connect()) {
            reConnectDelayed();
        }
    }

    /**
     * creates a new websocket connection.
     *
     * @return true, if the success or failure is determined by events
     *         ({@link #onOpen()} or {@link #onClose(boolean)}, false if the
     *         connection attempt failed and a reconnection attempt should be
     *         started without waiting for events
     */
    protected abstract boolean connect();

    /**
     * reconnects the websocket after a delay.
     */
    private synchronized void reConnectDelayed() {
        if (this.restart_wait_thread != null && this.restart_wait_thread.isAlive()
                && this.restart_wait_thread != Thread.currentThread()) {
            throw new IllegalStateException("this instance is already waiting for a reconnect");
        }
        this.restart_wait_thread = new Thread(() -> {
            this.lastDelay = this.lastDelay != 0 ? this.lastDelay * 2 : 1000;
            try {
                Thread.sleep(this.lastDelay);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            reconnect();
        });
        this.restart_wait_thread.setDaemon(false);
        this.restart_wait_thread.start();
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToMessages(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.User, boolean)
     */
    @Override
    public void subscribeToMessages(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rMessages", user, selfCreated);
        subscribe(request, "messages", user.getUID(), user, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToTrades(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.User, boolean)
     */
    @Override
    public void subscribeToTrades(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rOrders", user, selfCreated);
        subscribe(request, "trades", user.getUID(), user, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToItems(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.User, boolean)
     */
    @Override
    public void subscribeToItems(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rItems", user, selfCreated);
        subscribe(request, "items", user.getUID(), user, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToPayouts(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.User, boolean)
     */
    @Override
    public void subscribeToPayouts(final NotificationListener listener, final User user, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rPayouts", user, selfCreated);
        subscribe(request, "payouts", user.getUID(), user, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToOrderbook(de._125m125.kt.ktapi_java.core.NotificationListener)
     */
    @Override
    public void subscribeToOrderbook(final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData("orderbook");
        subscribe(request, "orderbook", null, null, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToHistory(de._125m125.kt.ktapi_java.core.NotificationListener)
     */
    @Override
    public void subscribeToHistory(final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData("history");
        subscribe(request, "history", null, null, listener);
    }

    /**
     * Subscribe to an event channel with a given key.
     *
     * @param request
     *            the subscription request that should be sent to the server
     * @param source
     *            the channel of the events
     * @param key
     *            the key for events. null means that all events on the channel
     *            should be passed to the listener
     * @param owner
     *            the authentification details required to subscribe to the
     *            channel
     * @param listener
     *            the listener that should be notified on new events
     * @return the response message from the server
     */
    public ResponseMessage subscribe(final SubscriptionRequestData request, final String source, final String key,
            final User owner, final NotificationListener listener) {
        if (this.useSession) {
            checkSession();
        }
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

    /**
     * Check for an existing session and start a new session if required.
     */
    private void checkSession() {
        if (!this.useSession || this.sessionId != null) {
            return;
        }
        synchronized (this) {
            if (this.sessionId != null) {
                return;
            }
            final Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("session", SessionRequestData.createStartRequest());
            try {
                final ResponseMessage responseMessage = sendAndAwait(requestMap);
                if (!(responseMessage instanceof SessionResponse)) {
                    return;
                }
                this.sessionId = ((SessionResponse) responseMessage).getSessionDetails().getId();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resume the previous session.
     *
     * @return true, if the session was resumed successfully
     */
    private boolean resumeSession() {
        if (!this.useSession) {
            return false;
        }
        synchronized (this) {
            if (this.sessionId == null) {
                return false;
            }
            final Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("session", SessionRequestData.createResumtionRequest(this.sessionId));
            try {
                final ResponseMessage responseMessage = sendAndAwait(requestMap);
                if (!(responseMessage instanceof SessionResponse)) {
                    responseMessage.getError().filter("unknownSessionId"::equals)
                            .ifPresent(msg -> this.sessionId = null);
                    return false;
                }
                return true;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#disconnect()
     */
    @Override
    public void disconnect() {
        stop();
    }

    /**
     * Should be called when the websocket closed.
     *
     * @param reconnect
     *            true, if a reconnection attempt should be started
     */
    public synchronized void onClose(final boolean reconnect) {
        if (this.active && reconnect) {
            reConnectDelayed();
        }
    }

    /**
     * Should be called, when a websocket was opened
     */
    protected void onOpen() {
        this.lastDelay = 0;
        synchronized (this.subscriptions) {
            boolean sessionResumed = false;
            if (this.useSession) {
                sessionResumed = sessionResumed || resumeSession();
            }
        }
    }

    /**
     * Should be called when the server sent a message
     *
     * @param message
     *            the message
     */
    public void onMessage(final String message) {
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

    /**
     * Send message.
     *
     * @param message
     *            the message
     */
    public abstract void sendMessage(final String message) throws IOException;

    /**
     * Send.
     *
     * @param data
     *            the data
     * @param callback
     *            the callback
     */
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

    /**
     * Send a message to the server and wait for a response
     *
     * @param data
     *            the data to send to the server
     * @return the response message from the server
     * @throws InterruptedException
     *             if the current thread was interrupted while waiting
     * @throws ExecutionException
     *             if the request completed exceptionally
     */
    public ResponseMessage sendAndAwait(final Object data) throws InterruptedException, ExecutionException {
        final CompletableFuture<ResponseMessage> cf = new CompletableFuture<>();
        send(data, cf::complete);
        return cf.get();
    }

    public void setManager(final KtWebsocketManager manager) {

    }

}
