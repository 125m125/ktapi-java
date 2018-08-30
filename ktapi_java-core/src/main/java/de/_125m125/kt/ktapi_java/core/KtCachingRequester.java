package de._125m125.kt.ktapi_java.core;

import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.users.UserKey;

public interface KtCachingRequester<T extends UserKey<?>> extends KtRequester<T> {
    public void invalidateHistory(String itemid);

    public void invalidateOrderBook(String itemid);

    public void invalidateMessages(T userKey);

    public void invalidatePayouts(T userKey);

    public void invalidateTrades(T userKey);

    public void invalidateItemList(T userKey);

    public boolean isValidHistory(String itemid, List<HistoryEntry> historyEntries);

    public boolean isValidOrderBook(String itemid, List<OrderBookEntry> orderBook);

    public boolean isValidMessageList(T userKey, List<Message> messages);

    public boolean isValidPayoutList(T userKey, List<Payout> payouts);

    public boolean isValidTradeList(T userKey, List<Trade> trades);

    public boolean isValidItemList(T userKey, List<Item> items);

}
