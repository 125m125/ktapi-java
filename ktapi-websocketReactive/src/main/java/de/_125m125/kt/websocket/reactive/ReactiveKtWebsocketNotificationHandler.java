package de._125m125.kt.websocket.reactive;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Entity;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.VerificationMode;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestDataFactory;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ReactiveKtWebsocketNotificationHandler
        extends AbstractKtWebsocketNotificationHandler<Disposable> {
    private static final Logger logger = LoggerFactory
            .getLogger(ReactiveKtWebsocketNotificationHandler.class);

    public static Subject<UpdateNotification<?>> createSubject() {
        return PublishSubject.<UpdateNotification<?>>create().toSerialized();
    }

    protected final Map<Priority, Subject<UpdateNotification<?>>> subjects;

    public ReactiveKtWebsocketNotificationHandler(final KtUserStore userStore,
            final VerificationMode mode) {
        this(userStore, mode, new SubscriptionRequestDataFactory());
    }

    public ReactiveKtWebsocketNotificationHandler(final KtUserStore userStore,
            final VerificationMode mode,
            final SubscriptionRequestDataFactory subscriptionRequestDataFactory) {
        super(ReactiveKtWebsocketNotificationHandler.logger, userStore, mode,
                subscriptionRequestDataFactory);
        this.subjects = Collections.synchronizedMap(new EnumMap<>(Priority.class));
    }

    @Override
    public void unsubscribe(final Disposable listener) {
        listener.dispose();
    }

    @Override
    protected void addListener(final SubscriptionRequestData request, final String source,
            final String key, final NotificationListener listener,
            final CompletableFuture<Disposable> result, final Priority priority) {
        final Subject<UpdateNotification<?>> subject = this.subjects.computeIfAbsent(priority,
                p -> createSubject());
        Observable<UpdateNotification<?>> filter = subject
                .filter(n -> source.equals(n.getSource()));
        if (key != null) {
            filter = filter.filter(n -> key.equals(n.getKey()));
        }
        filter = filter.filter(n -> request.isSelfCreated() == n.isSelfCreated());
        result.complete(filter.subscribe(listener::update));
    }

    @Override
    public void disconnect() {
        super.disconnect();
        this.subjects.values().forEach(Subject::onComplete);
    }

    @Override
    @WebsocketEventListening
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getMessage() instanceof UpdateNotification) {
            ReactiveKtWebsocketNotificationHandler.logger.trace("Received UpdateNotification {}",
                    e.getMessage());
            this.subjects.values().forEach(s -> s.onNext((UpdateNotification<?>) e.getMessage()));
        }
    }

    public Observable<HistoryEntry> getHistoryObservable(final Priority priority) {
        return getObservable(Entity.HISTORY_ENTRY, HistoryEntry.class, priority);
    }

    // public Observable<HistoryEntry> getHistoryObservable(String itemId) {
    // return getObservable(AbstractKtWebsocketNotificationHandler.HISTORY, HistoryEntry.class);
    // }

    public Observable<Item> getItemObservable(final Priority priority) {
        return getObservable(Entity.ITEM, Item.class, priority);
    }

    public Observable<Item> getItemObservable(final UserKey userkey, final Priority priority) {
        return getObservable(Entity.ITEM, Item.class, userkey, priority);
    }

    public Observable<Item> getItemObservable(final String itemId, final Priority priority) {
        return getObservable(Entity.ITEM, Item.class, priority)
                .filter(i -> itemId.equals(i.getId()));
    }

    public Observable<Item> getItemObservable(final UserKey userkey, final String itemId,
            final Priority priority) {
        return getObservable(Entity.ITEM, Item.class, userkey, priority)
                .filter(i -> itemId.equals(i.getId()));
    }

    public Observable<Message> getMessageObservable(final Priority priority) {
        return getObservable(Entity.MESSAGE, Message.class, priority);
    }

    public Observable<Message> getMessageObservable(final UserKey userkey,
            final Priority priority) {
        return getObservable(Entity.MESSAGE, Message.class, userkey, priority);
    }

    public Observable<OrderBookEntry> getOrderbookObservable(final Priority priority) {
        return getObservable(Entity.ORDERBOOK_ENTRY, OrderBookEntry.class, priority);
    }

    // public Observable<OrderBookEntry> getOrderbookObservable(String itemId) {
    // return getObservable(AbstractKtWebsocketNotificationHandler.ORDERBOOK, OrderBookEntry.class);
    // }

    public Observable<Payout> getPayoutObservable(final Priority priority) {
        return getObservable(Entity.PAYOUT, Payout.class, priority);
    }

    public Observable<Payout> getPayoutObservable(final UserKey userkey, final Priority priority) {
        return getObservable(Entity.PAYOUT, Payout.class, userkey, priority);
    }

    public Observable<Payout> getPayoutObservable(final long payoutId, final Priority priority) {
        return getObservable(Entity.PAYOUT, Payout.class, priority)
                .filter(p -> payoutId == p.getId()).takeUntil(p -> "SUCCESS".equals(p.getState())
                        || "CANCELLED".equals(p.getState()) || "FAILED_TAKEN".equals(p.getState()));
    }

    public Observable<Trade> getTradeObservable(final Priority priority) {
        return getObservable(Entity.TRADE, Trade.class, priority);
    }

    public Observable<Trade> getTradeObservable(final UserKey userkey, final Priority priority) {
        return getObservable(Entity.TRADE, Trade.class, userkey, priority);
    }

    public Observable<Trade> getTradeObservable(final long tradeId, final Priority priority) {
        return getObservable(Entity.TRADE, Trade.class, priority).filter(n -> tradeId == n.getId())
                .takeUntil(n -> n.getAmount() == n.getSold() && n.getToTakeItems() == 0
                        && n.getToTakeMoney() == 0);
    }

    private <U> Observable<U> getObservable(final Entity type, final Class<U> t,
            final Priority priority) {
        if (type.getInstanceClass() != t) {
            throw new IllegalArgumentException("type " + type + " does not map to " + t);
        }
        return this.subjects.computeIfAbsent(priority, p -> createSubject())
                .filter(n -> type.getUpdateChannel().equals(n.getSource()))
                .flatMap(n -> Observable.fromArray(n.getChangedEntries())).map(t::cast);
    }

    private <U> Observable<U> getObservable(final Entity type, final Class<U> t,
            final UserKey userkey, final Priority priority) {
        if (type.getInstanceClass() != t) {
            throw new IllegalArgumentException("type " + type + " does not map to " + t);
        }
        return this.subjects.computeIfAbsent(priority, p -> createSubject())
                .filter(n -> type.getUpdateChannel().equals(n.getSource()))
                .filter(n -> userkey.getUserId().equals(n.getKey()))
                .flatMap(n -> Observable.fromArray(n.getChangedEntries())).map(t::cast);
    }

}
