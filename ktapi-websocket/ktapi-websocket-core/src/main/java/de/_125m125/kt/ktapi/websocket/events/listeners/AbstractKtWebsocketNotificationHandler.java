/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Entity;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.User;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.exceptions.SubscriptionRefusedException;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestDataFactory;

public abstract class AbstractKtWebsocketNotificationHandler<U>
        implements KtNotificationManager<U> {

    private final Logger                         logger;
    protected KtUserStore                        userStore;
    private KtWebsocketManager                   manager;
    public final VerificationMode                mode;
    private final SubscriptionRequestDataFactory subscriptionRequestDataFactory;
    private final Set<ChannelIdentifier>         knownUsers = new HashSet<>();

    public AbstractKtWebsocketNotificationHandler(final Logger logger, final KtUserStore userStore,
            final VerificationMode mode,
            final SubscriptionRequestDataFactory subscriptionRequestDataFactory) {
        this.logger = Objects.requireNonNull(logger);
        this.userStore = Objects.requireNonNull(userStore);
        this.mode = Objects.requireNonNull(mode);
        this.subscriptionRequestDataFactory = Objects
                .requireNonNull(subscriptionRequestDataFactory);
    }

    public synchronized KtWebsocketManager getManager() {
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
     * @param priority
     *            the priority
     * @return the response message from the server
     */
    public CompletableFuture<U> subscribe(final SubscriptionRequestData request,
            final String source, final String key, final UserKey owner,
            final NotificationListener listener, final Priority priority) {
        final CompletableFuture<U> result = new CompletableFuture<>();
        try {
            final KtWebsocketManager manager = getManager();
            if (manager == null) {
                this.logger.error("tried to subscribe to events before "
                        + "NofiticationListener was fully initialized");
                result.completeExceptionally(
                        new IllegalStateException("the notification manager first has to "
                                + "be assigned to a KtWebsocketmanager"));
            }
            final ChannelIdentifier userKey = new ChannelIdentifier(request);
            if (this.knownUsers.contains(userKey)) {
                addListener(request, source, key, listener, result, priority);
                return result;
            }
            final RequestMessage requestMessage = RequestMessage.builder().addContent(request)
                    .build();
            this.logger.trace("adding listener {} to {}.{}", listener, source, key);
            manager.sendRequest(requestMessage);
            requestMessage.getResult().addCallback(responseMessage -> {
                if (responseMessage.success()) {
                    addListener(request, source, key, listener, result, priority);
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
            NotificationListener listener, CompletableFuture<U> result, Priority priority);

    protected boolean addKnownUser(final ChannelIdentifier identifier) {
        return this.knownUsers.add(identifier);
    }

    @Override
    public CompletableFuture<U> subscribeToMessages(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated, final Priority priority) {
        final SubscriptionRequestData request = this.subscriptionRequestDataFactory
                .createSubscriptionRequestData("rMessages", this.userStore.get(userKey),
                        selfCreated);
        return subscribe(request, Entity.MESSAGE.getUpdateChannel(), userKey.getUserId(), userKey,
                listener, priority);
    }

    @Override
    public CompletableFuture<U> subscribeToTrades(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated, final Priority priority) {
        final SubscriptionRequestData request = this.subscriptionRequestDataFactory
                .createSubscriptionRequestData("rOrders", this.userStore.get(userKey), selfCreated);
        return subscribe(request, Entity.TRADE.getUpdateChannel(), userKey.getUserId(), userKey,
                listener, priority);
    }

    @Override
    public CompletableFuture<U> subscribeToItems(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated, final Priority priority) {
        final SubscriptionRequestData request = this.subscriptionRequestDataFactory
                .createSubscriptionRequestData("rItems", this.userStore.get(userKey), selfCreated);
        return subscribe(request, Entity.ITEM.getUpdateChannel(), userKey.getUserId(), userKey,
                listener, priority);
    }

    @Override
    public CompletableFuture<U> subscribeToPayouts(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated, final Priority priority) {
        final SubscriptionRequestData request = this.subscriptionRequestDataFactory
                .createSubscriptionRequestData("rPayouts", this.userStore.get(userKey),
                        selfCreated);
        return subscribe(request, Entity.PAYOUT.getUpdateChannel(), userKey.getUserId(), userKey,
                listener, priority);
    }

    @Override
    public CompletableFuture<U> subscribeToOrderbook(final NotificationListener listener,
            final Priority priority) {
        final SubscriptionRequestData request = new SubscriptionRequestData(
                Entity.ORDERBOOK_ENTRY.getUpdateChannel());
        return subscribe(request, Entity.ORDERBOOK_ENTRY.getUpdateChannel(), null, null, listener,
                priority);
    }

    @Override
    public CompletableFuture<U> subscribeToHistory(final NotificationListener listener,
            final Priority priority) {
        final SubscriptionRequestData request = new SubscriptionRequestData(
                Entity.HISTORY_ENTRY.getUpdateChannel());
        return subscribe(request, Entity.HISTORY_ENTRY.getUpdateChannel(), null, null, listener,
                priority);
    }

    @Override
    public void disconnect() {
        this.logger.info("disconnecting...");
        getManager().stop();
        this.logger.info("disconnected");
    }

    protected class ChannelIdentifier {
        private final String channel;
        private final User   user;

        public ChannelIdentifier(final String channel, final User user) {
            super();
            this.channel = channel;
            this.user = user;
        }

        public ChannelIdentifier(final SubscriptionRequestData request) {
            this(request.getChannel(), request.getUser());
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
            default:
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
                default:
                    return false;
                }
            }
            return true;
        }

        private AbstractKtWebsocketNotificationHandler<U> getOuterType() {
            return AbstractKtWebsocketNotificationHandler.this;
        }
    }

    @WebsocketEventListening
    public abstract void onMessageReceived(final MessageReceivedEvent e);
}
