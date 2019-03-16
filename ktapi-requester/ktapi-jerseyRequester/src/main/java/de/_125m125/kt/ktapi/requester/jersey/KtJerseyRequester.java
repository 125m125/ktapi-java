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
package de._125m125.kt.ktapi.requester.jersey;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import de._125m125.kt.ktapi.core.BuySell;
import de._125m125.kt.ktapi.core.BuySellBoth;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.PayoutType;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.ItemName;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.requester.jersey.interceptors.HeaderAdderFilter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "Allow use of anonymous GenericTypes")
public class KtJerseyRequester implements KtRequester {
    private final Client    client;
    private final WebTarget target;

    public KtJerseyRequester(final String appName, final String url) {
        this(appName, url, null);
    }

    @SafeVarargs
    public KtJerseyRequester(final String appName, final String url, final Configuration config,
            final Function<ClientBuilder, ClientBuilder>... clientModifier) {
        this(appName, url, config,
                Arrays.stream(clientModifier).reduce(Function.identity(), Function::andThen));
    }

    public KtJerseyRequester(final String appName,
            final Function<ClientBuilder, ClientBuilder> clientModifier) {
        this(appName, null, null, clientModifier);
    }

    public KtJerseyRequester(final String appName, final Configuration config,
            final Function<ClientBuilder, ClientBuilder> clientModifier) {
        this(appName, null, config, clientModifier);
    }

    public KtJerseyRequester(final String appName, final String url, final Configuration config,
            final Function<ClientBuilder, ClientBuilder> clientModifier) {
        ClientBuilder builder = ClientBuilder.newBuilder();
        // .register(new HeaderAdderFilter("user-agent", "KtApi-Java-Jersey-" + appName));
        if (config != null) {
            builder = builder.withConfig(config);
        }
        if (clientModifier != null) {
            builder = clientModifier.apply(builder);
        }
        builder = builder
                .register(new HeaderAdderFilter("user-agent", "KtApi-Java-Jersey-" + appName) {
                });
        this.client = builder.build();
        this.target = this.client.target(url == null ? KtRequester.DEFAULT_BASE_URL : url);
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit,
            final int offset) {
        final InvocationCallbackResult<List<HistoryEntry>> result =
                new InvocationCallbackResult<>(new GenericType<List<HistoryEntry>>() {
                });
        this.target.path("history").path(itemid).queryParam("limit", limit)
                .queryParam("offset", offset).request().async().get(result);
        return result;
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        final InvocationCallbackResult<HistoryEntry> result =
                new InvocationCallbackResult<>(HistoryEntry.class);
        this.target.path("history").path(itemid).path("latest").request().async().get(result);
        return result;
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit,
            final BuySellBoth mode, final boolean summarizeRemaining) {
        final InvocationCallbackResult<List<OrderBookEntry>> result =
                new InvocationCallbackResult<>(new GenericType<List<OrderBookEntry>>() {
                });
        this.target.path("orderbook").path(itemid).queryParam("limit", limit)
                .queryParam("mode", mode).queryParam("summarize", summarizeRemaining).request()
                .async().get(result);
        return result;
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid,
            final BuySellBoth mode) {
        final InvocationCallbackResult<List<OrderBookEntry>> result =
                new InvocationCallbackResult<>(new GenericType<List<OrderBookEntry>>() {
                });
        this.target.path("orderbook").path(itemid).path("best").queryParam("mode", mode).request()
                .async().get(result);
        return result;
    }

    @Override
    public Result<Permissions> getPermissions(final UserKey userKey) {
        final InvocationCallbackResult<Permissions> result =
                new InvocationCallbackResult<>(Permissions.class);
        final WebTarget property =
                this.target.path("permissions").path(userKey.getUserId()).property("user", userKey);
        property.request().async().get(result);
        return result;
    }

    @Override
    public Result<List<ItemName>> getItemNames() {
        final InvocationCallbackResult<List<ItemName>> result =
                new InvocationCallbackResult<>(new GenericType<List<ItemName>>() {
                });
        this.target.path("itemnames").request().async().get(result);
        return result;
    }

    @Override
    public Result<List<Item>> getItems(final UserKey userKey) {
        final InvocationCallbackResult<List<Item>> result =
                new InvocationCallbackResult<>(new GenericType<List<Item>>() {
                });
        userTarget(userKey).path("items").request().async().get(result);
        return result;
    }

    @Override
    public Result<Item> getItem(final UserKey userKey, final String itemid) {
        final InvocationCallbackResult<Item> result = new InvocationCallbackResult<>(Item.class);
        userTarget(userKey).path("items").path(itemid).request().async().get(result);
        return result;
    }

    @Override
    public Result<List<Message>> getMessages(final UserKey userKey, final int offset,
            final int limit) {
        final InvocationCallbackResult<List<Message>> result =
                new InvocationCallbackResult<>(new GenericType<List<Message>>() {
                });
        userTarget(userKey).path("messages").request().async().get(result);
        return result;
    }

