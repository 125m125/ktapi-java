package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
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
    private static final Logger                              logger        = LoggerFactory
            .getLogger(KtWebsocketNotificationHandler.class);

    private KtWebsocketManager                               manager;

    /**
     * all active subscriptions. First key: channel. Second key: key. Null in the second key means,
     * that the client is interested in all events on this channel.
     */
    private final Map<String, Map<String, SubscriptionList>> subscriptions = new HashMap<>();
    private final Set<ChannelIdentifier>                     knownUsers    = new HashSet<>();
    private VerificationMode                                 mode;

    private final KtUserStore                                userStore;

    public KtWebsocketNotificationHandler(final KtUserStore userStore) {
        this(userStore, VerificationMode.UNKNOWN_TKN);
    }

    public KtWebsocketNotificationHandler(final KtUserStore userStore,
            final VerificationMode mode) {
        this.userStore = userStore;
        this.mode = mode;

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
            KtWebsocketNotificationHandler.logger.trace("Received UpdateNotification {}",
                    e.getMessage());
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
            } else {
                KtWebsocketNotificationHandler.logger.debug(
                        "No listeners found for notifications on {}.*",
                        notificationMessage.getSource());
            }
            if (unkeyedList != null) {
                unkeyedList.notifyListeners(notificationMessage);
            } else {
                KtWebsocketNotificationHandler.logger.debug(
                        "No listeners found for notifications on {}.{}",
                        notificationMessage.getSource(), notificationMessage.getKey());
            }
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToMessages(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rMessages",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "messages", userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToTrades(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rOrders",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "trades", userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToItems(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rItems",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "items", userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToPayouts(
            final NotificationListener listener, final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rPayouts",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, "payouts", userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToOrderbook(
            final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData("orderbook");
        return subscribe(request, "orderbook", null, null, listener);
    }

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
                KtWebsocketNotificationHandler.logger.error(
                        "tried to subscribe to events before NofiticationListener was fully initialized");
                throw new IllegalStateException(
                        "the notification manager first has to be assigned to a KtWebsocketmanager");
            }
            final ChannelIdentifier userKey = new ChannelIdentifier(request);
            if (this.knownUsers.contains(userKey)) {
                addListener(request, source, key, listener, result, userKey);
                return result;
            }
            final RequestMessage requestMessage = RequestMessage.builder().addContent(request)
                    .build();
            KtWebsocketNotificationHandler.logger.trace("adding listener {} to {}.{}", listener,
                    source, key);
            this.manager.sendRequest(requestMessage);
            requestMessage.getResult().addCallback(responseMessage -> {
                if (responseMessage.success()) {
                    addListener(request, source, key, listener, result, userKey);
                } else {
                    final Throwable exception = responseMessage.getErrorCause()
                            .orElseGet(() -> new SubscriptionRefusedException(
                                    responseMessage.getError().orElse("unknown")));
                    result.completeExceptionally(exception);
                    KtWebsocketNotificationHandler.logger.warn("failed to add listener {} to {}.{}",
                            listener, source, key, exception);
                }
            });
        } catch (final Throwable th) {
            KtWebsocketNotificationHandler.logger.warn(
                    "unexpected exception while trying to add listener {} to {}.{}", listener,
                    source, key, th);
            result.completeExceptionally(th);
        }
        return result;
    }

    private void addListener(final SubscriptionRequestData request, final String source,
            final String key, final NotificationListener listener,
            final CompletableFuture<NotificationListener> result, final ChannelIdentifier userKey) {
        final SubscriptionList subList;
        synchronized (this.subscriptions) {
            subList = this.subscriptions.computeIfAbsent(source, n -> new HashMap<>())
                    .computeIfAbsent(key, n -> new SubscriptionList());
        }
        subList.addListener(listener, request.isSelfCreated());
        if (!VerificationMode.ALWAYS.equals(this.mode)) {
            this.knownUsers.add(userKey);
        }
        result.complete(listener);
        KtWebsocketNotificationHandler.logger.info("successfully added listener {} to {}.{}",
                listener, source, key);
        KtWebsocketNotificationHandler.logger.debug("new listener map: {}", this.subscriptions);
    }

    @Override
    public void disconnect() {
        KtWebsocketNotificationHandler.logger.info("disconnecting...");
        this.manager.stop();
        KtWebsocketNotificationHandler.logger.info("disconnected");
    }

    @Override
    public void unsubscribe(final NotificationListener listener) {
        synchronized (this.subscriptions) {
            this.subscriptions.values()
                    .forEach(m -> m.values().forEach(sl -> sl.removeListener(listener)));
            KtWebsocketNotificationHandler.logger.info("unsubscribed listener {}", listener);
            KtWebsocketNotificationHandler.logger.debug("new listener map: {}", this.subscriptions);
        }
    }

    private class ChannelIdentifier {
        private final String    channel;
        private final TokenUser user;

        public ChannelIdentifier(final String channel, final TokenUser user) {
            super();
            this.channel = channel;
            this.user = user;
        }

        public ChannelIdentifier(final SubscriptionRequestData request) {
            this(request.getChannel(),
                    new TokenUser(request.getUid(), request.getTid(), request.getTkn()));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.channel == null) ? 0 : this.channel.hashCode());
            switch (KtWebsocketNotificationHandler.this.mode) {
            case UNKNOWN_UID:
                result = prime * result
                        + ((this.user == null) ? 0 : this.user.getUserId().hashCode());
                break;
            case UNKNOWN_TKN:
                result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
                break;
            case ALWAYS:
                return 0;
            }
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            @SuppressWarnings("unchecked")
            final ChannelIdentifier other = (ChannelIdentifier) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.channel == null) {
                if (other.channel != null) {
                    return false;
                }
            } else if (!this.channel.equals(other.channel)) {
                return false;
            }
            if (this.user == null) {
                if (other.user != null) {
                    return false;
                }
            } else {
                switch (KtWebsocketNotificationHandler.this.mode) {
                case UNKNOWN_UID:
                    if (!this.user.getUserId().equals(other.user.getUserId())) {
                        return false;
                    }
                    break;
                case UNKNOWN_TKN:
                    if (!this.user.equals(other.user)) {
                        return false;
                    }
                    break;
                case ALWAYS:
                    return false;
                }
            }
            return true;
        }

        private KtWebsocketNotificationHandler<T> getOuterType() {
            return KtWebsocketNotificationHandler.this;
        }

    }
}
