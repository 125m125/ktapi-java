package de._125m125.kt.ktapi_java.core;

import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.entities.UserKey;

public interface KtCachingRequester<T extends UserKey> extends KtRequester<T> {
    public void invalidateHistory(String itemid);

    public void invalidateOrderBook(String itemid);

    public void invalidateMessages(T user);

    public void invalidatePayouts(T user);

    public void invalidateTrades(T user);

    public void invalidateItemList(T user);

    public boolean isValidHistory(String itemid, List<HistoryEntry> historyEntries);

    public boolean isValidOrderBook(String itemid, List<OrderBookEntry> orderBook);

    public boolean isValidMessageList(T user, List<Message> messages);

    public boolean isValidPayoutList(T user, List<Payout> payouts);

    public boolean isValidTradeList(T user, List<Trade> trades);

    public boolean isValidItemList(T user, List<Item> items);

}