    @Override
    public Result<List<Payout>> getPayouts(final UserKey userKey, final int offset,
            final int limit) {
        final InvocationCallbackResult<List<Payout>> result =
                new InvocationCallbackResult<>(new GenericType<List<Payout>>() {
                });
        userTarget(userKey).path("payouts").request().async().get(result);
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final UserKey userKey, final PayoutType type,
            final String itemid, final String amount) {
        final InvocationCallbackResult<WriteResult<Payout>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Payout>>() {
                });
        final Form form = new Form().param("type", type.getComName()).param("item", itemid)
                .param("amount", amount);
        userTarget(userKey).path("payouts").request().async()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final UserKey userKey, final long payoutid) {
        final InvocationCallbackResult<WriteResult<Payout>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Payout>>() {
                });
        userTarget(userKey).path("payouts").path(Long.toString(payoutid)).path("cancel").request()
                .async()
                .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final UserKey userKey, final long payoutid) {
        final InvocationCallbackResult<WriteResult<Payout>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Payout>>() {
                });
        userTarget(userKey).path("payouts").path(Long.toString(payoutid)).path("takeout").request()
                .async()
                .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<PusherResult> authorizePusher(final UserKey userKey, final String channelName,
            final String socketId) {
        final InvocationCallbackResult<PusherResult> result =
                new InvocationCallbackResult<>(PusherResult.class);
        final Form form = new Form().param("channel_name", channelName).param("socketId", socketId);
        this.target.path("pusher/authenticate").queryParam("user", userKey.getUserId())
                .property("user", userKey).request().async()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<List<Trade>> getTrades(final UserKey userKey) {
        final InvocationCallbackResult<List<Trade>> result =
                new InvocationCallbackResult<>(new GenericType<List<Trade>>() {
                });
        userTarget(userKey).path("orders").request().async().get(result);
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final UserKey userKey, final BuySell mode,
            final String item, final int amount, final String pricePerItem) {
        final InvocationCallbackResult<WriteResult<Trade>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Trade>>() {
                });
        final Form form = new Form().param("buySell", mode.toString()).param("item", item)
                .param("amount", Integer.toString(amount)).param("price", pricePerItem);
        userTarget(userKey).path("orders").request().async()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final UserKey userKey, final long tradeId) {
        final InvocationCallbackResult<WriteResult<Trade>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Trade>>() {
                });
        userTarget(userKey).path("orders").path(Long.toString(tradeId)).path("cancel").request()
                .async()
                .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final UserKey userKey, final long tradeId) {
        final InvocationCallbackResult<WriteResult<Trade>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Trade>>() {
                });
        userTarget(userKey).path("orders").path(Long.toString(tradeId)).path("takeout").request()
                .async()
                .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    @Override
    public Result<Long> ping() {
        final InvocationCallbackResult<Long> result = new InvocationCallbackResult<>(Long.class);
        this.target.path("ping").request().async().get(result);
        return result;
    }

    @Override
    public Result<WriteResult<Long>> readBankStatement() {
        final InvocationCallbackResult<WriteResult<Long>> result =
                new InvocationCallbackResult<>(new GenericType<WriteResult<Long>>() {
                });
        this.target.path("bank/read").request().async()
                .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE), result);
        return result;
    }

    private WebTarget userTarget(final UserKey userKey) {
        return this.target.path("users").path(userKey.getUserId()).property("user", userKey);
    }

    public static class InvocationCallbackResult<T> extends Result<T>
            implements InvocationCallback<Response> {

        private final Class<T>       resultClass;
        private final GenericType<T> resultType;

        public InvocationCallbackResult(final Class<T> resultClass) {
            this.resultClass = Objects.requireNonNull(resultClass);
            this.resultType = null;
        }

        public InvocationCallbackResult(final GenericType<T> resultType) {
            this.resultType = Objects.requireNonNull(resultType);
            this.resultClass = null;
        }

        @Override
        public void completed(final Response response) {
            if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
                if (this.resultClass != null) {
                    setSuccessResult(response.getStatus(), response.readEntity(this.resultClass));
                } else {
                    setSuccessResult(response.getStatus(), response.readEntity(this.resultType));
                }
            } else {
                try {
                    response.bufferEntity();
                    setFailureResult(response.readEntity(ErrorResponse.class));
                } catch (final ProcessingException e) {
                    e.printStackTrace();
                    try {
                        setFailureResult(new ErrorResponse(response.getStatus(),
                                response.readEntity(String.class), "An unknown Error occurred"));
                    } catch (final ProcessingException e1) {
                        setFailureResult(new ErrorResponse(response.getStatus(),
                                "unknown : " + e1.toString(), "An unknown Error occurred"));
                    }
                }
            }
        }

        @Override
        public void failed(final Throwable throwable) {
            setErrorResult(throwable);
        }

    }
}
