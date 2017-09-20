package de._125m125.kt.ktapi_java.core;

import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.core.results.WriteResult;

public interface KtRequester {
    public Result<List<HistoryEntry>> getHistory(String itemid, int limit, int offset);

    public Result<HistoryEntry> getLatestHistory(String itemid);

    public Result<List<OrderBookEntry>> getOrderBook(String itemid, int limit, BUY_SELL_BOTH mode,
            boolean summarizeRemaining);

    public Result<List<OrderBookEntry>> getBestOrderBookEntries(String itemid, BUY_SELL_BOTH mode);

    public Result<Permissions> getPermissions(String userid);

    public Result<List<Item>> getItems(String userid);

    public Result<Item> getItem(String userid, String itemid);

    public Result<List<Message>> getMessages(String userid);

    public Result<List<Payout>> getPayouts(String userid);

    public Result<WriteResult<Payout>> createPayout(String userid, BUY_SELL type, String itemid, int amount);

    public Result<WriteResult<Payout>> cancelPayout(String userid, String payoutid);

    public Result<WriteResult<Payout>> takeoutPayout(String userid, String payoutid);

    public Result<PusherResult> authorizePusher(String userid, String channel_name, String socketId);

    public Result<List<Trade>> getTrades(String userid);

    public Result<WriteResult<Trade>> createTrade(final String userid, final BUY_SELL mode, final String item,
            final int amount, final String pricePerItem);

    public Result<WriteResult<Trade>> cancelTrade(final String userid, final long tradeId);

    public Result<WriteResult<Trade>> takeoutTrade(final String userid, final long tradeId);

    void close();
}