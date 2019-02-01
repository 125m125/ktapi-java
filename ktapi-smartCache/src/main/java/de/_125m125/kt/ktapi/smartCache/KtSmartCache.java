package de._125m125.kt.ktapi.smartCache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.core.BUY_SELL;
import de._125m125.kt.ktapi.core.BUY_SELL_BOTH;
import de._125m125.kt.ktapi.core.KtCachingRequester;
import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.KtRequesterDecorator;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.PAYOUT_TYPE;
import de._125m125.kt.ktapi.core.entities.Entity;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.ItemName;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;
import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.smartCache.caches.CacheData;
import de._125m125.kt.ktapi.smartCache.caches.PrependCacheData;
import de._125m125.kt.ktapi.smartCache.caches.ReplaceOrInvalidateCacheData;
import de._125m125.kt.ktapi.smartCache.caches.ReplaceOrPrependCacheData;
import de._125m125.kt.ktapi.smartCache.caches.ReplaceOrPrependOrInvalidateOnEmptyCacheData;
import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;
import de._125m125.kt.ktapi.smartCache.objects.TimestampedObjectFactory;

/**
 *
 */
public class KtSmartCache extends KtRequesterDecorator
        implements KtRequester, NotificationListener, KtCachingRequester {

    private static final Function<String, CacheData<HistoryEntry>> HISTORY_FACTORY  = s -> new PrependCacheData<>(
            HistoryEntry.class);
    private static final Function<String, CacheData<Item>>         ITEM_FACTORY     = s -> new ReplaceOrInvalidateCacheData<>(
            Item.class, Item::getId);
    private static final Function<String, CacheData<Message>>      MESSAGE_FACTORY  = s -> new PrependCacheData<>(
            Message.class);
    private static final Function<String, CacheData<Payout>>       PAYOUT_FACTORY   = s -> new ReplaceOrPrependCacheData<>(
            Payout.class, Payout::getId);
    private static final Function<String, CacheData<Trade>>        TRADE_FACTORY    = s -> new ReplaceOrPrependOrInvalidateOnEmptyCacheData<>(
            Trade.class, Trade::getId);

    private static final Logger                                    logger           = LoggerFactory
            .getLogger(KtSmartCache.class);

    public static final int                                        CACHE_HIT_STATUS = 299;

    private final Map<String, CacheData<?>>                        cache;
    private final KtNotificationManager<?>                         ktNotificationManager;
    private final TimestampedObjectFactory                         factory;

    public KtSmartCache(final KtRequester requester,
            final KtNotificationManager<?> ktNotificationManager) {
        this(requester, ktNotificationManager, null);
    }

    public KtSmartCache(final KtRequester requester,
            final KtNotificationManager<?> ktNotificationManager,
            final TimestampedObjectFactory factory) {
        super(requester);
        this.ktNotificationManager = ktNotificationManager;
        this.cache = new ConcurrentHashMap<>();
        this.factory = factory != null ? factory : new TimestampedObjectFactory();
    }

    @Override
    public void invalidateHistory(final String itemid) {
        invalidate(Entity.HISTORY_ENTRY.getUpdateChannel() + itemid);
    }

    @Override
    public void invalidateOrderBook(final String itemid) {
        invalidate(Entity.ORDERBOOK_ENTRY.getUpdateChannel() + itemid);
    }

    @Override
    public void invalidateMessages(final UserKey userKey) {
        invalidate(Entity.MESSAGE.getUpdateChannel() + userKey.getUserId());
    }

    @Override
    public void invalidatePayouts(final UserKey userKey) {
        invalidate(Entity.PAYOUT.getUpdateChannel() + userKey.getUserId());
    }

    @Override
    public void invalidateTrades(final UserKey userKey) {
        invalidate(Entity.TRADE.getUpdateChannel() + userKey.getUserId());
    }

    @Override
    public void invalidateItemList(final UserKey userKey) {
        invalidate(Entity.ITEM.getUpdateChannel() + userKey.getUserId());
    }

    private void invalidate(final String key) {
        invalidate(key, null);
    }

    protected <T> void invalidate(final String key, final T[] changedEntries) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheData = (CacheData<T>) this.cache.get(key);
        if (cacheData != null) {
            if (changedEntries != null && changedEntries.length > 0) {
                if (!cacheData.getClazz().isAssignableFrom(changedEntries[0].getClass())) {
                    throw new IllegalArgumentException();
                }
                KtSmartCache.logger.debug("invalidating cache with key {} and changed entries",
                        key);
            } else {
                KtSmartCache.logger.debug("invalidating cache with key {} without changed entries",
                        key);
            }
            cacheData.invalidate(changedEntries);
        }
    }

    @Override
    public boolean isValidHistory(final String itemid, final List<HistoryEntry> historyEntries) {
        return isValid(Entity.HISTORY_ENTRY.getUpdateChannel() + itemid, historyEntries);
    }

    @Override
    public boolean isValidOrderBook(final String itemid, final List<OrderBookEntry> orderBook) {
        return isValid(Entity.ORDERBOOK_ENTRY.getUpdateChannel() + itemid, orderBook);
    }

    @Override
    public boolean isValidMessageList(final UserKey userKey, final List<Message> messages) {
        return isValid(Entity.MESSAGE.getUpdateChannel() + userKey.getUserId(), messages);
    }

    @Override
    public boolean isValidPayoutList(final UserKey userKey, final List<Payout> payouts) {
        return isValid(Entity.PAYOUT.getUpdateChannel() + userKey.getUserId(), payouts);
    }

    @Override
    public boolean isValidTradeList(final UserKey userKey, final List<Trade> trades) {
        return isValid(Entity.TRADE.getUpdateChannel() + userKey.getUserId(), trades);
    }

    @Override
    public boolean isValidItemList(final UserKey userKey, final List<Item> items) {
        return isValid(Entity.ITEM.getUpdateChannel() + userKey.getUserId(), items);
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
        final String key = notification.getDetails().get("source")
                + notification.getDetails().get("key");
        KtSmartCache.logger.debug("received notification for key {}: {}", key, notification);
        if (notification instanceof UpdateNotification<?>) {
            invalidate(key, ((UpdateNotification<?>) notification).getChangedEntries());
        } else {
            invalidate(key);
        }
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit,
            final int offset) {
        this.ktNotificationManager.subscribeToHistory(this);
        return getOrFetch(Entity.HISTORY_ENTRY.getUpdateChannel() + itemid, offset, offset + limit,
                KtSmartCache.HISTORY_FACTORY,
                () -> this.requester.getHistory(itemid, limit, offset));
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        this.ktNotificationManager.subscribeToHistory(this);
        return this.getOrFetch(Entity.HISTORY_ENTRY.getUpdateChannel() + itemid, 0,
                KtSmartCache.HISTORY_FACTORY, () -> this.requester.getLatestHistory(itemid));
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
    public Result<Permissions> getPermissions(final UserKey userKey) {
        // TODO caching?
        return this.requester.getPermissions(userKey);
    }

    @Override
    public Result<List<ItemName>> getItemNames() {
        return getAllOrFetch("itemnames",
                s -> new ReplaceOrInvalidateCacheData<>(ItemName.class, ItemName::getId),
                () -> this.requester.getItemNames());
    }

    @Override
    public Result<List<Item>> getItems(final UserKey userKey) {
        this.ktNotificationManager.subscribeToItems(this, userKey, false);
        this.ktNotificationManager.subscribeToItems(this, userKey, true);
        return getAllOrFetch(Entity.ITEM.getUpdateChannel() + userKey.getUserId(),
                KtSmartCache.ITEM_FACTORY, () -> this.requester.getItems(userKey));
    }

    @Override
    public Result<Item> getItem(final UserKey userKey, final String itemid) {
        this.ktNotificationManager.subscribeToItems(this, userKey, false);
        this.ktNotificationManager.subscribeToItems(this, userKey, true);
        return getOrFetch(Entity.ITEM.getUpdateChannel() + userKey.getUserId(),
                item -> item.getId().equals(itemid), KtSmartCache.ITEM_FACTORY,
                () -> this.requester.getItem(userKey, itemid));
    }

    @Override
    public Result<List<Message>> getMessages(final UserKey userKey, final int offset,
            final int limit) {
        this.ktNotificationManager.subscribeToMessages(this, userKey, false);
        this.ktNotificationManager.subscribeToMessages(this, userKey, true);
        return getOrFetch(Entity.MESSAGE.getUpdateChannel() + userKey.getUserId(), offset,
                offset + limit, KtSmartCache.MESSAGE_FACTORY,
                () -> this.requester.getMessages(userKey, offset, limit));
    }

    @Override
    public Result<List<Payout>> getPayouts(final UserKey userKey, final int offset,
            final int limit) {
        this.ktNotificationManager.subscribeToPayouts(this, userKey, false);
        this.ktNotificationManager.subscribeToPayouts(this, userKey, true);
        return getOrFetch(Entity.PAYOUT.getUpdateChannel() + userKey.getUserId(), offset,
                offset + limit, KtSmartCache.PAYOUT_FACTORY,
                () -> this.requester.getPayouts(userKey, offset, limit));
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final UserKey userKey, final PAYOUT_TYPE type,
            final String itemid, final String amount) {
        final Result<WriteResult<Payout>> result = this.requester.createPayout(userKey, type,
                itemid, amount);
        result.addCallback(new InvalidationCallback<WriteResult<Payout>>(this,
                Entity.PAYOUT.getUpdateChannel() + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final UserKey userKey, final long payoutid) {
        final Result<WriteResult<Payout>> result = this.requester.cancelPayout(userKey, payoutid);
        result.addCallback(new InvalidationCallback<WriteResult<Payout>>(this,
                Entity.PAYOUT.getUpdateChannel() + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final UserKey userKey, final long payoutid) {
        final Result<WriteResult<Payout>> result = this.requester.takeoutPayout(userKey, payoutid);
        result.addCallback(new InvalidationCallback<WriteResult<Payout>>(this,
                Entity.PAYOUT.getUpdateChannel() + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<PusherResult> authorizePusher(final UserKey userKey, final String channel_name,
            final String socketId) {
        return this.requester.authorizePusher(userKey, channel_name, socketId);
    }

    @Override
    public Result<List<Trade>> getTrades(final UserKey userKey) {
        this.ktNotificationManager.subscribeToTrades(this, userKey, false);
        this.ktNotificationManager.subscribeToTrades(this, userKey, true);
        return getAllOrFetch(Entity.TRADE.getUpdateChannel() + userKey.getUserId(),
                KtSmartCache.TRADE_FACTORY, () -> this.requester.getTrades(userKey));
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final UserKey userKey, final BUY_SELL mode,
            final String item, final int amount, final String pricePerItem) {
        final Result<WriteResult<Trade>> result = this.requester.createTrade(userKey, mode, item,
                amount, pricePerItem);
        result.addCallback(new InvalidationCallback<WriteResult<Trade>>(this,
                Entity.TRADE.getUpdateChannel() + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final UserKey userKey, final long tradeId) {
        final Result<WriteResult<Trade>> result = this.requester.cancelTrade(userKey, tradeId);
        result.addCallback(new InvalidationCallback<WriteResult<Trade>>(this,
                Entity.TRADE.getUpdateChannel() + userKey.getUserId()));
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final UserKey userKey, final long tradeId) {
        final Result<WriteResult<Trade>> result = this.requester.takeoutTrade(userKey, tradeId);
        result.addCallback(new InvalidationCallback<WriteResult<Trade>>(this,
                Entity.TRADE.getUpdateChannel() + userKey.getUserId()));
        return result;
    }

    private <T> Result<List<T>> getOrFetch(final String key, final int start, final int end,
            final Function<String, CacheData<T>> cacheGenerator,
            final Supplier<Result<List<T>>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                cacheGenerator);
        final Optional<TimestampedList<T>> all = cacheEntry.get(start, end);
        if (all.isPresent()) {
            KtSmartCache.logger.debug("getting {} to {} for {} resulted in a cache hit", start, end,
                    key);
            return new ImmediateResult<>(KtSmartCache.CACHE_HIT_STATUS, all.get());
        } else {
            KtSmartCache.logger.debug("getting {} to {} for {} resulted in a cache miss", start,
                    end, key);
            return new ExposedResult<>(fetcher,
                    (status, result) -> cacheEntry.set(result, start, result.size() < end - start));
        }
    }

    private <T> Result<T> getOrFetch(final String key, final int index,
            final Function<String, CacheData<T>> cacheGenerator,
            final Supplier<Result<T>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                cacheGenerator);
        final Optional<T> all = cacheEntry.get(index);
        if (all.isPresent()) {
            KtSmartCache.logger.debug("getting index {} for {} resulted in a cache hit", index,
                    key);
            return new ImmediateResult<>(KtSmartCache.CACHE_HIT_STATUS, KtSmartCache.this.factory
                    .create(all.get(), cacheEntry.getLastInvalidationTime(), true));
        } else {
            KtSmartCache.logger.debug("getting index {} for {} resulted in a cache miss", index,
                    key);
            return new ExposedResult<>(fetcher, (status, result) -> KtSmartCache.this.factory
                    .create(result, cacheEntry.getLastInvalidationTime(), false));
        }
    }

    private <T> Result<T> getOrFetch(final String key, final Predicate<T> index,
            final Function<String, CacheData<T>> cacheGenerator,
            final Supplier<Result<T>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                cacheGenerator);
        final Optional<T> all = cacheEntry.getAny(index);
        if (all.isPresent()) {
            KtSmartCache.logger.debug("getting {} with predicate {} resulted in a cache hit", key,
                    index);
            return new ImmediateResult<>(KtSmartCache.CACHE_HIT_STATUS, KtSmartCache.this.factory
                    .create(all.get(), cacheEntry.getLastInvalidationTime(), true));
        } else {
            KtSmartCache.logger.debug("getting {} with predicate {} resulted in a cache miss", key,
                    index);
            final ExposedResult<T> returnResult = new ExposedResult<>(fetcher,
                    (status, result) -> KtSmartCache.this.factory.create(result,
                            cacheEntry.getLastInvalidationTime(), false));
            return returnResult;
        }
    }

    private <T> Result<List<T>> getAllOrFetch(final String key,
            final Function<String, CacheData<T>> cacheGenerator,
            final Supplier<Result<List<T>>> fetcher) {
        @SuppressWarnings("unchecked")
        final CacheData<T> cacheEntry = (CacheData<T>) this.cache.computeIfAbsent(key,
                cacheGenerator);
        final Optional<TimestampedList<T>> all = cacheEntry.getAll();
        if (all.isPresent()) {
            KtSmartCache.logger.debug("getting all entries for {} resulted in a cache hit", key);
            return new ImmediateResult<>(KtSmartCache.CACHE_HIT_STATUS, all.get());
        } else {
            KtSmartCache.logger.debug("getting all entries for {} resulted in a cache miss", key);
            return new ExposedResult<>(fetcher,
                    (status, result) -> cacheEntry.set(result, 0, true));
        }
    }

    protected static class ExposedResult<T> extends Result<T> {

        public ExposedResult(final Supplier<Result<T>> fetcher,
                final BiFunction<Integer, T, T> success) {
            fetcher.get().addCallback(Callback.of(
                    Optional.of((s, t) -> this.setSuccessResult(s, success.apply(s, t))),
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
