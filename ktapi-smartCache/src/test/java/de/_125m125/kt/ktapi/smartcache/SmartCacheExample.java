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
package de._125m125.kt.ktapi.smartcache;

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
                .createDefaultRequester("Smart Cache Example", new KtUserStore(user));
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
