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
package de._125m125.kt.ktapi.pusher;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.entities.PusherResult;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.users.UserKey;

public class KtPusherAuthorizer implements Authorizer {

    private final UserKey     userKey;
    private final KtRequester requester;

    public KtPusherAuthorizer(final UserKey userKey, final KtRequester requester) {
        this.userKey = userKey;
        this.requester = requester;
    }

    @Override
    public final String authorize(final String channelName, final String socketId)
            throws AuthorizationFailureException {
        final Result<PusherResult> pusherAuthResult = this.requester.authorizePusher(this.userKey,
                channelName, socketId);
        try {
            if (pusherAuthResult.isSuccessful()) {
                return pusherAuthResult.getContent().getAuthdata();
            } else {
                throw new AuthorizationFailureException(pusherAuthResult.getErrorMessage());
            }
        } catch (final Exception e) {
            throw new AuthorizationFailureException(e);
        }
    }
}