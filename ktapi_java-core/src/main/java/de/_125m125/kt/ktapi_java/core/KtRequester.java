package de._125m125.kt.ktapi_java.core;

import java.io.Closeable;
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

public interface KtRequester<T extends UserKey> extends Closeable {
    public Result<List<HistoryEntry>> getHistory(String itemid, int limit, int offset);

    public Result<HistoryEntry> getLatestHistory(String itemid);

    public Result<List<OrderBookEntry>> getOrderBook(String itemid, int limit, BUY_SELL_BOTH mode,
            boolean summarizeRemaining);

    public Result<List<OrderBookEntry>> getBestOrderBookEntries(String itemid, BUY_SELL_BOTH mode);

    public Result<Permissions> getPermissions(T user);

    public Result<List<Item>> getItems(T user);

    public Result<Item> getItem(T user, String itemid);

    public Result<List<Message>> getMessages(T user);

    public Result<List<Payout>> getPayouts(T user);

    public Result<WriteResult<Payout>> createPayout(T user, BUY_SELL type, String itemid, int amount);

    public Result<WriteResult<Payout>> cancelPayout(T user, String payoutid);

    public Result<WriteResult<Payout>> takeoutPayout(T user, String payoutid);

    public Result<PusherResult> authorizePusher(T user, String channel_name, String socketId);

    public Result<List<Trade>> getTrades(T user);

    public Result<WriteResult<Trade>> createTrade(T user, BUY_SELL mode, String item, int amount, String pricePerItem);

    public Result<WriteResult<Trade>> cancelTrade(T user, long tradeId);

    public Result<WriteResult<Trade>> takeoutTrade(T user, long tradeId);
}