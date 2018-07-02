package de._125m125.kt.ktapi_java.core;

import java.text.NumberFormat;
import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.core.results.WriteResult;

public class SingleUserKtRequester<T extends UserKey> {

    private static final NumberFormat NUMBER_FORMAT;
    static {
        NUMBER_FORMAT = NumberFormat.getInstance();
        SingleUserKtRequester.NUMBER_FORMAT.setMaximumFractionDigits(2);
        SingleUserKtRequester.NUMBER_FORMAT.setGroupingUsed(false);
    }

    private final T                      user;
    private final KtRequester<? super T> requester;

    public SingleUserKtRequester(final T user, final KtRequester<? super T> requester) {
        this.user = user;
        this.requester = requester;
    }

    public T getUser() {
        return this.user;
    }

    /**
     * Gets the permissions.
     *
     * @return the permissions
     */
    public Result<Permissions> getPermissions() {
        return this.requester.getPermissions(this.user);
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public Result<List<Item>> getItems() {
        return this.requester.getItems(this.user);
    }

    /**
     * Gets the trades.
     *
     * @return the trades
     */
    public Result<List<Trade>> getTrades() {
        return this.requester.getTrades(this.user);
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public Result<List<Message>> getMessages() {
        return this.requester.getMessages(this.user);
    }

    /**
     * Gets the payoutrequests.
     *
     * @return the payouts
     */
    public Result<List<Payout>> getPayouts() {
        return this.requester.getPayouts(this.user);
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
    public Result<List<OrderBookEntry>> getOrderBook(final String material, final int limit, final BUY_SELL_BOTH mode,
            final boolean summarize) {
        return this.requester.getOrderBook(material, limit, mode, summarize);
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
    public Result<List<HistoryEntry>> getHistory(final String material, final int limit, final int offset) {
        return this.requester.getHistory(material, limit, offset);
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
        return this.requester.createTrade(this.user, buySell, item, count, price);
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
        return this.requester.cancelTrade(this.user, tradeid);
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
        return this.requester.takeoutTrade(this.user, tradeid);
    }

    public Result<WriteResult<Trade>> createTrade(final BUY_SELL buySell, final Item item, final int amount,
            final double price) {
        return this.createTrade(buySell, item, amount, SingleUserKtRequester.NUMBER_FORMAT.format(price));
    }

}