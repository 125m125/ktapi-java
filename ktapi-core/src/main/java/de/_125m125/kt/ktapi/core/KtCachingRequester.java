/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
