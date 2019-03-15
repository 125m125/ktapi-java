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
package de._125m125.kt.ktapi.requester.jersey.interceptors;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.UserKey;

public class BasicAuthFilter extends SelfRegistrator implements ClientRequestFilter {

    private final KtUserStore userStore;

    public BasicAuthFilter(final KtUserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public void filter(final ClientRequestContext context) throws IOException {
        Object property = context.getProperty("user");
        if (property == null) {
            property = context.getConfiguration().getProperty("user");
        }
        if (property != null && property instanceof UserKey) {
            final TokenUser user = this.userStore.get((UserKey) property, TokenUser.class);
            if (user != null) {
                context.getHeaders().add("Authorization",
                        "Basic " + Base64.getEncoder()
                                .encodeToString((user.getTokenId() + ":" + user.getToken())
                                        .getBytes(Charset.forName("UTF-8"))));
            }
        }
    }

}
