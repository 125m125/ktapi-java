package de._125m125.kt.ktapi_java.simple;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.KtRequestUtil;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.Parser;
import de._125m125.kt.ktapi_java.core.Result;
import de._125m125.kt.ktapi_java.core.objects.HistoryEntry;
import de._125m125.kt.ktapi_java.core.objects.Item;
import de._125m125.kt.ktapi_java.core.objects.Message;
import de._125m125.kt.ktapi_java.core.objects.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.objects.Payout;
import de._125m125.kt.ktapi_java.core.objects.Permissions;
import de._125m125.kt.ktapi_java.core.objects.Trade;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.simple.parsers.CsvParser;
import de._125m125.kt.ktapi_java.simple.parsers.JsonParser;

/**
 *
 */
public class Kt implements KtRequester, KtRequestUtil {

    /** The DecimalFormat used to format prices of new trades. */
    private static final DecimalFormat df         = new DecimalFormat("#.##");

    /** The CsvParser used to parse csv answers. */
    protected static final CsvParser   csvParser  = new CsvParser();

    /** The JsonParser used to parse json answers. */
    protected static final JsonParser  jsonParser = new JsonParser();

    private final KtRequester          requester;

    static {
        Kt.df.setRoundingMode(RoundingMode.HALF_UP);
    }

    /**
     * Instantiates a new kt.
     *
     * @param user
     *            the user
     */
    public Kt(final User user) {
        this.requester = new KtRequesterImpl(user);
        ((KtRequesterImpl) this.requester).syncTime();
    }

    public Kt(final KtRequester requester) {
        this.requester = requester;
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getPermissions()
     */
    @Override
    public Permissions getPermissions() {
        return performRequest("GET", "permissions", null, true, Kt.jsonParser, new TypeToken<Permissions>() {
        });
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getItems()
     */
    @Override
    public List<Item> getItems() {
        return performRequest("GET", "itemlist", null, true, Kt.csvParser, Item.class);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getTrades()
     */
    @Override
    public List<Trade> getTrades() {
        return performRequest("GET", "trades", null, true, Kt.csvParser, Trade.class);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getMessages()
     */
    @Override
    public List<Message> getMessages() {
        return performRequest("GET", "messages", null, true, Kt.csvParser, Message.class);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getPayouts()
     */
    @Override
    public List<Payout> getPayouts() {
        return performRequest("GET", "payouts", null, true, Kt.csvParser, Payout.class);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getOrderBook(de._125m125.kt.ktapi_java.simple.objects.Item, int, boolean)
     */
    @Override
    public List<OrderBookEntry> getOrderBook(final Item material, final int limit, final boolean summarize) {
        return getOrderBook(material.getId(), limit, summarize);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getOrderBook(java.lang.String, int, boolean)
     */
    @Override
    public List<OrderBookEntry> getOrderBook(final String material, final int limit, final boolean summarize) {
        final Map<String, String> params = new HashMap<>();
        params.put("res", material);
        params.put("limit", String.valueOf(limit));
        params.put("summarize", String.valueOf(summarize));
        return performRequest("GET", "order", params, false, Kt.csvParser, OrderBookEntry.class);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getStatistics(de._125m125.kt.ktapi_java.simple.objects.Item, int)
     */
    @Override
    public List<HistoryEntry> getStatistics(final Item material, final int limit) {
        return getStatistics(material.getId(), limit);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#getStatistics(java.lang.String, int)
     */
    @Override
    public List<HistoryEntry> getStatistics(final String material, final int limit) {
        final Map<String, String> params = new HashMap<>();
        params.put("res", material);
        params.put("limit", String.valueOf(limit));
        return performRequest("GET", "history", params, false, Kt.csvParser, HistoryEntry.class);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#createTrade(de._125m125.kt.ktapi_java.simple.Kt.BUY_SELL, de._125m125.kt.ktapi_java.simple.objects.Item, int, double)
     */
    @Override
    public Result<Trade> createTrade(final BUY_SELL buySell, final Item item, final int count, final double price) {
        return createTrade(buySell, item.getId(), count, price);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#createTrade(de._125m125.kt.ktapi_java.simple.Kt.BUY_SELL, de._125m125.kt.ktapi_java.simple.objects.Item, double)
     */
    @Override
    public Result<Trade> createTrade(final BUY_SELL buySell, final Item item, final double price) {
        return createTrade(buySell, item.getId(), Math.min(3456, ((Double) item.getAmount()).intValue()), price);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#recreateTrade(de._125m125.kt.ktapi_java.simple.objects.Trade)
     */
    @Override
    public Result<Trade> recreateTrade(final Trade trade) {
        return createTrade(trade.getBuySell(), trade.getMaterialId(), trade.getAmount(), trade.getPrice());
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#fulfillTrade(de._125m125.kt.ktapi_java.simple.objects.Trade)
     */
    @Override
    public Result<Trade> fulfillTrade(final Trade trade) {
        return createTrade(trade.getBuySell().getOpposite(), trade.getMaterialId(), trade.getAmount() - trade.getSold(),
                trade.getPrice());
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#createTrade(de._125m125.kt.ktapi_java.simple.Kt.BUY_SELL, java.lang.String, int, double)
     */
    @Override
    public Result<Trade> createTrade(final BUY_SELL buySell, final String item, final int count, final double price) {
        final Map<String, String> params = new HashMap<>();
        params.put("create", "create");
        params.put("bs", buySell == BUY_SELL.BUY ? "buy" : "sell");
        params.put("item", item);
        params.put("count", String.valueOf(count));
        params.put("price", Kt.df.format(price));
        return performRequest("POST", "trades", params, true, Kt.jsonParser, new TypeToken<Result<Trade>>() {
        });
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#cancelTrade(de._125m125.kt.ktapi_java.simple.objects.Trade)
     */
    @Override
    public Result<Trade> cancelTrade(final Trade trade) {
        return cancelTrade(trade.getId());
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#cancelTrade(long)
     */
    @Override
    public Result<Trade> cancelTrade(final long tradeid) {
        final Map<String, String> params = new HashMap<>();
        params.put("cancel", "cancel");
        params.put("tradeid", String.valueOf(tradeid));
        return performRequest("POST", "trades", params, true, Kt.jsonParser, new TypeToken<Result<Trade>>() {
        });
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#takeoutFromTrade(de._125m125.kt.ktapi_java.simple.objects.Trade)
     */
    @Override
    public Result<Trade> takeoutFromTrade(final Trade trade) {
        return takeoutFromTrade(trade.getId());
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequestUtil#takeoutFromTrade(long)
     */
    @Override
    public Result<Trade> takeoutFromTrade(final long tradeid) {
        final Map<String, String> params = new HashMap<>();
        params.put("takeout", "takeout");
        params.put("tradeid", String.valueOf(tradeid));
        return performRequest("POST", "trades", params, true, Kt.jsonParser, new TypeToken<Result<Trade>>() {
        });
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequester#performPlainRequest(java.lang.String, java.lang.String, java.util.Map, boolean, de._125m125.kt.ktapi_java.simple.parsers.Parser)
     */
    @Override
    public <T> T performPlainRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<T, ?, ?> parser) {
        return this.requester.performPlainRequest(method, path, params, auth, parser);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequester#performRequest(java.lang.String, java.lang.String, java.util.Map, boolean, de._125m125.kt.ktapi_java.simple.parsers.Parser, java.lang.Object)
     */
    @Override
    public <T, U> U performRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        return this.requester.performRequest(method, path, params, auth, parser, helper);
    }

}
