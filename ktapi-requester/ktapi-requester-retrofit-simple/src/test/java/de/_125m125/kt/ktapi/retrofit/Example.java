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
package de._125m125.kt.ktapi.retrofit;

import java.util.List;

import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.requester.retrofit.KtRetrofitRequester;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final TokenUser user = new TokenUser("1", "1", "1");
        final KtRetrofitRequester requester = KtRetrofit.createDefaultRequester("Retrofit Example",
                new KtUserStore(user));
        // final CertificateUser user = new CertificateUser("1", new
        // File("certificate.p12"), new char[] { 'a' });
        // final SingleUserKtRequester<CertificateUserKey> requester =
        // KtRetrofit
        // .createClientCertificateRequester(new KtUserStore(user),
        // user.getKey(), null);
        final Result<List<Message>> history = requester.getMessages(user.getKey());
        try {
            if (history.isSuccessful()) {
                System.out.println(history.getContent());
            } else {
                System.out.println(history.getStatus() + ": " + history.getErrorMessage());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final Result<Permissions> permissions = requester.getPermissions(user.getKey());
        permissions.addCallback(new Callback<Permissions>() {

            @Override
            public void onSuccess(final int status, final Permissions result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(final int status, final String message,
                    final String humanReadableMessage) {
                System.out.println("Request failed with status " + status + ": " + message);
            }

            @Override
            public void onError(final Throwable t) {
                t.printStackTrace();
            }
        });

        requester.ping().addCallback(Callback.successCallback(s -> r -> System.out.println(r)))
                .addCallback(Callback.failureCallback(
                        s -> s < 500 ? m -> h -> System.out.println("Client error: " + h)
                                : m -> h -> System.out.println("Server error: " + h)))
                .addCallback(Callback.errorCallback(Throwable::printStackTrace));

        Thread.sleep(10000);
        requester.close();
    }
}
