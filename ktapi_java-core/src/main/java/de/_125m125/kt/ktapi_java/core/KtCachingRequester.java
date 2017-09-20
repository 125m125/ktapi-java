package de._125m125.kt.ktapi_java.core;

import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Trade;

public interface KtCachingRequester extends KtRequester {
    public void invalidateHistory(String itemid);

    public void invalidateOrderBook(String itemid);

    public void invalidateMessages(String userid);

    public void invalidatePayouts(String userid);

    public void invalidateTrades(String userid);

    public void invalidateItemList(String userid);

    public boolean isValidHistory(String itemid, List<HistoryEntry> historyEntries);

    public boolean isValidOrderBook(String itemid, List<OrderBookEntry> orderBook);

    public boolean isValidMessageList(String userid, List<Message> messages);

    public boolean isValidPayoutList(String userid, List<Payout> payouts);

    public boolean isValidTradeList(String userid, List<Trade> trades);

    public boolean isValidItemList(String userid, List<Item> items);

}
