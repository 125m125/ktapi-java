package de._125m125.kt.ktapi_java.simple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.reflect.TypeToken;

import de._125m125.kt.ktapi_java.simple.objects.HistoryEntry;
import de._125m125.kt.ktapi_java.simple.objects.Item;
import de._125m125.kt.ktapi_java.simple.objects.Message;
import de._125m125.kt.ktapi_java.simple.objects.OrderBookEntry;
import de._125m125.kt.ktapi_java.simple.objects.Payout;
import de._125m125.kt.ktapi_java.simple.objects.Permissions;
import de._125m125.kt.ktapi_java.simple.objects.Trade;
import de._125m125.kt.ktapi_java.simple.objects.User;
import de._125m125.kt.ktapi_java.simple.parsers.CsvParser;
import de._125m125.kt.ktapi_java.simple.parsers.JsonParser;
import de._125m125.kt.ktapi_java.simple.parsers.Parser;
import de._125m125.kt.ktapi_java.simple.parsers.StringParser;

/**
 *
 */
public class Kt {

    /** The algorithm used for signatures. */
    private static final String        SIGNATURE_ALGORITHM = "HmacSHA256";

    /** The base url of the api. */
    private static final String        BASE_URL            = "https://kt.125m125.de/api/";

    /** The maximum offset of the timestamp from the current time. */
    private static final long          MAX_OFFSET          = 4 * 60 * 1000;

    /** The DecimalFormat used to format prices of new trades. */
    private static final DecimalFormat df                  = new DecimalFormat("#.##");

    /**
     * The Enum BUY_SELL.
     */
    public static enum BUY_SELL {
        BUY,
        SELL;

        /** The opposite. */
        private BUY_SELL opposite;

        /**
         * Gets the opposite.
         *
         * @return the opposite
         */
        public BUY_SELL getOpposite() {
            return this.opposite;
        }

        static {
            BUY.opposite = SELL;
            SELL.opposite = BUY;
        }
    }

    /** The CsvParser used to parse csv answers. */
    private static final CsvParser  csvParser  = new CsvParser();

    /** The JsonParser used to parse json answers. */
    private static final JsonParser jsonParser = new JsonParser();

    static {
        Kt.df.setRoundingMode(RoundingMode.HALF_UP);
    }

    /** The user. */
    private final User user;

    /** The time offset. */
    private long       timeOffset;

    /** The last used timestamp. */
    private long       lastTime;

    /**
     * Instantiates a new kt.
     *
     * @param user
     *            the user
     */
    public Kt(final User user) {
        this.user = user;
    }

    /**
     * Synchronizes the time used for requests with the server by using
     * Cristian's algorithm.
     */
    public void syncTime() {
        final long start = System.currentTimeMillis();
        final String time = performPlainRequest("GET", "ping", null, false, new StringParser());
        final long end = System.currentTimeMillis();
        final long serverTime = Long.parseLong(time);

        this.timeOffset = serverTime - (start + end) / 2;
        this.lastTime = System.currentTimeMillis() + Kt.MAX_OFFSET;
    }

