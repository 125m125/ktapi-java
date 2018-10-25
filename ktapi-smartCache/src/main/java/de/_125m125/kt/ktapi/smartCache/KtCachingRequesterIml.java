package de._125m125.kt.ktapi.smartCache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de._125m125.kt.ktapi.core.BUY_SELL;
import de._125m125.kt.ktapi.core.BUY_SELL_BOTH;
import de._125m125.kt.ktapi.core.KtCachingRequester;
import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.PAYOUT_TYPE;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;
import de._125m125.kt.ktapi.smartCache.objects.TimestampedObjectFactory;

/**
 *
 */
public class KtCachingRequesterIml<U extends UserKey<?>>
        implements KtRequester<U>, NotificationListener, KtCachingRequester<U> {

    public static final int                 CACHE_HIT_STATUS_CODE = 299;

    private static final String             ITEMS                 = "items-";
    private static final String             TRADES                = "trades-";
    private static final String             PAYOUTS               = "payouts-";
    private static final String             MESSAGES              = "messages-";
    private static final String             ORDERBOOK             = "orderbook-";
    private static final String             HISTORY               = "history-";

    private final Map<String, CacheData<?>> cache;
    private final KtRequester<U>            requester;
    private KtNotificationManager<U>        ktNotificationManager;
    private final TimestampedObjectFactory  factory;

    public KtCachingRequesterIml(final KtRequester<U> requester,
            final KtNotificationManager<U> ktNotificationManager) {
        this(requester, ktNotificationManager, null);
    }

    public KtCachingRequesterIml(final KtRequester<U> requester,
            final KtNotificationManager<U> ktNotificationManager,
            final TimestampedObjectFactory factory) {
        this.ktNotificationManager = ktNotificationManager;
        this.cache = new ConcurrentHashMap<>();
        this.requester = requester;
        this.factory = factory != null ? factory : new TimestampedObjectFactory();
    }

    @Override
    public void invalidateHistory(final String itemid) {
        invalidate(KtCachingRequesterIml.HISTORY + itemid);
    }

    @Override
    public void invalidateOrderBook(final String itemid) {
        invalidate(KtCachingRequesterIml.ORDERBOOK + itemid);
    }

    @Override
    public void invalidateMessages(final U userKey) {
        invalidate(KtCachingRequesterIml.MESSAGES + userKey.getUserId());
    }

    @Override
    public void invalidatePayouts(final U userKey) {
        invalidate(KtCachingRequesterIml.PAYOUTS + userKey.getUserId());
    }

    @Override
    public void invalidateTrades(final U userKey) {
        invalidate(KtCachingRequesterIml.TRADES + userKey.getUserId());
    }

    @Override
    public void invalidateItemList(final U userKey) {
        invalidate(KtCachingRequesterIml.ITEMS + userKey.getUserId());
    }

    private void invalidate(final String key) {
        final CacheData<?> cacheData = this.cache.get(key);
        if (cacheData != null) {
            cacheData.invalidate();
        }
    }

    @Override
    public boolean isValidHistory(final String itemid, final List<HistoryEntry> historyEntries) {
        return isValid(KtCachingRequesterIml.HISTORY + itemid, historyEntries);
    }

    @Override
    public boolean isValidOrderBook(final String itemid, final List<OrderBookEntry> orderBook) {
        return isValid(KtCachingRequesterIml.ORDERBOOK + itemid, orderBook);
    }

    @Override
    public boolean isValidMessageList(final U userKey, final List<Message> messages) {
        return isValid(KtCachingRequesterIml.MESSAGES + userKey.getUserId(), messages);
    }

    @Override
    public boolean isValidPayoutList(final U userKey, final List<Payout> payouts) {
        return isValid(KtCachingRequesterIml.PAYOUTS + userKey.getUserId(), payouts);
    }

    @Override
    public boolean isValidTradeList(final U userKey, final List<Trade> trades) {
        return isValid(KtCachingRequesterIml.TRADES + userKey.getUserId(), trades);
    }

    @Override
    public boolean isValidItemList(final U userKey, final List<Item> items) {
        return isValid(KtCachingRequesterIml.ITEMS + userKey.getUserId(), items);
    }

    private <T> boolean isValid(final String key, final List<T> historyEntries) {
        if (!(historyEntries instanceof TimestampedList)) {
            return false;
        }
        final CacheData<?> cacheData = this.cache.get(key);
        return cacheData != null && ((TimestampedList<?>) historyEntries)
                .getTimestamp() >= cacheData.getLastInvalidationTime();
    }

    @Override
    public void update(final Notification notification) {
        final String key = notification.getDetails().get("source") + "-"
                + notification.getDetails().get("key");
        invalidate(key);
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit,
            final int offset) {
        this.ktNotificationManager.subscribeToHistory(this);
        return getOrFetch(KtCachingRequesterIml.HISTORY + itemid, offset, offset + limit,
                () -> this.requester.getHistory(itemid, limit, offset));
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        this.ktNotificationManager.subscribeToHistory(this);
        return this.getOrFetch(KtCachingRequesterIml.HISTORY + itemid, 0,
                () -> this.requester.getLatestHistory(itemid));
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit,
            final BUY_SELL_BOTH mode, final boolean summarizeRemaining) {
        // TODO caching
        return this.requester.getOrderBook(itemid, limit, mode, summarizeRemaining);
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid,
            final BUY_SELL_BOTH mode) {
        // TODO caching
        return this.requester.getBestOrderBookEntries(itemid, mode);
    }

    @Override
    public Result<Permissions> getPermissions(final U userKey) {
        // TODO caching?
        return this.requester.getPermissions(userKey);
    }

    @Override
    public Result<List<Item>> getItems(final U userKey) {
        this.ktNotificationManager.subscribeToItems(this, userKey, false);
        this.ktNotificationManager.subscribeToItems(this, userKey, true);
        return getAllOrFetch(KtCachingRequesterIml.ITEMS + userKey.getUserId(),
                () -> this.requester.getItems(userKey));
    }

    @Override
    public Result<Item> getItem(final U userKey, final String itemid) {
        this.ktNotificationManager.subscribeToItems(this, userKey, false);
        this.ktNotificationManager.subscribeToItems(this, userKey, true);
        return getOrFetch(KtCachingRequesterIml.ITEMS + userKey.getUserId(),
                item -> item.getId().equals(itemid), () -> this.requester.getItem(userKey, itemid));
    }

    @Override
    public Result<List<Message>> getMessages(final U userKey) {
        this.ktNotificationManager.subscribeToMessages(this, userKey, false);
        this.ktNotificationManager.subscribeToMessages(this, userKey, true);
        return getAllOrFetch(KtCachingRequesterIml.MESSAGES + userKey.getUserId(),
                () -> this.requester.getMessages(userKey));
    }

    @Override
    public Result<List<Payout>> getPayouts(final U userKey) {
        this.ktNotificationManager.subscribeToPayouts(this, userKey, false);
        this.ktNotificationManager.subscribeToPayouts(this, userKey, true);
        return getAllOrFetch(KtCachingRequesterIml.PAYOUTS + userKey.getUserId(),
                () -> this.requester.getPayouts(userKey));
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final U userKey, final PAYOUT_TYPE type,
            final String itemid, final String amount) {
        final Result<WriteResult<Payout>> result = this.requester.createPayout(userKey, type,
                itemid, amount);
        result.addCallback(new InvalidationCallback<WriteResult<Payout>>(this.cache,
                KtCachingRequesterIml.PAYOUTS + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final U userKey, final long payoutid) {
        final Result<WriteResult<Payout>> result = this.requester.cancelPayout(userKey, payoutid);
        result.addCallback(new InvalidationCallback<WriteResult<Payout>>(this.cache,
                KtCachingRequesterIml.PAYOUTS + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final U userKey, final long payoutid) {
        final Result<WriteResult<Payout>> result = this.requester.takeoutPayout(userKey, payoutid);
        result.addCallback(new InvalidationCallback<WriteResult<Payout>>(this.cache,
                KtCachingRequesterIml.PAYOUTS + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<PusherResult> authorizePusher(final U userKey, final String channel_name,
            final String socketId) {
        return this.requester.authorizePusher(userKey, channel_name, socketId);
    }

    @Override
    public Result<List<Trade>> getTrades(final U userKey) {
        this.ktNotificationManager.subscribeToTrades(this, userKey, false);
        this.ktNotificationManager.subscribeToTrades(this, userKey, true);
        return getAllOrFetch(KtCachingRequesterIml.TRADES + userKey.getUserId(),
                () -> this.requester.getTrades(userKey));
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final U userKey, final BUY_SELL mode,
            final String item, final int amount, final String pricePerItem) {
        final Result<WriteResult<Trade>> result = this.requester.createTrade(userKey, mode, item,
                amount, pricePerItem);
        result.addCallback(new InvalidationCallback<WriteResult<Trade>>(this.cache,
                KtCachingRequesterIml.TRADES + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final U userKey, final long tradeId) {
        final Result<WriteResult<Trade>> result = this.requester.cancelTrade(userKey, tradeId);
        result.addCallback(new InvalidationCallback<WriteResult<Trade>>(this.cache,
                KtCachingRequesterIml.TRADES + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final U userKey, final long tradeId) {
        final Result<WriteResult<Trade>> result = this.requester.takeoutTrade(userKey, tradeId);
        result.addCallback(new InvalidationCallback<WriteResult<Trade>>(this.cache,
                KtCachingRequesterIml.TRADES + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<Long> ping() {
        return this.requester.ping();
    }

    private <T> Result<List<T>> getOrFetch(final String key, final int start, final int end,
            final Supplier<Result<List<T>>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                s -> new CacheData<T>());
        final Optional<TimestampedList<T>> all = cacheEntry.get(start, end);
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE, all.get());
        } else {
            return new ExposedResult<>(fetcher,
                    (status, result) -> cacheEntry.set(result, start, start + result.size()));
        }
    }

    private <T> Result<T> getOrFetch(final String key, final int index,
            final Supplier<Result<T>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                s -> new CacheData<T>());
        final Optional<T> all = cacheEntry.get(index);
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE,
                    KtCachingRequesterIml.this.factory.create(all.get(),
                            cacheEntry.getLastInvalidationTime(), true));
        } else {
            return new ExposedResult<>(fetcher, (status,result)->KtCachingRequesterIml.this.factory
                    .create(result, cacheEntry.getLastInvalidationTime(), false));
        }
    }

    private <T> Result<T> getOrFetch(final String key, final Predicate<T> index,
            final Supplier<Result<T>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                s -> new CacheData<T>());
        final Optional<T> all = cacheEntry.getAny(index);
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE,
                    KtCachingRequesterIml.this.factory.create(all.get(),
                            cacheEntry.getLastInvalidationTime(), true));
        } else {
            final ExposedResult<T> returnResult = new ExposedResult<>(fetcher,
                    (status, result) -> KtCachingRequesterIml.this.factory.create(result,
                            cacheEntry.getLastInvalidationTime(),
                            false));
            return returnResult;
        }
    }

    private <T> Result<List<T>> getAllOrFetch(final String key,
            final Supplier<Result<List<T>>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                s -> new CacheData<T>());
        final Optional<TimestampedList<T>> all = cacheEntry.getAll();
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE, all.get());
        } else {
            return new ExposedResult<>(fetcher, (status,result)->cacheEntry.set(result, 0,
                    result.size()));
        }
    }

    protected static class ExposedResult<T> extends Result<T> {

        public ExposedResult(final Supplier<Result<T>> fetcher, final BiFunction<Integer, T, T> success) {
            fetcher.get().addCallback(Callback.of(Optional.of((s, t) -> this.setSuccessResult(s, success.apply(s, t))),
                    Optional.of(this::setFailureResult), Optional.of(this::setErrorResult)));
        }

        @Override
        protected void setSuccessResult(final int status, final T content) {
            super.setSuccessResult(status, content);
        }

        @Override
        protected void setErrorResult(final Throwable t) {
            super.setErrorResult(t);
        }

        @Override
        protected void setFailureResult(final int status, final String errorMessage,
                final String humanReadableErrorMessage) {
            super.setFailureResult(status, errorMessage, humanReadableErrorMessage);
        }
    }

    @Override
    public void close() throws IOException {
        this.requester.close();
    }
}
