package de._125m125.kt.ktapi_java.core;

import java.util.List;

import de._125m125.kt.ktapi_java.core.objects.HistoryEntry;
import de._125m125.kt.ktapi_java.core.objects.Item;
import de._125m125.kt.ktapi_java.core.objects.Message;
import de._125m125.kt.ktapi_java.core.objects.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.objects.Payout;
import de._125m125.kt.ktapi_java.core.objects.Permissions;
import de._125m125.kt.ktapi_java.core.objects.Trade;

public interface KtRequestUtil extends KtRequester {

    /**
     * Gets the permissions.
     *
     * @return the permissions
     */
    Permissions getPermissions();

    /**
     * Gets the items.
     *
     * @return the items
     */
    List<Item> getItems();

    /**
     * Gets the trades.
     *
     * @return the trades
     */
    List<Trade> getTrades();

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    List<Message> getMessages();

    /**
     * Gets the payoutrequests.
     *
     * @return the payouts
     */
    List<Payout> getPayouts();

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
    List<OrderBookEntry> getOrderBook(Item material, int limit, boolean summarize);

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
    List<OrderBookEntry> getOrderBook(String material, int limit, boolean summarize);

    /**
     * Gets the statistics.
     *
     * @param material
     *            the material
     * @param limit
     *            the maximum number of entries
     * @return the statistics
     */
    List<HistoryEntry> getStatistics(Item material, int limit);

    /**
     * Gets the statistics.
     *
     * @param material
     *            the materialid
     * @param limit
     *            the maximum number of entries
     * @return the statistics
     */
    List<HistoryEntry> getStatistics(String material, int limit);

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
    Result<Trade> createTrade(BUY_SELL buySell, Item item, int count, double price);

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
    Result<Trade> createTrade(BUY_SELL buySell, Item item, double price);

    /**
     * Creates a new trade with the same parameters as the given trade.
     *
     * @param trade
     *            the trade to recreate
     * @return the result
     */
    Result<Trade> recreateTrade(Trade trade);

    /**
     * Creates a new order that completes a given order.
     *
     * @param trade
     *            the trade to fulfill
     * @return the result
     */
    Result<Trade> fulfillTrade(Trade trade);

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
    Result<Trade> createTrade(BUY_SELL buySell, String item, int count, double price);

    /**
     * Cancel a trade.
     *
     * @param trade
     *            the trade to cancel
     * @return the result
     */
    Result<Trade> cancelTrade(Trade trade);

    /**
     * Cancel a trade.
     *
     * @param tradeid
     *            the id of the trade to cancel
     * @return the result
     */
    Result<Trade> cancelTrade(long tradeid);

    /**
     * Takeout from a trade.
     *
     * @param trade
     *            the trade from which to take the output
     * @return the result
     */
    Result<Trade> takeoutFromTrade(Trade trade);

    /**
     * Takeout from a trade.
     *
     * @param tradeid
     *            the if of the trade from which to take the output
     * @return the result
     */
    Result<Trade> takeoutFromTrade(long tradeid);

}