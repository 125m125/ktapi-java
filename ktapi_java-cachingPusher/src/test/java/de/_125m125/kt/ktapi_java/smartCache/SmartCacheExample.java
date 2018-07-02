package de._125m125.kt.ktapi_java.smartCache;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import de._125m125.kt.ktapi_java.core.KtCachingRequester;
import de._125m125.kt.ktapi_java.core.KtUserStore;
import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Notification;
import de._125m125.kt.ktapi_java.core.entities.User;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.Callback;
import de._125m125.kt.ktapi_java.pusher.KtPusherAuthorizer;
import de._125m125.kt.ktapi_java.pusher.PusherKt;
import de._125m125.kt.ktapi_java.retrofit.KtRetrofit;
import de._125m125.kt.ktapi_java.retrofitRequester.KtRetrofitRequester;

public class SmartCacheExample {
    public static void main(final String[] args) throws IOException {
        final User user = new User("1", "1", "1");
        final KtRetrofitRequester innerRequester = KtRetrofit.createDefaultRequester(new KtUserStore<>(user));
        final PusherKt<UserKey> pusher = new PusherKt<>(user,
                unescapedData -> new Gson().fromJson(unescapedData, Notification.class),
                new KtPusherAuthorizer<>(user, innerRequester));
        final KtCachingRequester<UserKey> cachingRequester = new KtCachingRequesterIml<>(innerRequester, pusher);

        cachingRequester.getHistory("-1", 10, 0).addCallback(new Callback<List<HistoryEntry>>() {

            @Override
            public void onSuccess(final int status, final List<HistoryEntry> result) {
                final boolean valid = cachingRequester.isValidHistory("-1", result);
                System.out.println("#### REQUEST 1 SUCCESS ####");
                System.out.println("was cache hit: " + ((Timestamped) result).wasCacheHit());
                System.out.println("entry is valid: " + valid);
                cachingRequester.getHistory("-1", 1, 0).addCallback(new Callback<List<HistoryEntry>>() {

                    @Override
                    public void onSuccess(final int status, final List<HistoryEntry> result) {
                        final boolean valid = cachingRequester.isValidHistory("-1", result);
                        System.out.println("#### REQUEST 2 SUCCESS ####");
                        System.out.println("was cache hit: " + ((Timestamped) result).wasCacheHit());
                        System.out.println("entry is valid: " + valid);

                        cachingRequester.invalidateHistory("-1");

                        final boolean stillValid = cachingRequester.isValidHistory("-1", result);
                        System.out.println("#### HISTORY INVALIDATED ####");
                        System.out.println("entry is valid after invalidation: " + stillValid);
                    }

                    @Override
                    public void onFailure(final int status, final String message, final String humanReadableMessage) {
                        System.err.println(status + ": " + humanReadableMessage);
                    }

                    @Override
                    public void onError(final Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(final int status, final String message, final String humanReadableMessage) {
                System.err.println(status + ": " + humanReadableMessage);
            }

            @Override
            public void onError(final Throwable t) {
                t.printStackTrace();
            }
        });

        try {
            Thread.sleep(10000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        innerRequester.close();
        cachingRequester.close();
    }
}
