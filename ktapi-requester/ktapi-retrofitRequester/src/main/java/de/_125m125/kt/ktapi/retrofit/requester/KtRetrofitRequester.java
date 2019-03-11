package de._125m125.kt.ktapi.retrofit.requester;

import java.util.List;

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
import de._125m125.kt.ktapi.retrofit.requester.modifier.RetrofitModifier;
import de._125m125.kt.okhttp.helper.OkHttpClientBuilder;
import de._125m125.kt.okhttp.helper.modifier.CertificatePinnerAdder;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class KtRetrofitRequester implements KtRequester {
    private final KtRetrofitClient                       client;
    private final Converter<ResponseBody, ErrorResponse> errorConverter;
    private final OkHttpClient                           okHttpClient;
    private final OkHttpClientBuilder                    clientBuilder;

    public KtRetrofitRequester(final String url, final ClientModifier[] clientModifiers,
            final RetrofitModifier[] retrofitModifiers,
            final Converter<ResponseBody, ErrorResponse> errorConverter) {
        this(url,
                new OkHttpClientBuilder(clientModifiers).addModifier(new CertificatePinnerAdder()),
                retrofitModifiers, errorConverter);
    }

    public KtRetrofitRequester(final String url, final OkHttpClientBuilder clientBuilder,
            final RetrofitModifier[] retrofitModifiers,
            final Converter<ResponseBody, ErrorResponse> errorConverter) {
        this.clientBuilder = clientBuilder;
        this.errorConverter = errorConverter;

        this.okHttpClient = clientBuilder.build(this);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(url);
        if (retrofitModifiers != null) {
            for (final RetrofitModifier retrofitModifier : retrofitModifiers) {
                retrofitBuilder = retrofitModifier.modify(retrofitBuilder);
            }
        }
        this.client = retrofitBuilder.client(this.okHttpClient).build()
                .create(KtRetrofitClient.class);
    }

    @Override
    public void close() {
        this.clientBuilder.close(this);
    }

    @Override
    public Result<List<HistoryEntry>> getHistory(final String itemid, final int limit,
            final int offset) {
        return new RetrofitResult<>(this.client.getHistory(itemid, limit, offset),
                this.errorConverter);
    }

    @Override
    public Result<HistoryEntry> getLatestHistory(final String itemid) {
        return new RetrofitResult<>(this.client.getLatestHistory(itemid), this.errorConverter);
    }

    @Override
    public Result<List<OrderBookEntry>> getOrderBook(final String itemid, final int limit,
            final BuySellBoth mode, final boolean summarizeRemaining) {
        return new RetrofitResult<>(
                this.client.getOrderBook(itemid, limit, mode, summarizeRemaining),
                this.errorConverter);
    }

    @Override
    public Result<List<OrderBookEntry>> getBestOrderBookEntries(final String itemid,
            final BuySellBoth mode) {
        return new RetrofitResult<>(this.client.getBestOrderBookEntries(itemid, mode),
                this.errorConverter);
    }

    @Override
    public Result<Permissions> getPermissions(final UserKey userKey) {
        return new RetrofitResult<>(
                this.client.getPermissions(userKey.getUserId(), userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<List<ItemName>> getItemNames() {
        return new RetrofitResult<>(this.client.getItemNames(), this.errorConverter);
    }

    @Override
    public Result<List<Item>> getItems(final UserKey userKey) {
        return new RetrofitResult<>(
                this.client.getItems(userKey.getUserId(), userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<Item> getItem(final UserKey userKey, final String itemid) {
        return new RetrofitResult<>(
                this.client.getItem(userKey.getUserId(), itemid, userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<List<Message>> getMessages(final UserKey userKey, final int offset,
            final int limit) {
        return new RetrofitResult<>(this.client.getMessages(userKey.getUserId(),
                userKey.getIdentifier(), offset, limit), this.errorConverter);
    }

    @Override
    public Result<List<Payout>> getPayouts(final UserKey userKey, final int offset,
            final int limit) {
        return new RetrofitResult<>(
                this.client.getPayouts(userKey.getUserId(), userKey.getIdentifier(), offset, limit),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Payout>> createPayout(final UserKey userKey, final PayoutType type,
            final String itemid, final String amount) {
        return new RetrofitResult<>(this.client.createPayout(userKey.getUserId(), type.getComName(),
                itemid, amount, userKey.getIdentifier()), this.errorConverter);
    }

    @Override
    public Result<WriteResult<Payout>> cancelPayout(final UserKey userKey, final long payoutid) {
        return new RetrofitResult<>(
                this.client.cancelPayout(userKey.getUserId(), payoutid, userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Payout>> takeoutPayout(final UserKey userKey, final long payoutid) {
        return new RetrofitResult<>(
                this.client.takeoutPayout(userKey.getUserId(), payoutid, userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<PusherResult> authorizePusher(final UserKey userKey, final String channelName,
            final String socketId) {
        return new RetrofitResult<>(this.client.authorizePusher(userKey.getUserId(), channelName,
                socketId, userKey.getIdentifier()), this.errorConverter);
    }

    @Override
    public Result<List<Trade>> getTrades(final UserKey userKey) {
        return new RetrofitResult<>(
                this.client.getTrades(userKey.getUserId(), userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Trade>> createTrade(final UserKey userKey, final BuySell mode,
            final String item, final int amount, final String pricePerItem) {
        return new RetrofitResult<>(this.client.createTrade(userKey.getUserId(), mode, item, amount,
                pricePerItem, userKey.getIdentifier()), this.errorConverter);
    }

    @Override
    public Result<WriteResult<Trade>> cancelTrade(final UserKey userKey, final long tradeId) {
        return new RetrofitResult<>(
                this.client.cancelTrade(userKey.getUserId(), tradeId, userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<WriteResult<Trade>> takeoutTrade(final UserKey userKey, final long tradeId) {
        return new RetrofitResult<>(
                this.client.takeoutTrade(userKey.getUserId(), tradeId, userKey.getIdentifier()),
                this.errorConverter);
    }

    @Override
    public Result<Long> ping() {
        return new RetrofitResult<>(this.client.ping(), this.errorConverter);
    }

    @Override
    public Result<WriteResult<Long>> readBankStatement() {
        return new RetrofitResult<>(this.client.readBankStatement(), this.errorConverter);
    }

    public OkHttpClientBuilder getOkHttpClient() {
        return this.clientBuilder;
    }
}
