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

import java.util.List;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.PayoutType;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Permissions;
import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.requester.jersey.interceptors.BasicAuthFilter;
import de._125m125.kt.ktapi.requester.jersey.parsers.JacksonJsonProviderRegistrator;

public class Example {

    public static void main(final String[] args) throws Exception {
        final TokenUser user = new TokenUser("1", "1", "1");
        final KtUserStore store = new KtUserStore(user);

        // final ClientConfig config = new ClientConfig();
        // config.connectorProvider(new
        // HttpUrlConnectorProvider().connectionFactory(url ->
        // (HttpURLConnection) url
        // .openConnection(new Proxy(Proxy.Type.HTTP, new
        // InetSocketAddress("localhost", 8080)))));

        final KtRequester requester = new KtJerseyRequester(
                new BasicAuthFilter(store).andThen(JacksonJsonProviderRegistrator.INSTANCE));

        requester.getPermissions(user.getKey()).addCallback(new Callback<Permissions>() {

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
        final Result<List<Message>> messages = requester.getMessages(user.getKey());
        try {
            if (messages.isSuccessful()) {
                System.out.println(messages.getContent());
            } else {
                System.out.println("Request failed with status " + messages.getStatus() + ": "
                        + messages.getErrorMessage());
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        requester.createPayout(user.getKey(), PayoutType.KADCON, "-1", "1")
                .addCallback(new Callback<WriteResult<Payout>>() {

                    @Override
                    public void onSuccess(final int status, final WriteResult<Payout> result) {
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

        Thread.sleep(10000);
        requester.close();
    }

}
