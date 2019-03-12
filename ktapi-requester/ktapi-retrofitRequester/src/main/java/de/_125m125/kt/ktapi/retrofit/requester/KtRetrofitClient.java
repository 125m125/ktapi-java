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
package de._125m125.kt.ktapi.retrofit.requester;

import java.util.List;

import de._125m125.kt.ktapi.core.BuySell;
import de._125m125.kt.ktapi.core.BuySellBoth;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.ItemName;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.WriteResult;
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

    @GET("history/{itemid}/latest")
    Call<HistoryEntry> getLatestHistory(@Path("itemid") String itemid);

    @GET("orderbook/{itemid}")
    Call<List<OrderBookEntry>> getOrderBook(@Path("itemid") String itemid,
            @Query("limit") int limit, @Query("mode") BuySellBoth mode,
            @Query("summarize") boolean summarizeRemaining);

    @GET("orderbook/{itemid}/best")
    Call<List<OrderBookEntry>> getBestOrderBookEntries(@Path("itemid") String itemid,
            @Query("mode") BuySellBoth mode);

    @GET("permissions/{userid}")
    Call<Permissions> getPermissions(@Path("userid") String userid,
            @Header("userKey") String userKey);

    @GET("itemnames")
    Call<List<ItemName>> getItemNames();

    @GET("users/{userid}/items")
    Call<List<Item>> getItems(@Path("userid") String userid, @Header("userKey") String userKey);

    @GET("users/{userid}/items/{itemid}")
    Call<Item> getItem(@Path("userid") String userid, @Path("itemid") String itemid,
            @Header("userKey") String userKey);

    @GET("users/{userid}/messages")
    Call<List<Message>> getMessages(@Path("userid") String userid,
            @Header("userKey") String userKey, @Query("offset") int offset,
            @Query("limit") int limit);

    @GET("users/{userid}/payouts")
    Call<List<Payout>> getPayouts(@Path("userid") String userid, @Header("userKey") String userKey,
            @Query("offset") int offset, @Query("limit") int limit);

    @GET("users/{userid}/payouts/{payoutid}")
    Call<List<Payout>> getPayouts(@Path("userid") String userid, @Path("payoutid") String payoutid,
            @Header("userKey") String userKey);

    @POST("users/{userid}/payouts")
    @FormUrlEncoded
    Call<WriteResult<Payout>> createPayout(@Path("userid") String userid,
            @Field("type") String type, @Field("item") String itemid,
            @Field("amount") String amount, @Header("userKey") String userKey);

    @POST("users/{userid}/payouts/{payoutid}/cancel")
    Call<WriteResult<Payout>> cancelPayout(@Path("userid") String userid,
            @Path("payoutid") long payoutid, @Header("userKey") String userKey);

    @POST("users/{userid}/payouts/{payoutid}/takout")
    Call<WriteResult<Payout>> takeoutPayout(@Path("userid") String userid,
            @Path("payoutid") long payoutid, @Header("userKey") String userKey);

    @POST("pusher/authenticate")
    @FormUrlEncoded
    Call<PusherResult> authorizePusher(@Query("user") final String user,
            @Field("channel_name") final String channelname,
            @Field("socketId") final String socketId, @Header("userKey") String userKey);

    @GET("users/{user}/orders")
    Call<List<Trade>> getTrades(@Path("user") final String user, @Header("userKey") String userKey);

    @POST("users/{user}/orders")
    @FormUrlEncoded
    Call<WriteResult<Trade>> createTrade(@Path("user") final String user,
            @Field("buySell") final BuySell buySell, @Field("item") final String item,
            @Field("amount") final int amount, @Field("price") final String price,
            @Header("userKey") String userKey);

    @POST("users/{user}/orders/{orderId}/cancel")
    Call<WriteResult<Trade>> cancelTrade(@Path("user") final String user,
            @Path("orderId") final long orderId, @Header("userKey") String userKey);

    @POST("users/{user}/orders/{orderId}/takeout")
    Call<WriteResult<Trade>> takeoutTrade(@Path("user") final String user,
            @Path("orderId") final long orderId, @Header("userKey") String userKey);

    @GET("ping")
    Call<Long> ping();

    @POST("bank/read")
    Call<WriteResult<Long>> readBankStatement();
}
