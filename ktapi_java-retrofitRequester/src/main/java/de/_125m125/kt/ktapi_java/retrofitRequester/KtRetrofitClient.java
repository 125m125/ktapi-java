package de._125m125.kt.ktapi_java.retrofitRequester;

import java.util.List;

import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.BUY_SELL_BOTH;
import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Item;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.Payout;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.Trade;
import de._125m125.kt.ktapi_java.core.results.WriteResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface KtRetrofitClient {

    @GET("history/{itemid}")
    Call<List<HistoryEntry>> getHistory(@Path("itemid") String itemid, @Query("limit") int limit,
            @Query("offset") int offset);

    @GET("history/{itemid}")
    Call<HistoryEntry> getLatestHistory(@Path("itemid") String itemid);

    @GET("orderbook/{itemid}")
    Call<List<OrderBookEntry>> getOrderBook(@Path("itemid") String itemid, @Query("limit") int limit,
            @Query("mode") BUY_SELL_BOTH mode, @Query("summarize") boolean summarizeRemaining);

    @GET("orderbook/{itemid}/best")
    Call<List<OrderBookEntry>> getBestOrderBookEntries(@Path("itemid") String itemid,
            @Query("mode") BUY_SELL_BOTH mode);

    @GET("permissions/{userid}")
    Call<Permissions> getPermissions(@Path("userid") String userid, @Header("userKey") String String);

    @GET("users/{userid}/items")
    Call<List<Item>> getItems(@Path("userid") String userid, @Header("userKey") String String);

    @GET("users/{userid}/items/{itemid}")
    Call<Item> getItem(@Path("userid") String userid, @Path("itemid") String itemid, @Header("userKey") String String);

    @GET("users/{userid}/messages")
    Call<List<Message>> getMessages(@Path("userid") String userid, @Header("userKey") String String);

    @GET("users/{userid}/payouts")
    Call<List<Payout>> getPayouts(@Path("userid") String userid, @Header("userKey") String String);

    @GET("users/{userid}/payouts/{payoutid}")
    Call<List<Payout>> getPayouts(@Path("userid") String userid, @Path("payoutid") String payoutid,
            @Header("userKey") String String);

    @POST("users/{userid}/payouts")
    @FormUrlEncoded
    Call<WriteResult<Payout>> createPayout(@Path("userid") String userid, @Field("type") BUY_SELL type,
            @Field("item") String itemid, @Field("amount") int amount, @Header("userKey") String String);

    @POST("users/{userid}/payouts/{payoutid}/cancel")
    Call<WriteResult<Payout>> cancelPayout(@Path("userid") String userid, @Path("payoutid") String payoutid,
            @Header("userKey") String String);

    @POST("users/{userid}/payouts/{payoutid}/takout")
    Call<WriteResult<Payout>> takeoutPayout(@Path("userid") String userid, @Path("payoutid") String payoutid,
            @Header("userKey") String String);

    @POST("pusher/authenticate")
    @FormUrlEncoded
    Call<PusherResult> authorizePusher(@Query("user") final String user,
            @Field("channel_name") final String channelname, @Field("socketId") final String socketId,
            @Header("userKey") String String);

    @GET("users/{user}/orders")
    Call<List<Trade>> getTrades(@Path("user") final String user, @Header("userKey") String String);

    @POST("users/{user}/orders")
    @FormUrlEncoded
    Call<WriteResult<Trade>> createTrade(@Path("user") final String user, @Field("buySell") final BUY_SELL buySell,
            @Field("item") final String item, @Field("amount") final int amount, @Field("price") final String price,
            @Header("userKey") String String);

    @POST("users/{user}/orders/{orderId}/cancel")
    Call<WriteResult<Trade>> cancelTrade(@Path("user") final String user, @Path("orderId") final long orderId,
            @Header("userKey") String String);

    @POST("users/{user}/orders/{orderId}/takeout")
    Call<WriteResult<Trade>> takeoutTrade(@Path("user") final String user, @Path("orderId") final long orderId,
            @Header("userKey") String String);

}
