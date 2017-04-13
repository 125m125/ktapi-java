package de._125m125.kt.ktapi_java.full;

import java.util.List;
import java.util.Map;

import de._125m125.kt.ktapi_java.cachingPusher.CachingPusherKt;
import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.KtCachingRequester;
import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.KtRequestUtil;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.Parser;
import de._125m125.kt.ktapi_java.core.Result;
import de._125m125.kt.ktapi_java.core.objects.HistoryEntry;
import de._125m125.kt.ktapi_java.core.objects.Item;
import de._125m125.kt.ktapi_java.core.objects.Message;
import de._125m125.kt.ktapi_java.core.objects.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.objects.Payout;
import de._125m125.kt.ktapi_java.core.objects.Permissions;
import de._125m125.kt.ktapi_java.core.objects.Trade;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.pinningRequester.KtPinningRequester;
import de._125m125.kt.ktapi_java.pusher.PusherKt;
import de._125m125.kt.ktapi_java.simple.Kt;
import de._125m125.kt.ktapi_java.simple.KtRequesterImpl;
import de._125m125.kt.ktapi_java.simple.parsers.SpecializedJsonParser;

public class FullKt implements KtRequester, KtRequestUtil, KtCachingRequester, KtNotificationManager {

    public static FullKt create(final User u, final boolean pinning) {
        final KtRequester r = createRequester(u, pinning);
        final KtNotificationManager nm = new PusherKt(u, new SpecializedJsonParser<>(), KtRequesterImpl.BASE_URL);
        final CachingPusherKt cp = new CachingPusherKt(u, r, nm);
        final KtRequestUtil kt = new Kt(cp);

        return new FullKt(kt, nm, cp);
    }

    public static FullKt createWithoutCache(final User u, final boolean pinning) {
        final KtRequester r = createRequester(u, pinning);
        final KtRequestUtil kt = new Kt(r);
        final KtNotificationManager nm = new PusherKt(u, new SpecializedJsonParser<>(), KtRequesterImpl.BASE_URL);

        return new FullKt(kt, nm);
    }

    public static FullKt create(final User u, final boolean caching, final boolean pinning) {
        if (caching) {
            return create(u, pinning);
        } else {
            return createWithoutCache(u, pinning);
        }
    }

    private static KtRequesterImpl createRequester(final User u, final boolean pinning) {
        KtRequesterImpl result;
        if (pinning) {
            result = new KtPinningRequester(u);
        } else {
            result = new KtRequesterImpl(u);
        }
        return result;
    }

    private final KtRequestUtil         requestUtil;
    private final KtNotificationManager notificationManager;
    private final KtCachingRequester    cachingRequester;

    public FullKt(final KtRequestUtil requestUtil, final KtNotificationManager notificationManager,
            final KtCachingRequester cachingRequester) {
        this.requestUtil = requestUtil;
        this.notificationManager = notificationManager;
        this.cachingRequester = cachingRequester;

    }

    public FullKt(final KtRequestUtil requestUtil, final KtNotificationManager notificationManager) {
        this.requestUtil = requestUtil;
        this.notificationManager = notificationManager;
        this.cachingRequester = null;

    }

