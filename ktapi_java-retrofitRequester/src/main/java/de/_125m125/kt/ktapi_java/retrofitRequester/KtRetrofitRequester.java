package de._125m125.kt.ktapi_java.retrofitRequester;

import java.util.List;

import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.BUY_SELL_BOTH;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.ErrorResponse;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.core.results.WriteResult;
import de._125m125.kt.ktapi_java.retrofitRequester.builderModifier.ClientModifier;
import de._125m125.kt.ktapi_java.retrofitRequester.builderModifier.RetrofitModifier;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class KtRetrofitRequester implements KtRequester<UserKey> {
    private final KtRetrofitClient                       client;
    private final Converter<ResponseBody, ErrorResponse> errorConverter;
    private final OkHttpClient                           okHttpClient;

    public KtRetrofitRequester(final String url, final ClientModifier[] clientModifiers,
            final RetrofitModifier[] retrofitModifiers, final Converter<ResponseBody, ErrorResponse> errorConverter) {
        this.errorConverter = errorConverter;

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (clientModifiers != null) {
            for (final ClientModifier clientModifier : clientModifiers) {
                clientBuilder = clientModifier.modify(clientBuilder);
            }
        }
        clientBuilder = clientBuilder.addInterceptor(chain -> {
            final Request r = chain.request().newBuilder().removeHeader("userKey").build();
            return chain.proceed(r);
        });
        clientBuilder = clientBuilder.addInterceptor((chain) -> {
            Request request = chain.request();
            if (request.method().equals("GET")) {
                return chain.proceed(request);
            }
            if (request.header("content-type") != null) {
                return chain.proceed(request);
            }
            request = request.newBuilder().addHeader("content-type", "application/x-www-form-urlencoded").build();
            return chain.proceed(request);
        });
        this.okHttpClient = clientBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(url);
        if (retrofitModifiers != null) {
            for (final RetrofitModifier retrofitModifier : retrofitModifiers) {
                retrofitBuilder = retrofitModifier.modify(retrofitBuilder);
            }
        }
        this.client = retrofitBuilder.client(this.okHttpClient).build().create(KtRetrofitClient.class);
    }

    @Override
    public void close() {
        this.okHttpClient.connectionPool().evictAll();
        this.okHttpClient.dispatcher().executorService().shutdown();
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit, final int offset) {
        return new RetrofitResult<>(this.client.getHistory(itemid, limit, offset), this.errorConverter);
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        return new RetrofitResult<>(this.client.getLatestHistory(itemid), this.errorConverter);
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit, final BUY_SELL_BOTH mode,
            final boolean summarizeRemaining) {
        return new RetrofitResult<>(this.client.getOrderBook(itemid, limit, mode, summarizeRemaining),
                this.errorConverter);
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid, final BUY_SELL_BOTH mode) {
        return new RetrofitResult<>(this.client.getBestOrderBookEntries(itemid, mode), this.errorConverter);
    }

    @Override
    public Result<Permissions> getPermissions(final UserKey user) {
        return new RetrofitResult<>(this.client.getPermissions(user.getUid(), UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<List<Item>> getItems(final UserKey user) {
        return new RetrofitResult<>(this.client.getItems(user.getUid(), UserKey.toString(user)), this.errorConverter);
    }

    @Override
    public Result<Item> getItem(final UserKey user, final String itemid) {
        return new RetrofitResult<>(this.client.getItem(user.getUid(), itemid, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<List<Message>> getMessages(final UserKey user) {
        return new RetrofitResult<>(this.client.getMessages(user.getUid(), UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<List<Payout>> getPayouts(final UserKey user) {
        return new RetrofitResult<>(this.client.getPayouts(user.getUid(), UserKey.toString(user)), this.errorConverter);
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final UserKey user, final BUY_SELL type, final String itemid,
            final int amount) {
        return new RetrofitResult<>(
                this.client.createPayout(user.getUid(), type, itemid, amount, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final UserKey user, final String payoutid) {
        return new RetrofitResult<>(this.client.cancelPayout(user.getUid(), payoutid, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final UserKey user, final String payoutid) {
        return new RetrofitResult<>(this.client.takeoutPayout(user.getUid(), payoutid, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<PusherResult> authorizePusher(final UserKey user, final String channel_name, final String socketId) {
        return new RetrofitResult<>(
                this.client.authorizePusher(user.getUid(), channel_name, socketId, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<List<Trade>> getTrades(final UserKey user) {
        return new RetrofitResult<>(this.client.getTrades(user.getUid(), UserKey.toString(user)), this.errorConverter);
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final UserKey user, final BUY_SELL mode, final String item,
            final int amount, final String pricePerItem) {
        return new RetrofitResult<>(
                this.client.createTrade(user.getUid(), mode, item, amount, pricePerItem, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final UserKey user, final long tradeId) {
        return new RetrofitResult<>(this.client.cancelTrade(user.getUid(), tradeId, UserKey.toString(user)),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final UserKey user, final long tradeId) {
        return new RetrofitResult<>(this.client.takeoutTrade(user.getUid(), tradeId, UserKey.toString(user)),
                this.errorConverter);
    }

}
