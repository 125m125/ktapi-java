package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.exceptions.SubscriptionRefusedException;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.requests.SubscriptionRequestData;

public abstract class AbstractKtWebsocketNotificationHandler<T extends TokenUserKey, U>
        implements KtNotificationManager<T, U> {

    public static final String                HISTORY   = "history";
    public static final String                ITEMS     = "items";
    public static final String                MESSAGES  = "messages";
    public static final String                ORDERBOOK = "orderbook";
    public static final String                PAYOUTS   = "payouts";
    public static final String                TRADES    = "trades";

    public static final Map<String, Class<?>> types     = new HashMap<>();

    static {
        AbstractKtWebsocketNotificationHandler.types
                .put(AbstractKtWebsocketNotificationHandler.HISTORY, HistoryEntry.class);
        AbstractKtWebsocketNotificationHandler.types
                .put(AbstractKtWebsocketNotificationHandler.ITEMS, Item.class);
        AbstractKtWebsocketNotificationHandler.types
                .put(AbstractKtWebsocketNotificationHandler.MESSAGES, Message.class);
        AbstractKtWebsocketNotificationHandler.types
                .put(AbstractKtWebsocketNotificationHandler.ORDERBOOK, OrderBookEntry.class);
        AbstractKtWebsocketNotificationHandler.types
                .put(AbstractKtWebsocketNotificationHandler.PAYOUTS, Payout.class);
        AbstractKtWebsocketNotificationHandler.types
                .put(AbstractKtWebsocketNotificationHandler.TRADES, Trade.class);
    }

    private final Logger                 logger;
    private KtWebsocketManager           manager;
    private final Set<ChannelIdentifier> knownUsers = new HashSet<>();
    protected KtUserStore                userStore;
    public final VerificationMode        mode;

    public AbstractKtWebsocketNotificationHandler(final Logger logger, final KtUserStore userStore,
            final VerificationMode mode) {
        this.logger = logger;
        this.userStore = userStore;
        this.mode = mode;
    }

    public KtWebsocketManager getManager() {
        if (this.manager == null) {
            synchronized (this) {
                return this.manager;
            }
        }
        return this.manager;
    }

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (getManager() != null) {
            throw new IllegalStateException(
                    "each session handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
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
    public CompletableFuture<U> subscribe(final SubscriptionRequestData request,
            final String source, final String key, final T owner,
            final NotificationListener listener) {
        final CompletableFuture<U> result = new CompletableFuture<>();
        try {
            final KtWebsocketManager manager = getManager();
            if (manager == null) {
                this.logger.error(
                        "tried to subscribe to events before NofiticationListener was fully initialized");
                result.completeExceptionally(new IllegalStateException(
                        "the notification manager first has to be assigned to a KtWebsocketmanager"));
            }
            final ChannelIdentifier userKey = new ChannelIdentifier(request);
            if (this.knownUsers.contains(userKey)) {
                addListener(request, source, key, listener, result);
                return result;
            }
            final RequestMessage requestMessage = RequestMessage.builder().addContent(request)
                    .build();
            this.logger.trace("adding listener {} to {}.{}", listener, source, key);
            manager.sendRequest(requestMessage);
            requestMessage.getResult().addCallback(responseMessage -> {
                if (responseMessage.success()) {
                    addListener(request, source, key, listener, result);
                    if (!VerificationMode.ALWAYS.equals(this.mode)) {
                        addKnownUser(userKey);
                    }
                } else {
                    final Throwable exception = responseMessage.getErrorCause()
                            .orElseGet(() -> new SubscriptionRefusedException(
                                    responseMessage.getError().orElse("unknown")));
                    result.completeExceptionally(exception);
                    this.logger.warn("failed to add listener {} to {}.{}", listener, source, key,
                            exception);
                }
            });
        } catch (final Throwable th) {
            this.logger.warn("unexpected exception while trying to add listener {} to {}.{}",
                    listener, source, key, th);
            result.completeExceptionally(th);
        }
        return result;
    }

    protected abstract void addListener(SubscriptionRequestData request, String source, String key,
            NotificationListener listener, CompletableFuture<U> result);

    protected boolean addKnownUser(final ChannelIdentifier identifier) {
        return this.knownUsers.add(identifier);
    }

    @Override
    public CompletableFuture<U> subscribeToMessages(final NotificationListener listener,
            final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rMessages",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, AbstractKtWebsocketNotificationHandler.MESSAGES,
                userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<U> subscribeToTrades(final NotificationListener listener,
            final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rOrders",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, AbstractKtWebsocketNotificationHandler.TRADES,
                userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<U> subscribeToItems(final NotificationListener listener,
            final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rItems",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, AbstractKtWebsocketNotificationHandler.ITEMS, userKey.getUserId(),
                userKey, listener);
    }

    @Override
    public CompletableFuture<U> subscribeToPayouts(final NotificationListener listener,
            final T userKey, final boolean selfCreated) {
        final SubscriptionRequestData request = new SubscriptionRequestData("rPayouts",
                this.userStore.get(userKey), selfCreated);
        return subscribe(request, AbstractKtWebsocketNotificationHandler.PAYOUTS,
                userKey.getUserId(), userKey, listener);
    }

    @Override
    public CompletableFuture<U> subscribeToOrderbook(final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData(
                AbstractKtWebsocketNotificationHandler.ORDERBOOK);
        return subscribe(request, AbstractKtWebsocketNotificationHandler.ORDERBOOK, null, null,
                listener);
    }

    @Override
    public CompletableFuture<U> subscribeToHistory(final NotificationListener listener) {
        final SubscriptionRequestData request = new SubscriptionRequestData(
                AbstractKtWebsocketNotificationHandler.HISTORY);
        return subscribe(request, AbstractKtWebsocketNotificationHandler.HISTORY, null, null,
                listener);
    }

    @Override
    public void disconnect() {
        this.logger.info("disconnecting...");
        getManager().stop();
        this.logger.info("disconnected");
    }

    protected class ChannelIdentifier {
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
            switch (AbstractKtWebsocketNotificationHandler.this.mode) {
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
                switch (AbstractKtWebsocketNotificationHandler.this.mode) {
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

        private AbstractKtWebsocketNotificationHandler<T, U> getOuterType() {
            return AbstractKtWebsocketNotificationHandler.this;
        }
    }

    @WebsocketEventListening
    public abstract void onMessageReceived(final MessageReceivedEvent e);
}
