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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.ItemName;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.UserKey;

public class KtRequesterDecorator implements KtRequester {
    protected final KtRequester requester;

    public KtRequesterDecorator(final KtRequester requester) {
        this.requester = Objects.requireNonNull(requester);
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit,
            final int offset) {
        return this.requester.getHistory(itemid, limit, offset);
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        return this.requester.getLatestHistory(itemid);
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit,
            final BuySellBoth mode, final boolean summarizeRemaining) {
        return this.requester.getOrderBook(itemid, limit, mode, summarizeRemaining);
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid,
            final BuySellBoth mode) {
        return this.requester.getBestOrderBookEntries(itemid, mode);
    }

    @Override
    public Result<Permissions> getPermissions(final UserKey userKey) {
        return this.requester.getPermissions(userKey);
    }

    @Override
    public Result<List<ItemName>> getItemNames() {
        return this.requester.getItemNames();
    }

    @Override
    public Result<List<Item>> getItems(final UserKey userKey) {
        return this.requester.getItems(userKey);
    }

    @Override
    public Result<Item> getItem(final UserKey userKey, final String itemid) {
        return this.requester.getItem(userKey, itemid);
    }

    @Override
    public Result<List<Message>> getMessages(final UserKey userKey) {
        return this.requester.getMessages(userKey);
    }

    @Override
    public Result<List<Message>> getMessages(final UserKey userKey, final int offset,
            final int limit) {
        return this.requester.getMessages(userKey, offset, limit);
    }

    @Override
    public Result<List<Payout>> getPayouts(final UserKey userKey) {
        return this.requester.getPayouts(userKey);
    }

    @Override
    public Result<List<Payout>> getPayouts(final UserKey userKey, final int offset,
            final int limit) {
        return this.requester.getPayouts(userKey, offset, limit);
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final UserKey userKey, final PayoutType type,
            final String itemid, final String amount) {
        return this.requester.createPayout(userKey, type, itemid, amount);
    }

    @Override
    public void close() throws IOException {
        this.requester.close();
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final UserKey userKey, final long payoutid) {
        return this.requester.cancelPayout(userKey, payoutid);
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final UserKey userKey, final long payoutid) {
        return this.requester.takeoutPayout(userKey, payoutid);
    }

    @Override
    public Result<PusherResult> authorizePusher(final UserKey userKey, final String channelName,
            final String socketId) {
        return this.requester.authorizePusher(userKey, channelName, socketId);
    }

    @Override
    public Result<List<Trade>> getTrades(final UserKey userKey) {
        return this.requester.getTrades(userKey);
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final UserKey userKey, final BuySell mode,
            final String item, final int amount, final String pricePerItem) {
        return this.requester.createTrade(userKey, mode, item, amount, pricePerItem);
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final UserKey userKey, final long tradeId) {
        return this.requester.cancelTrade(userKey, tradeId);
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final UserKey userKey, final long tradeId) {
        return this.requester.takeoutTrade(userKey, tradeId);
    }

    @Override
    public Result<Long> ping() {
        return this.requester.ping();
    }

    @Override
    public Result<WriteResult<Long>> readBankStatement() {
        return this.requester.readBankStatement();
    }

}
