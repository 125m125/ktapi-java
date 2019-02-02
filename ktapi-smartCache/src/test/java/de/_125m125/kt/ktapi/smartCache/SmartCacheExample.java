package de._125m125.kt.ktapi.smartCache;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import de._125m125.kt.ktapi.core.KtCachingRequester;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.pusher.KtPusher;
import de._125m125.kt.ktapi.pusher.KtPusherAuthorizer;
import de._125m125.kt.ktapi.retrofit.KtRetrofit;
import de._125m125.kt.ktapi.retrofit.requester.KtRetrofitRequester;

public class SmartCacheExample {
    public static void main(final String[] args) throws IOException {
        final TokenUser user = new TokenUser("1", "1", "1");
        final KtRetrofitRequester innerRequester = KtRetrofit
                .createDefaultRequester(new KtUserStore(user));
        final KtPusher pusher = new KtPusher(user,
                unescapedData -> new Gson().fromJson(unescapedData, Notification.class),
                new KtPusherAuthorizer(user.getKey(), innerRequester));
        final KtCachingRequester cachingRequester = new KtSmartCache(innerRequester, pusher);

        cachingRequester.getHistory("-1", 10, 0).addCallback(new Callback<List<HistoryEntry>>() {

            @Override
            public void onSuccess(final int status, final List<HistoryEntry> result) {
                final boolean valid = cachingRequester.isValidHistory("-1", result);
                System.out.println("#### REQUEST 1 SUCCESS ####");
                System.out.println("was cache hit: " + ((Timestamped) result).wasCacheHit());
                System.out.println("entry is valid: " + valid);
                cachingRequester.getHistory("-1", 1, 0)
                        .addCallback(new Callback<List<HistoryEntry>>() {

                            @Override
                            public void onSuccess(final int status,
                                    final List<HistoryEntry> result) {
                                final boolean valid = cachingRequester.isValidHistory("-1", result);
                                System.out.println("#### REQUEST 2 SUCCESS ####");
                                System.out.println(
                                        "was cache hit: " + ((Timestamped) result).wasCacheHit());
                                System.out.println("entry is valid: " + valid);

                                cachingRequester.invalidateHistory("-1");

                                final boolean stillValid = cachingRequester.isValidHistory("-1",
                                        result);
                                System.out.println("#### HISTORY INVALIDATED ####");
                                System.out.println(
                                        "entry is valid after invalidation: " + stillValid);
                            }

                            @Override
                            public void onFailure(final int status, final String message,
                                    final String humanReadableMessage) {
                                System.err.println(status + ": " + humanReadableMessage);
                            }

                            @Override
                            public void onError(final Throwable t) {
                                t.printStackTrace();
                            }
                        });
            }

            @Override
            public void onFailure(final int status, final String message,
                    final String humanReadableMessage) {
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
