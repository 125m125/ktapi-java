package de._125m125.kt.ktapi.core;

import java.util.List;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.users.UserKey;

public interface KtCachingRequester extends KtRequester {
    public void invalidateHistory(String itemid);

    public void invalidateOrderBook(String itemid);

    public void invalidateMessages(UserKey userKey);

    public void invalidatePayouts(UserKey userKey);

    public void invalidateTrades(UserKey userKey);

    public void invalidateItemList(UserKey userKey);

    public boolean isValidHistory(String itemid, List<HistoryEntry> historyEntries);

    public boolean isValidOrderBook(String itemid, List<OrderBookEntry> orderBook);

    public boolean isValidMessageList(UserKey userKey, List<Message> messages);

    public boolean isValidPayoutList(UserKey userKey, List<Payout> payouts);

    public boolean isValidTradeList(UserKey userKey, List<Trade> trades);

    public boolean isValidItemList(UserKey userKey, List<Item> items);

}
