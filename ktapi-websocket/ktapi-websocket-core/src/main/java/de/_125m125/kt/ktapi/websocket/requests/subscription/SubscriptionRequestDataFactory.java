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

import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.User;

/**
 * A factory for creating SubscriptionRequestData objects. New Types of users can be added by
 * overriding {@link SubscriptionRequestDataFactory#create(String, User, boolean)} and creating
 * special SubscriptionRequestData instances for these user types.
 */
public class SubscriptionRequestDataFactory {

    /**
     * Creates a new instance of SubscriptionRequestData.
     *
     * @param channel
     *            the channel
     * @param user
     *            the user
     * @param selfCreated
     *            the self created
     * @return the subscription request data
     */
    public SubscriptionRequestData createSubscriptionRequestData(final String channel,
            final User user, final boolean selfCreated) {
        if (user instanceof TokenUser) {
            return new TokenSubscriptionRequestData(channel, (TokenUser) user, selfCreated);
        }
        return new SubscriptionRequestData(channel, user, selfCreated);
    }
}
