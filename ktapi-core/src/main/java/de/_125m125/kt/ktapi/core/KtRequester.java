package de._125m125.kt.ktapi.core;

import java.io.Closeable;
import java.util.List;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.UserKey;

public interface KtRequester extends Closeable {
    public Result<List<HistoryEntry>> getHistory(String itemid, int limit, int offset);

    public Result<HistoryEntry> getLatestHistory(String itemid);

    public Result<List<OrderBookEntry>> getOrderBook(String itemid, int limit, BUY_SELL_BOTH mode,
            boolean summarizeRemaining);

    public Result<List<OrderBookEntry>> getBestOrderBookEntries(String itemid, BUY_SELL_BOTH mode);

    public Result<Permissions> getPermissions(UserKey userKey);

    public Result<List<Item>> getItems(UserKey userKey);

    public Result<Item> getItem(UserKey userKey, String itemid);

    public Result<List<Message>> getMessages(UserKey userKey);

    public Result<List<Payout>> getPayouts(UserKey userKey);

    public Result<WriteResult<Payout>> createPayout(UserKey userKey, PAYOUT_TYPE type,
            String itemid, String amount);

    public Result<WriteResult<Payout>> cancelPayout(UserKey userKey, long payoutid);

    public Result<WriteResult<Payout>> takeoutPayout(UserKey userKey, long payoutid);

    public Result<PusherResult> authorizePusher(UserKey userKey, String channel_name,
            String socketId);

    public Result<List<Trade>> getTrades(UserKey userKey);

    public Result<WriteResult<Trade>> createTrade(UserKey userKey, BUY_SELL mode, String item,
            int amount, String pricePerItem);

    public Result<WriteResult<Trade>> cancelTrade(UserKey userKey, long tradeId);

    public Result<WriteResult<Trade>> takeoutTrade(UserKey userKey, long tradeId);

    public Result<Long> ping();
}