    @Override
    public <T, U> U performRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        if (this.cachingRequester != null) {
            this.cachingRequester.performRequest(method, path, params, auth, parser, helper);
        }
        return this.requestUtil.performRequest(method, path, params, auth, parser, helper);
    }

    @Override
    public Permissions getPermissions() {
        return this.requestUtil.getPermissions();
    }

    @Override
    public List<Item> getItems() {
        return this.requestUtil.getItems();
    }

    @Override
    public List<Trade> getTrades() {
        return this.requestUtil.getTrades();
    }

    @Override
    public List<Message> getMessages() {
        return this.requestUtil.getMessages();
    }

    @Override
    public List<Payout> getPayouts() {
        return this.requestUtil.getPayouts();
    }

    @Override
    public <T> T performPlainRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<T, ?, ?> parser) {
        if (this.cachingRequester != null) {
            this.cachingRequester.performPlainRequest(method, path, params, auth, parser);
        }
        return this.requestUtil.performPlainRequest(method, path, params, auth, parser);
    }

    @Override
    public List<OrderBookEntry> getOrderBook(final Item material, final int limit, final boolean summarize) {
        return this.requestUtil.getOrderBook(material, limit, summarize);
    }

    @Override
    public List<OrderBookEntry> getOrderBook(final String material, final int limit, final boolean summarize) {
        return this.requestUtil.getOrderBook(material, limit, summarize);
    }

    @Override
    public List<HistoryEntry> getStatistics(final Item material, final int limit) {
        return this.requestUtil.getStatistics(material, limit);
    }

    @Override
    public List<HistoryEntry> getStatistics(final String material, final int limit) {
        return this.requestUtil.getStatistics(material, limit);
    }

    @Override
    public Result<Trade> createTrade(final BUY_SELL buySell, final Item item, final int count, final double price) {
        return this.requestUtil.createTrade(buySell, item, count, price);
    }

    @Override
    public Result<Trade> createTrade(final BUY_SELL buySell, final Item item, final double price) {
        return this.requestUtil.createTrade(buySell, item, price);
    }

    @Override
    public Result<Trade> recreateTrade(final Trade trade) {
        return this.requestUtil.recreateTrade(trade);
    }

    @Override
    public Result<Trade> fulfillTrade(final Trade trade) {
        return this.requestUtil.fulfillTrade(trade);
    }

    @Override
    public Result<Trade> createTrade(final BUY_SELL buySell, final String item, final int count, final double price) {
        return this.requestUtil.createTrade(buySell, item, count, price);
    }

    @Override
    public Result<Trade> cancelTrade(final Trade trade) {
        return this.requestUtil.cancelTrade(trade);
    }

    @Override
    public Result<Trade> cancelTrade(final long tradeid) {
        return this.requestUtil.cancelTrade(tradeid);
    }

    @Override
    public Result<Trade> takeoutFromTrade(final Trade trade) {
        return this.requestUtil.takeoutFromTrade(trade);
    }

    @Override
    public Result<Trade> takeoutFromTrade(final long tradeid) {
        return this.requestUtil.takeoutFromTrade(tradeid);
    }

    @Override
    public void subscribeToMessages(final NotificationListener listener, final User user, final boolean selfCreated) {
        this.notificationManager.subscribeToMessages(listener, user, selfCreated);
    }

    @Override
    public void subscribeToTrades(final NotificationListener listener, final User user, final boolean selfCreated) {
        this.notificationManager.subscribeToTrades(listener, user, selfCreated);
    }

    @Override
    public void subscribeToItems(final NotificationListener listener, final User user, final boolean selfCreated) {
        this.notificationManager.subscribeToItems(listener, user, selfCreated);
    }

    @Override
    public void subscribeToPayouts(final NotificationListener listener, final User user, final boolean selfCreated) {
        this.notificationManager.subscribeToPayouts(listener, user, selfCreated);
    }

    @Override
    public void subscribeToOrderbook(final NotificationListener listener) {
        this.notificationManager.subscribeToOrderbook(listener);
    }

    @Override
    public void subscribeToHistory(final NotificationListener listener) {
        this.notificationManager.subscribeToHistory(listener);
    }

    @Override
    public void subscribeToAll(final NotificationListener listener, final User u, final boolean selfCreated) {
        this.notificationManager.subscribeToAll(listener, u, selfCreated);
    }

    @Override
    public void subscribeToUpdates(final NotificationListener listener, final User u, final String path,
            final boolean selfCreated) {
        this.notificationManager.subscribeToUpdates(listener, u, path, selfCreated);
    }

    @Override
    public <T, U> U performUncachedRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        if (this.cachingRequester == null) {
            return this.requestUtil.performRequest(method, path, params, auth, parser, helper);
        }
        return this.cachingRequester.performUncachedRequest(method, path, params, auth, parser, helper);
    }

    @Override
    public boolean hasUpdated(final Object toCheck) {
        if (this.cachingRequester == null) {
            return true;
        }
        return this.cachingRequester.hasUpdated(toCheck);
    }
}