    /**
     * Gets the permissions.
     *
     * @return the permissions
     */
    public Permissions getPermissions() {
        return performRequest("GET", "permissions", null, true, Kt.jsonParser, new TypeToken<Permissions>() {
        });
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public List<Item> getItems() {
        return performRequest("GET", "itemlist", null, true, Kt.csvParser, Item.class);
    }

    /**
     * Gets the trades.
     *
     * @return the trades
     */
    public List<Trade> getTrades() {
        return performRequest("GET", "trades", null, true, Kt.csvParser, Trade.class);
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public List<Message> getMessages() {
        return performRequest("GET", "messages", null, true, Kt.csvParser, Message.class);
    }

    /**
     * Gets the payoutrequests.
     *
     * @return the payouts
     */
    public List<Payout> getPayouts() {
        return performRequest("GET", "payouts", null, true, Kt.csvParser, Payout.class);
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
    public List<OrderBookEntry> getOrderBook(final Item material, final int limit, final boolean summarize) {
        return getOrderBook(material.getId(), limit, summarize);
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
    public List<OrderBookEntry> getOrderBook(final String material, final int limit, final boolean summarize) {
        final Map<String, String> params = new HashMap<>();
        params.put("res", material);
        params.put("limit", String.valueOf(limit));
        params.put("summarize", String.valueOf(summarize));
        return performRequest("GET", "order", params, false, Kt.csvParser, OrderBookEntry.class);
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
    public List<HistoryEntry> getStatistics(final Item material, final int limit) {
        return getStatistics(material.getId(), limit);
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
    public List<HistoryEntry> getStatistics(final String material, final int limit) {
        final Map<String, String> params = new HashMap<>();
        params.put("res", material);
        params.put("limit", String.valueOf(limit));
        return performRequest("GET", "history", params, false, Kt.csvParser, HistoryEntry.class);
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
    public Result<Trade> createTrade(final BUY_SELL buySell, final Item item, final int count, final double price) {
        return createTrade(buySell, item.getId(), count, price);
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
    public Result<Trade> createTrade(final BUY_SELL buySell, final Item item, final double price) {
        return createTrade(buySell, item.getId(), Math.min(3456, ((Double) item.getAmount()).intValue()), price);
    }

    /**
     * Creates a new trade with the same parameters as the given trade.
     *
     * @param trade
     *            the trade to recreate
     * @return the result
     */
    public Result<Trade> recreateTrade(final Trade trade) {
        return createTrade(trade.getBuySell(), trade.getMaterialId(), trade.getAmount(), trade.getPrice());
    }

    /**
     * Creates a new order that completes a given order.
     *
     * @param trade
     *            the trade to fulfill
     * @return the result
     */
    public Result<Trade> fulfillTrade(final Trade trade) {
        return createTrade(trade.getBuySell().getOpposite(), trade.getMaterialId(), trade.getAmount() - trade.getSold(),
                trade.getPrice());
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

    /**
     * Cancel a trade.
     *
     * @param trade
     *            the trade to cancel
     * @return the result
     */
    public Result<Trade> cancelTrade(final Trade trade) {
        return cancelTrade(trade.getId());
    }

    /**
     * Cancel a trade.
     *
     * @param tradeid
     *            the id of the trade to cancel
     * @return the result
     */
    public Result<Trade> cancelTrade(final long tradeid) {
        final Map<String, String> params = new HashMap<>();
        params.put("cancel", "cancel");
        params.put("tradeid", String.valueOf(tradeid));
        return performRequest("POST", "trades", params, true, Kt.jsonParser, new TypeToken<Result<Trade>>() {
        });
    }

    /**
     * Takeout from a trade.
     *
     * @param trade
     *            the trade from which to take the output
     * @return the result
     */
    public Result<Trade> takeoutFromTrade(final Trade trade) {
        return takeoutFromTrade(trade.getId());
    }

    /**
     * Takeout from a trade.
     *
     * @param tradeid
     *            the if of the trade from which to take the output
     * @return the result
     */
    public Result<Trade> takeoutFromTrade(final long tradeid) {
        final Map<String, String> params = new HashMap<>();
        params.put("takeout", "takeout");
        params.put("tradeid", String.valueOf(tradeid));
        return performRequest("POST", "trades", params, true, Kt.jsonParser, new TypeToken<Result<Trade>>() {
        });
    }

    /**
     * Perform a plain request.
     *
     * @param <T>
     *            the generic type of the result
     * @param method
     *            the request method (GET, POST)
     * @param path
     *            the path of the request
     * @param params
     *            the parameters to send with the request
     * @param auth
     *            true if authentication is required
     * @param parser
     *            the parser for the result
     * @return the t
     */
    public <T> T performPlainRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<T, ?, ?> parser) {
        return performRequest(method, path, params, auth, parser, null);
    }

    /**
     * Perform a request to the kadcontrade api.
     *
     * @param <T>
     *            the generic type of the helper
     * @param <U>
     *            the generic type of the result
     * @param method
     *            the method of the request
     * @param path
     *            the path of the request
     * @param params
     *            the parameters for the request
     * @param auth
     *            true, if authentication is required for the request
     * @param parser
     *            the parser
     * @param helper
     *            the helper for the parser
     * @return the parsed result
     * @throws ClassCastException
     *             if the result of the parser is not of the Type &lt;U&gt;
     */
    @SuppressWarnings("unchecked")
    public <T, U> U performRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        final StringBuilder fullUrl = new StringBuilder(Kt.BASE_URL).append(path);
        final StringBuilder paramString = new StringBuilder();
        if ((params != null && !params.isEmpty()) || auth) {
            final TreeMap<String, String> sortedParams;
            if (params == null) {
                sortedParams = new TreeMap<>();
            } else {
                sortedParams = new TreeMap<>(params);
            }
            if (auth) {
                sortedParams.put("uid", this.user.getUID());
                sortedParams.put("tid", this.user.getTID());
                sortedParams.put("timestamp", String.valueOf(getCurrentTimestamp()));
            }
            for (final Entry<String, String> entry : sortedParams.entrySet()) {
                paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (auth) {
            final String signature = getSignatureFor(paramString.deleteCharAt(paramString.length() - 1).toString());
            paramString.append("&signature=").append(signature).append("&");
        }
        fullUrl.append("?").append(paramString.deleteCharAt(paramString.length() - 1));

        HttpsURLConnection connection = null;
        try {
            final URL url = new URL(fullUrl.toString());
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", parser.getResponseType());

            try (Reader r = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"))) {
                if (helper != null) {
                    return (U) parser.parse(r, helper);
                } else {
                    return (U) parser.parse(r);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the timestamp to use for a new request.
     *
     * @return the current timestamp
     */
    private synchronized long getCurrentTimestamp() {
        if (this.lastTime != 0) {
            if (this.lastTime < System.currentTimeMillis() - Kt.MAX_OFFSET) {
                this.lastTime = System.currentTimeMillis() + Kt.MAX_OFFSET;
            }
            return this.lastTime + this.timeOffset;
        }
        return System.currentTimeMillis();
    }

    /**
     * Gets the signature for a String.
     *
     * @param data
     *            the data for which to create a signature
     * @return the signature
     */
    private String getSignatureFor(final String data) {
        final SecretKeySpec signingKey = new SecretKeySpec(this.user.getTKN().getBytes(), Kt.SIGNATURE_ALGORITHM);
        String encoded = null;
        try {
            final Mac mac = Mac.getInstance(Kt.SIGNATURE_ALGORITHM);
            mac.init(signingKey);
            final byte[] rawHmac = mac.doFinal(data.getBytes(Charset.forName("UTF-8")));
            encoded = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encoded;
    }
}
