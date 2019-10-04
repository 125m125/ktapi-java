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

import java.io.Closeable;
import java.util.List;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.ItemName;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.ItemPayinResult;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.UserKey;

public interface KtRequester extends Closeable {
    String DEFAULT_BASE_URL = "https://kt.125m125.de/api/v2.0/";

    public Result<List<HistoryEntry>> getHistory(String itemid, int limit, int offset);

    public Result<HistoryEntry> getLatestHistory(String itemid);

    public Result<List<OrderBookEntry>> getOrderBook(String itemid, int limit, BuySellBoth mode,
            boolean summarizeRemaining);

    public Result<List<OrderBookEntry>> getBestOrderBookEntries(String itemid, BuySellBoth mode);

    public Result<Permissions> getPermissions(UserKey userKey);

    /**
     * Gets a list containing the ItemNames for all items known to the server.
     *
     * @return the item names
     */
    public Result<List<ItemName>> getItemNames();

    public Result<List<Item>> getItems(UserKey userKey);

    public Result<Item> getItem(UserKey userKey, String itemid);

    public default Result<List<Message>> getMessages(final UserKey userKey) {
        return getMessages(userKey, 0, 50);
    }

    public Result<List<Message>> getMessages(UserKey userKey, int offset, int limit);

    public default Result<List<Payout>> getPayouts(final UserKey userKey) {
        return getPayouts(userKey, 0, 50);
    }

    public Result<List<Payout>> getPayouts(UserKey userKey, int offset, int limit);

    public Result<WriteResult<Payout>> createPayout(UserKey userKey, PayoutType type, String itemid,
            String amount);

    public Result<WriteResult<Payout>> cancelPayout(UserKey userKey, long payoutid);

    public Result<WriteResult<Payout>> takeoutPayout(UserKey userKey, long payoutid);

    public Result<PusherResult> authorizePusher(UserKey userKey, String channelName,
            String socketId);

    public Result<List<Trade>> getTrades(UserKey userKey);

    public Result<WriteResult<Trade>> createTrade(UserKey userKey, BuySell mode, String item,
            int amount, String pricePerItem);

    public Result<WriteResult<Trade>> cancelTrade(UserKey userKey, long tradeId);

    public Result<WriteResult<Trade>> takeoutTrade(UserKey userKey, long tradeId);

    public Result<Long> ping();

    /**
     * tells the server to read the bank statement.
     *
     * @return a WriteResult containing the remaining time in seconds until the next request can be
     *         sent.
     */
    public Result<WriteResult<Long>> readBankStatement();

    public Result<ItemPayinResult> adminAddItems(UserKey adminKey, String targetName,
            List<Item> items, String message);
}