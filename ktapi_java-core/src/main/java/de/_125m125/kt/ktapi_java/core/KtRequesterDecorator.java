package de._125m125.kt.ktapi_java.core;

import java.io.IOException;
import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.core.results.WriteResult;

public class KtRequesterDecorator<U extends UserKey> implements KtRequester<U> {
    private final KtRequester<U> requester;

    public KtRequesterDecorator(final KtRequester<U> requester) {
        this.requester = requester;
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit, final int offset) {
        return this.requester.getHistory(itemid, limit, offset);
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        return this.requester.getLatestHistory(itemid);
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit, final BUY_SELL_BOTH mode,
            final boolean summarizeRemaining) {
        return this.requester.getOrderBook(itemid, limit, mode, summarizeRemaining);
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid, final BUY_SELL_BOTH mode) {
        return this.requester.getBestOrderBookEntries(itemid, mode);
    }

    @Override
    public Result<Permissions> getPermissions(final U user) {
        return this.requester.getPermissions(user);
    }

    @Override
    public Result<List<Item>> getItems(final U user) {
        return this.requester.getItems(user);
    }

    @Override
    public Result<Item> getItem(final U user, final String itemid) {
        return this.requester.getItem(user, itemid);
    }

    @Override
    public Result<List<Message>> getMessages(final U user) {
        return this.requester.getMessages(user);
    }

    @Override
    public Result<List<Payout>> getPayouts(final U user) {
        return this.requester.getPayouts(user);
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final U user, final BUY_SELL type, final String itemid,
            final int amount) {
        return this.requester.createPayout(user, type, itemid, amount);
    }

    @Override
    public void close() throws IOException {
        this.requester.close();
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final U user, final String payoutid) {
        return this.requester.cancelPayout(user, payoutid);
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final U user, final String payoutid) {
        return this.requester.takeoutPayout(user, payoutid);
    }

    @Override
    public Result<PusherResult> authorizePusher(final U user, final String channel_name, final String socketId) {
        return this.requester.authorizePusher(user, channel_name, socketId);
    }

    @Override
    public Result<List<Trade>> getTrades(final U user) {
        return this.requester.getTrades(user);
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final U user, final BUY_SELL mode, final String item,
            final int amount, final String pricePerItem) {
        return this.requester.createTrade(user, mode, item, amount, pricePerItem);
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final U user, final long tradeId) {
        return this.requester.cancelTrade(user, tradeId);
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final U user, final long tradeId) {
        return this.requester.takeoutTrade(user, tradeId);
    }

}
