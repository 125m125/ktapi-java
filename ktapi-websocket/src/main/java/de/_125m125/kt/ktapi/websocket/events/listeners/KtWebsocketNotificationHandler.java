package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.SubscriptionList;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.exceptions.SubscriptionRefusedException;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.requests.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.responses.UpdateNotification;

public class KtWebsocketNotificationHandler<T extends TokenUserKey>
        implements KtNotificationManager<T> {

    private KtWebsocketManager                               manager;

    /**
     * all active subscriptions. First key: channel. Second key: key. Null in the second key means,
     * that the client is interested in all events on this channel.
     */
    private final Map<String, Map<String, SubscriptionList>> subscriptions = new HashMap<>();

    private final KtUserStore                                userStore;

    public KtWebsocketNotificationHandler(final KtUserStore userStore) {
        this.userStore = userStore;
    }

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException(
                    "each session handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
    }

    @WebsocketEventListening
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getMessage() instanceof UpdateNotification) {
            SubscriptionList keyList = null;
            SubscriptionList unkeyedList = null;
            final UpdateNotification notificationMessage = (UpdateNotification) e.getMessage();
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
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToMessages(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.userKey, boolean)
     */
    @Override
    public CompletableFuture<NotificationListener> subscribeToMessages(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rMessages",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "messages", userKey.getUserId(), userKey, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToTrades(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.userKey, boolean)
     */
    @Override
    public CompletableFuture<NotificationListener> subscribeToTrades(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rOrders",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "trades", userKey.getUserId(), userKey, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToItems(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.userKey, boolean)
     */
    @Override
    public CompletableFuture<NotificationListener> subscribeToItems(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        System.out.println(this.userStore.get(userKey));
        final SubscriptionRequestData request = new SubscriptionRequestData("rItems",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "items", userKey.getUserId(), userKey, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToPayouts(de._125m125.kt.ktapi_java.core.NotificationListener, de._125m125.kt.ktapi_java.core.entities.userKey, boolean)
     */
    @Override
    public CompletableFuture<NotificationListener> subscribeToPayouts(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rPayouts",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "payouts", userKey.getUserId(), userKey, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToOrderbook(de._125m125.kt.ktapi_java.core.NotificationListener)
     */
    @Override
    public CompletableFuture<NotificationListener> subscribeToOrderbook(
            final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData("orderbook");
        return subscribe(request, "orderbook", null, null, listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#subscribeToHistory(de._125m125.kt.ktapi_java.core.NotificationListener)
     */
    @Override
    public CompletableFuture<NotificationListener> subscribeToHistory(
            final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData("history");
        return subscribe(request, "history", null, null, listener);
    }

    /**
     * Subscribe to an event channel with a given key.
     *
     * @param request
     *            the subscription request that should be sent to the server
     * @param source
     *            the channel of the events
     * @param key
     *            the key for events. null means that all events on the channel should be passed to
     *            the listener
     * @param owner
     *            the authentification details required to subscribe to the channel
     * @param listener
     *            the listener that should be notified on new events
     * @return the response message from the server
     */
    public CompletableFuture<NotificationListener> subscribe(final SubscriptionRequestData request,
            final String source, final String key, final T owner,
            final NotificationListener listener) {
        final CompletableFuture<NotificationListener> result = new CompletableFuture<>();
        try {
            KtWebsocketManager manager = this.manager;
            if (manager == null) {
                synchronized (this) {
                    manager = this.manager;
                }
            }
            if (manager == null) {
                throw new IllegalStateException(
                        "the notification manager first has to be assigned to a KtWebsocketmanager");
            }
            final RequestMessage requestMessage = RequestMessage.builder().addContent(request)
                    .build();
            this.manager.sendRequest(requestMessage);
            requestMessage.getResult().addCallback(responseMessage -> {
                if (responseMessage.success()) {
                    SubscriptionList subList;
                    synchronized (this.subscriptions) {
                        subList = this.subscriptions.computeIfAbsent(source, n -> new HashMap<>())
                                .computeIfAbsent(key,
                                        n -> new SubscriptionList(this.userStore.get(owner)));
                    }
                    subList.addListener(listener, request.isSelfCreated());
                    result.complete(listener);
                } else {
                    final Throwable exception = responseMessage.getErrorCause()
                            .orElseGet(() -> new SubscriptionRefusedException(
                                    responseMessage.getError().orElse("unknown")));
                    result.completeExceptionally(exception);
                }
            });
        } catch (final Throwable th) {
            result.completeExceptionally(th);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.core.KtNotificationManager#disconnect()
     */
    @Override
    public void disconnect() {
        this.manager.stop();
    }

    @Override
    public void unsubscribe(final NotificationListener listener) {
        synchronized (this.subscriptions) {
            this.subscriptions.values()
                    .forEach(m -> m.values().forEach(sl -> sl.removeListener(listener)));
        }
    }
}
