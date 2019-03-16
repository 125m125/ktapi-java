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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;

import de._125m125.kt.ktapi.core.results.WriteResult;

public class InvalidationCallbackTest {

    private InvalidationCallback<String> uut;
    private KtSmartCache                 requester;

    @Before
    public void setUp() throws Exception {
        this.requester = mock(KtSmartCache.class);
        this.uut = new InvalidationCallback<>(this.requester, "testKey");
    }

    @Test
    public void testOnErrorInvalidates() {
        this.uut.onError(new Throwable());

        verify(this.requester, times(1)).invalidate("testKey", null);
    }

    @Test
    public void testOnFailureDoesNotInvalidateOnClientError() {
        this.uut.onFailure(400, "invalid", "testing");

        verify(this.requester, times(0)).invalidate(any(), any());
    }

    @Test
    public void testOnFailureInvalidatesOnServerError() {
        this.uut.onFailure(500, "invalid", "testing");

        verify(this.requester, times(1)).invalidate("testKey", null);
    }

    @Test
    public void testOnSuccessInvalidatesOnSuccess() {
        this.uut.onSuccess(200, new WriteResult<>(true, "successMessage", "success"));

        verify(this.requester, times(1)).invalidate(eq("testKey"),
                AdditionalMatchers.aryEq(new String[] { "success" }));
    }

    @Test
    public void testOnSuccessInvalidatesOnEmpty() {
        this.uut.onSuccess(200, new WriteResult<>(true, "successMessage"));

        verify(this.requester, times(1)).invalidate(eq("testKey"), eq(null));
    }
}
