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
package de._125m125.kt.ktapi.websocket.requests.subscription;

import com.google.gson.annotations.Expose;

import de._125m125.kt.ktapi.core.users.User;

public class SubscriptionRequestData {
    @Expose
    private final String  channel;
    @Expose
    private final String  uid;
    @Expose
    private final boolean selfCreated;

    private final User    user;

    public SubscriptionRequestData(final String channel, final User user,
            final boolean selfCreated) {
        this.channel = channel;
        this.user = user;
        this.selfCreated = selfCreated;
        if (user != null) {
            this.uid = user.getUserId();
        } else {
            this.uid = null;
        }
    }

    public SubscriptionRequestData(final String channel) {
        this(channel, null, false);
    }

    public String getChannel() {
        return this.channel;
    }

    public String getUid() {
        return this.uid;
    }

    public boolean isSelfCreated() {
        return this.selfCreated;
    }

    public User getUser() {
        return this.user;
    }
}
