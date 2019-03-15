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

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.requester.integrationtest.RequesterIntegrationTest;
import de._125m125.kt.ktapi.requester.jersey.parsers.JacksonJsonProviderRegistrator;

public class KtJerseyRequesterIt extends RequesterIntegrationTest {

    @Override
    public KtRequester createRequester(final String baseUrl, final KtUserStore userStore) {
        return new KtJerseyRequester(baseUrl, null, JacksonJsonProviderRegistrator.INSTANCE);
    }

}
