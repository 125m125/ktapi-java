package de._125m125.kt.ktapi.core;

import java.text.NumberFormat;
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

public class SingleUserKtRequester<T extends UserKey<?>> extends KtRequesterDecorator<T> {

    private static final NumberFormat NUMBER_FORMAT;
    static {
        NUMBER_FORMAT = NumberFormat.getInstance();
        SingleUserKtRequester.NUMBER_FORMAT.setMaximumFractionDigits(2);
        SingleUserKtRequester.NUMBER_FORMAT.setGroupingUsed(false);
    }

    private final String wrongUserErrorStart;
    private final T      userKey;

    public SingleUserKtRequester(final T userKey, final KtRequester<T> requester) {
        super(requester);
        this.userKey = userKey;
        this.wrongUserErrorStart = "This requester only supports " + this.userKey + " but got ";
    }

    public T getUser() {
        return this.userKey;
    }

    /**
     * Gets the permissions.
     *
     * @return the permissions
     */
    public Result<Permissions> getPermissions() {
        return super.getPermissions(this.userKey);
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public Result<List<Item>> getItems() {
        return super.getItems(this.userKey);
    }

    /**
     * Gets the trades.
     *
     * @return the trades
     */
    public Result<List<Trade>> getTrades() {
        return super.getTrades(this.userKey);
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public Result<List<Message>> getMessages() {
        return super.getMessages(this.userKey);
    }

    /**
     * Gets the payoutrequests.
     *
     * @return the payouts
     */
    public Result<List<Payout>> getPayouts() {
        return super.getPayouts(this.userKey);
    }

    /**
     * Gets the order book.
     *
     * @param material
     *            the material
     * @param limit
     *            the maximum number of entries
     * @param summarize
     *            true if remaining orders should be summarized if exceeding the
     *            limit
     * @return the order book
     */
    public Result<List<OrderBookEntry>> getOrderBook(final Item material, final int limit, final BUY_SELL_BOTH mode,
            final boolean summarize) {
        return this.getOrderBook(material.getId(), limit, mode, summarize);
    }

    /**
     * Gets the order book.
     *
     * @param material
     *            the materialid
     * @param limit
     *            the maximum number of entries
     * @param summarize
     *            true if remaining orders should be summarized if exceeding the
     *            limit
     * @return the order book
     */
    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String material, final int limit, final BUY_SELL_BOTH mode,
            final boolean summarize) {
        return super.getOrderBook(material, limit, mode, summarize);
    }

    /**
     * Gets the statistics.
     *
     * @param material
     *            the material
     * @param limit
     *            the maximum number of entries
     * @return the statistics
     */
    public Result<List<HistoryEntry>> getHistory(final Item material, final int limit, final int offset) {
        return this.getHistory(material.getId(), limit, offset);
    }

    /**
     * Gets the statistics.
     *
     * @param material
     *            the materialid
     * @param limit
     *            the maximum number of entries
     * @return the statistics
     */
    @Override
    public Result<List<HistoryEntry>> getHistory(final String material, final int limit, final int offset) {
        return super.getHistory(material, limit, offset);
    }

    /**
     * Creates a new order.
     *
     * @param buySell
     *            the mode of the new order
     * @param item
     *            the item to trade
     * @param count
     *            the amount of items to trade
     * @param price
     *            the price per item
     * @return the result
     */
    public Result<WriteResult<Trade>> createTrade(final BUY_SELL buySell, final Item item, final int count,
            final String price) {
        return this.createTrade(buySell, item.getId(), count, price);
    }

    /**
     * Creates a new order and trades the amount of items indicated by the item.
     *
     * @param buySell
     *            the mode of the new order
     * @param item
     *            the item to trade
     * @param price
     *            the price per item
     * @return the result
     */
    public Result<WriteResult<Trade>> createTrade(final BUY_SELL buySell, final Item item, final String price) {
        return this.createTrade(buySell, item.getId(), (int) item.getAmount(), price);
    }

    /**
     * Creates a new trade with the same parameters as the given trade.
     *
     * @param trade
     *            the trade to recreate
     * @return the result
     */
    public Result<WriteResult<Trade>> recreateTrade(final Trade trade) {
        return this.createTrade(trade.getBuySell(), trade.getMaterialId(), trade.getAmount(),
                SingleUserKtRequester.NUMBER_FORMAT.format(trade.getPrice()));
    }

    /**
     * Creates a new order that completes a given order.
     *
     * @param trade
     *            the trade to fulfill
     * @return the result
     */
    public Result<WriteResult<Trade>> fulfillTrade(final Trade trade) {
        return this.createTrade(trade.getBuySell().getOpposite(), trade.getMaterialId(), trade.getAmount(),
                SingleUserKtRequester.NUMBER_FORMAT.format(trade.getPrice()));
    }

    /**
     * Creates a new order.
     *
     * @param buySell
     *            the mode of the new order
     * @param item
     *            the id of the material to trade
     * @param count
     *            the amount of items to trade
     * @param price
     *            the price per item
     * @return the result
     */
    public Result<WriteResult<Trade>> createTrade(final BUY_SELL buySell, final String item, final int count,
            final String price) {
        return super.createTrade(this.userKey, buySell, item, count, price);
    }

    /**
     * Cancel a trade.
     *
     * @param trade
     *            the trade to cancel
     * @return the result
     */
    public Result<WriteResult<Trade>> cancelTrade(final Trade trade) {
        return this.cancelTrade(trade.getId());
    }

    /**
     * Cancel a trade.
     *
     * @param tradeid
     *            the id of the trade to cancel
     * @return the result
     */
    public Result<WriteResult<Trade>> cancelTrade(final long tradeid) {
        return super.cancelTrade(this.userKey, tradeid);
    }

    /**
     * Takeout from a trade.
     *
     * @param trade
     *            the trade from which to take the output
     * @return the result
     */
    public Result<WriteResult<Trade>> takeoutFromTrade(final Trade trade) {
        return this.takeoutFromTrade(trade.getId());
    }

    /**
     * Takeout from a trade.
     *
     * @param tradeid
     *            the if of the trade from which to take the output
     * @return the result
     */
    public Result<WriteResult<Trade>> takeoutFromTrade(final long tradeid) {
        return super.takeoutTrade(this.userKey, tradeid);
    }

    public Result<WriteResult<Trade>> createTrade(final BUY_SELL buySell, final Item item, final int amount,
            final double price) {
        return this.createTrade(buySell, item, amount, SingleUserKtRequester.NUMBER_FORMAT.format(price));
    }

    private void checkUser(final T user) {
        if (!user.equals(this.userKey)) {
            throw new IllegalArgumentException(this.wrongUserErrorStart + user);
        }
    }

    @Override
    public Result<Permissions> getPermissions(final T userKey) {
        checkUser(userKey);
        return super.getPermissions(userKey);
    }

    @Override
    public Result<List<Item>> getItems(final T userKey) {
        checkUser(userKey);
        return super.getItems(userKey);
    }

    @Override
    public Result<Item> getItem(final T userKey, final String itemid) {
        checkUser(userKey);
        return super.getItem(userKey, itemid);
    }

    @Override
    public Result<List<Message>> getMessages(final T userKey) {
        checkUser(userKey);
        return super.getMessages(userKey);
    }

    @Override
    public Result<List<Payout>> getPayouts(final T userKey) {
        checkUser(userKey);
        return super.getPayouts(userKey);
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final T userKey, final BUY_SELL type, final String itemid,
            final int amount) {
        checkUser(userKey);
        return super.createPayout(userKey, type, itemid, amount);
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final T userKey, final String payoutid) {
        checkUser(userKey);
        return super.cancelPayout(userKey, payoutid);
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final T userKey, final String payoutid) {
        checkUser(userKey);
        return super.takeoutPayout(userKey, payoutid);
    }

    @Override
    public Result<PusherResult> authorizePusher(final T userKey, final String channel_name, final String socketId) {
        checkUser(userKey);
        return super.authorizePusher(userKey, channel_name, socketId);
    }

    @Override
    public Result<List<Trade>> getTrades(final T userKey) {
        checkUser(userKey);
        return super.getTrades(userKey);
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final T userKey, final BUY_SELL mode, final String item,
            final int amount, final String pricePerItem) {
        checkUser(userKey);
        return super.createTrade(userKey, mode, item, amount, pricePerItem);
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final T userKey, final long tradeId) {
        checkUser(userKey);
        return super.cancelTrade(userKey, tradeId);
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final T userKey, final long tradeId) {
        checkUser(userKey);
        return super.takeoutTrade(userKey, tradeId);
    }

}