package de._125m125.kt.websocket.reactive;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.VerificationMode;
import de._125m125.kt.ktapi.websocket.requests.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.responses.UpdateNotification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ReactiveKtWebsocketNotificationHandler<T extends TokenUserKey>
        extends AbstractKtWebsocketNotificationHandler<T, Disposable> {
    private static final Logger                    logger  = LoggerFactory
            .getLogger(ReactiveKtWebsocketNotificationHandler.class);
    protected final Subject<UpdateNotification<?>> subject = PublishSubject
            .<UpdateNotification<?>>create().toSerialized();

    public ReactiveKtWebsocketNotificationHandler(final KtUserStore userStore,
            final VerificationMode mode) {
        super(ReactiveKtWebsocketNotificationHandler.logger, userStore, mode);
    }

    @Override
    public void unsubscribe(final Disposable listener) {
        listener.dispose();
    }

    @Override
    protected void addListener(final SubscriptionRequestData request, final String source,
            final String key, final NotificationListener listener,
            final CompletableFuture<Disposable> result) {
        Observable<UpdateNotification<?>> filter = this.subject
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
        this.subject.onComplete();
    }

    @Override
    @WebsocketEventListening
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getMessage() instanceof UpdateNotification) {
            ReactiveKtWebsocketNotificationHandler.logger.trace("Received UpdateNotification {}",
                    e.getMessage());
            this.subject.onNext((UpdateNotification<?>) e.getMessage());
        }
    }

    public Observable<HistoryEntry> getHistoryObservable() {
        return getObservable(AbstractKtWebsocketNotificationHandler.HISTORY, HistoryEntry.class);
    }

    // public Observable<HistoryEntry> getHistoryObservable(String itemId) {
    // return getObservable(AbstractKtWebsocketNotificationHandler.HISTORY, HistoryEntry.class);
    // }

    public Observable<Item> getItemObservable() {
        return getObservable(AbstractKtWebsocketNotificationHandler.ITEMS, Item.class);
    }

    public Observable<Item> getItemObservable(final T user) {
        return getObservable(AbstractKtWebsocketNotificationHandler.ITEMS, Item.class, user);
    }

    public Observable<Item> getItemObservable(final String itemId) {
        return getObservable(AbstractKtWebsocketNotificationHandler.ITEMS, Item.class)
                .filter(i -> itemId.equals(i.getId()));
    }

    public Observable<Item> getItemObservable(final T user, final String itemId) {
        return getObservable(AbstractKtWebsocketNotificationHandler.ITEMS, Item.class, user)
                .filter(i -> itemId.equals(i.getId()));
    }

    public Observable<Message> getMessageObservable() {
        return getObservable(AbstractKtWebsocketNotificationHandler.MESSAGES, Message.class);
    }

    public Observable<Message> getMessageObservable(final T user) {
        return getObservable(AbstractKtWebsocketNotificationHandler.MESSAGES, Message.class, user);
    }

    public Observable<OrderBookEntry> getOrderbookObservable() {
        return getObservable(AbstractKtWebsocketNotificationHandler.ORDERBOOK,
                OrderBookEntry.class);
    }

    // public Observable<OrderBookEntry> getOrderbookObservable(String itemId) {
    // return getObservable(AbstractKtWebsocketNotificationHandler.ORDERBOOK, OrderBookEntry.class);
    // }

    public Observable<Payout> getPayoutObservable() {
        return getObservable(AbstractKtWebsocketNotificationHandler.PAYOUTS, Payout.class);
    }

    public Observable<Payout> getPayoutObservable(final T user) {
        return getObservable(AbstractKtWebsocketNotificationHandler.PAYOUTS, Payout.class, user);
    }

    public Observable<Payout> getPayoutObservable(final long payoutId) {
        return getObservable(AbstractKtWebsocketNotificationHandler.PAYOUTS, Payout.class)
                .filter(p -> payoutId == p.getId()).takeUntil(p -> p.getState() == "SUCCESS"
                        || p.getState() == "CANCELLED" || p.getState() == "FAILED_TAKEN");
    }

    public Observable<Trade> getTradeObservable() {
        return getObservable(AbstractKtWebsocketNotificationHandler.TRADES, Trade.class);
    }

    public Observable<Trade> getTradeObservable(final T user) {
        return getObservable(AbstractKtWebsocketNotificationHandler.TRADES, Trade.class, user);
    }

    public Observable<Trade> getTradeObservable(final long tradeId) {
        return getObservable(AbstractKtWebsocketNotificationHandler.TRADES, Trade.class)
                .filter(n -> tradeId == n.getId()).takeUntil(n -> n.getAmount() > n.getSold()
                        || n.getToTakeItems() > 0 || n.getToTakeMoney() > 0);
    }

    private <U> Observable<U> getObservable(final String type, final Class<U> t) {
        if (AbstractKtWebsocketNotificationHandler.types.get(type) != t) {
            throw new IllegalArgumentException("type " + type + " does not map to " + t);
        }
        return this.subject.filter(n -> type.equals(n.getSource()))
                .flatMap(n -> Observable.fromArray(n.getChangedEntries())).map(t::cast);
    }

    private <U> Observable<U> getObservable(final String type, final Class<U> t, final T user) {
        if (AbstractKtWebsocketNotificationHandler.types.get(type) != t) {
            throw new IllegalArgumentException("type " + type + " does not map to " + t);
        }
        return this.subject.filter(n -> type.equals(n.getSource()))
                .filter(n -> user.getUserId().equals(n.getKey()))
                .flatMap(n -> Observable.fromArray(n.getChangedEntries())).map(t::cast);
    }

}
