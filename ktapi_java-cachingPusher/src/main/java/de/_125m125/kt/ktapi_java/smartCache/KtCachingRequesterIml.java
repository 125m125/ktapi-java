package de._125m125.kt.ktapi_java.smartCache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.BUY_SELL_BOTH;
import de._125m125.kt.ktapi_java.core.KtCachingRequester;
import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.Notification;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.Callback;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.core.results.WriteResult;
import de._125m125.kt.ktapi_java.smartCache.objects.TimestampedList;
import de._125m125.kt.ktapi_java.smartCache.objects.TimestampedObjectFactory;

/**
 *
 */
public class KtCachingRequesterIml<U extends UserKey>
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
    private final TimestampedObjectFactory  factory;

    public KtCachingRequesterIml(final KtRequester<U> requester, final KtNotificationManager<U> ktNotificationManager) {
        this(requester, ktNotificationManager, new TimestampedObjectFactory());
    }

    public KtCachingRequesterIml(final KtRequester<U> requester, final KtNotificationManager<U> ktNotificationManager,
            final TimestampedObjectFactory factory) {
        this.cache = new ConcurrentHashMap<>();
        this.requester = requester;
        this.factory = factory != null ? factory : new TimestampedObjectFactory();

        // ktNotificationManager.subscribeToAll(this, false);
        // ktNotificationManager.subscribeToAll(this, true);
        ktNotificationManager.subscribeToAll(this);
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
    public void invalidateMessages(final U userid) {
        invalidate(KtCachingRequesterIml.MESSAGES + userid);
    }

    @Override
    public void invalidatePayouts(final U userid) {
        invalidate(KtCachingRequesterIml.PAYOUTS + userid);
    }

    @Override
    public void invalidateTrades(final U userid) {
        invalidate(KtCachingRequesterIml.TRADES + userid);
    }

    @Override
    public void invalidateItemList(final U userid) {
        invalidate(KtCachingRequesterIml.ITEMS + userid);
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
    public boolean isValidMessageList(final U userid, final List<Message> messages) {
        return isValid(KtCachingRequesterIml.MESSAGES + userid, messages);
    }

    @Override
    public boolean isValidPayoutList(final U userid, final List<Payout> payouts) {
        return isValid(KtCachingRequesterIml.PAYOUTS + userid, payouts);
    }

    @Override
    public boolean isValidTradeList(final U userid, final List<Trade> trades) {
        return isValid(KtCachingRequesterIml.TRADES + userid, trades);
    }

    @Override
    public boolean isValidItemList(final U userid, final List<Item> items) {
        return isValid(KtCachingRequesterIml.ITEMS + userid, items);
    }

    private <T> boolean isValid(final String key, final List<T> historyEntries) {
        if (!(historyEntries instanceof TimestampedList)) {
            return false;
        }
        final CacheData<?> cacheData = this.cache.get(key);
        return cacheData != null
                && ((TimestampedList<?>) historyEntries).getTimestamp() >= cacheData.getLastInvalidationTime();
    }

    @Override
    public void update(final Notification notification) {
        final String key = notification.getDetails().get("source") + "-" + notification.getDetails().get("key");
        invalidate(key);
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit, final int offset) {
        return getOrFetch(KtCachingRequesterIml.HISTORY + itemid, offset, offset + limit,
                () -> this.requester.getHistory(itemid, limit, offset));
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        return this.getOrFetch(KtCachingRequesterIml.HISTORY + itemid, 0,
                () -> this.requester.getLatestHistory(itemid));
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit, final BUY_SELL_BOTH mode,
            final boolean summarizeRemaining) {
        // TODO caching
        return this.requester.getOrderBook(itemid, limit, mode, summarizeRemaining);
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid, final BUY_SELL_BOTH mode) {
        // TODO caching
        return this.requester.getBestOrderBookEntries(itemid, mode);
    }

    @Override
    public Result<Permissions> getPermissions(final U userid) {
        // TODO caching?
        return this.requester.getPermissions(userid);
    }

    @Override
    public Result<List<Item>> getItems(final U userid) {
        return getAllOrFetch(KtCachingRequesterIml.PAYOUTS + userid, () -> this.requester.getItems(userid));
    }

    @Override
    public Result<Item> getItem(final U userid, final String itemid) {
        return getOrFetch(KtCachingRequesterIml.ITEMS + userid, item -> item.getId().equals(itemid),
                () -> this.requester.getItem(userid, itemid));
    }

    @Override
    public Result<List<Message>> getMessages(final U userid) {
        return getAllOrFetch(KtCachingRequesterIml.PAYOUTS + userid, () -> this.requester.getMessages(userid));
    }

    @Override
    public Result<List<Payout>> getPayouts(final U userid) {
        return getAllOrFetch(KtCachingRequesterIml.PAYOUTS + userid, () -> this.requester.getPayouts(userid));
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final U userid, final BUY_SELL type, final String itemid,
            final int amount) {
        final Result<WriteResult<Payout>> result = this.requester.createPayout(userid, type, itemid, amount);
        result.addCallback(
                new InvalidationCallback<WriteResult<Payout>>(this.cache, KtCachingRequesterIml.PAYOUTS + userid));
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final U userid, final String payoutid) {
        final Result<WriteResult<Payout>> result = this.requester.cancelPayout(userid, payoutid);
        result.addCallback(
                new InvalidationCallback<WriteResult<Payout>>(this.cache, KtCachingRequesterIml.PAYOUTS + userid));
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final U userid, final String payoutid) {
        final Result<WriteResult<Payout>> result = this.requester.takeoutPayout(userid, payoutid);
        result.addCallback(
                new InvalidationCallback<WriteResult<Payout>>(this.cache, KtCachingRequesterIml.PAYOUTS + userid));
        return result;
    }

    @Override
    public Result<PusherResult> authorizePusher(final U userid, final String channel_name, final String socketId) {
        return this.requester.authorizePusher(userid, channel_name, socketId);
    }

    @Override
    public Result<List<Trade>> getTrades(final U userid) {
        return getAllOrFetch(KtCachingRequesterIml.TRADES + userid, () -> this.requester.getTrades(userid));
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final U userid, final BUY_SELL mode, final String item,
            final int amount, final String pricePerItem) {
        final Result<WriteResult<Trade>> result = this.requester.createTrade(userid, mode, item, amount, pricePerItem);
        result.addCallback(
                new InvalidationCallback<WriteResult<Trade>>(this.cache, KtCachingRequesterIml.TRADES + userid));
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final U userid, final long tradeId) {
        final Result<WriteResult<Trade>> result = this.requester.cancelTrade(userid, tradeId);
        result.addCallback(
                new InvalidationCallback<WriteResult<Trade>>(this.cache, KtCachingRequesterIml.TRADES + userid));
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final U userid, final long tradeId) {
        final Result<WriteResult<Trade>> result = this.requester.takeoutTrade(userid, tradeId);
        result.addCallback(
                new InvalidationCallback<WriteResult<Trade>>(this.cache, KtCachingRequesterIml.TRADES + userid));
        return result;
    }

    private <T> Result<List<T>> getOrFetch(final String key, final int start, final int end,
            final Supplier<Result<List<T>>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key, s -> new CacheData<T>());
        final Optional<TimestampedList<T>> all = cacheEntry.get(start, end);
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE, all.get());
        } else {
            final Result<List<T>> result = fetcher.get();
            final ExposedResult<List<T>> returnResult = new ExposedResult<>();
            result.addCallback(new Callback<List<T>>() {
                @Override
                public void onSuccess(final int status, final List<T> result) {
                    final TimestampedList<T> timestampedList = cacheEntry.set(result, start, start + result.size());
                    returnResult.setSuccessResult(status, timestampedList);
                }

                @Override
                public void onFailure(final int status, final String message, final String humanReadableMessage) {
                    returnResult.setErrorResult(status, message, humanReadableMessage);
                }

                @Override
                public void onError(final Throwable t) {
                    returnResult.setFailureResult(t);
                }
            });
            return returnResult;
        }
    }

    private <T> Result<T> getOrFetch(final String key, final int index, final Supplier<Result<T>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key, s -> new CacheData<T>());
        final Optional<T> all = cacheEntry.get(index);
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE,
                    KtCachingRequesterIml.this.factory.create(all.get(), cacheEntry.getLastInvalidationTime(), true));
        } else {
            final Result<T> result = fetcher.get();
            final ExposedResult<T> returnResult = new ExposedResult<>();
            result.addCallback(new Callback<T>() {
                @Override
                public void onSuccess(final int status, final T result) {
                    returnResult.setSuccessResult(status, KtCachingRequesterIml.this.factory.create(result,
                            cacheEntry.getLastInvalidationTime(), false));
                }

                @Override
                public void onFailure(final int status, final String message, final String humanReadableMessage) {
                    returnResult.setErrorResult(status, message, humanReadableMessage);
                }

                @Override
                public void onError(final Throwable t) {
                    returnResult.setFailureResult(t);
                }
            });
            return returnResult;
        }
    }

    private <T> Result<T> getOrFetch(final String key, final Predicate<T> index, final Supplier<Result<T>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key, s -> new CacheData<T>());
        final Optional<T> all = cacheEntry.getAny(index);
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE,
                    KtCachingRequesterIml.this.factory.create(all.get(), cacheEntry.getLastInvalidationTime(), true));
        } else {
            final Result<T> result = fetcher.get();
            final ExposedResult<T> returnResult = new ExposedResult<>();
            result.addCallback(new Callback<T>() {
                @Override
                public void onSuccess(final int status, final T result) {
                    returnResult.setSuccessResult(status, KtCachingRequesterIml.this.factory.create(result,
                            cacheEntry.getLastInvalidationTime(), false));
                }

                @Override
                public void onFailure(final int status, final String message, final String humanReadableMessage) {
                    returnResult.setErrorResult(status, message, humanReadableMessage);
                }

                @Override
                public void onError(final Throwable t) {
                    returnResult.setFailureResult(t);
                }
            });
            return returnResult;
        }
    }

    private <T> Result<List<T>> getAllOrFetch(final String key, final Supplier<Result<List<T>>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key, s -> new CacheData<T>());
        final Optional<TimestampedList<T>> all = cacheEntry.getAll();
        if (all.isPresent()) {
            return new ImmediateResult<>(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE, all.get());
        } else {
            final Result<List<T>> result = fetcher.get();
            final ExposedResult<List<T>> returnResult = new ExposedResult<>();
            result.addCallback(new Callback<List<T>>() {
                @Override
                public void onSuccess(final int status, final List<T> result) {
                    final TimestampedList<T> timestampedList = cacheEntry.set(result, 0, result.size());
                    returnResult.setSuccessResult(status, timestampedList);
                }

                @Override
                public void onFailure(final int status, final String message, final String humanReadableMessage) {
                    returnResult.setErrorResult(status, message, humanReadableMessage);
                }

                @Override
                public void onError(final Throwable t) {
                    returnResult.setFailureResult(t);
                }
            });
            return returnResult;
        }
    }

    protected static class ExposedResult<T> extends Result<T> {
        @Override
        protected void setSuccessResult(final int status, final T content) {
            super.setSuccessResult(status, content);
        }

        @Override
        protected void setFailureResult(final Throwable t) {
            super.setFailureResult(t);
        }

        @Override
        protected void setErrorResult(final int status, final String errorMessage,
                final String humanReadableErrorMessage) {
            super.setErrorResult(status, errorMessage, humanReadableErrorMessage);
        }
    }

    @Override
    public void close() throws IOException {
        this.requester.close();
    }
}
