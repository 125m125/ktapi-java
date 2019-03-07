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
package de._125m125.kt.ktapi.core.results;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import de._125m125.kt.ktapi.core.results.Callback.TriConsumer;

public class CallbackTest {

    @Test
    public void testCurriedFailureIsCorrectlyUncurried() {
        final Object[] data = { 0, -1, null, null };
        final Callback<Object> failureCallback = Callback.failureCallback(s -> m -> h -> {
            data[0] = (int) data[0] + 1;
            data[1] = s;
            data[2] = m;
            data[3] = h;
        });
        failureCallback.onFailure(400, "failure", "human readable failure");

        assertEquals(1, data[0]);
        assertEquals(400, data[1]);
        assertEquals("failure", data[2]);
        assertEquals("human readable failure", data[3]);
    }

    @Test
    public void testNonCurriedFailureIsCorrectlyPassedOn() {
        final Object[] data = { 0, -1, null, null };
        final Callback<Object> failureCallback = Callback.failureCallback((s, m, h) -> {
            data[0] = (int) data[0] + 1;
            data[1] = s;
            data[2] = m;
            data[3] = h;
        });
        failureCallback.onFailure(400, "failure", "human readable failure");

        assertEquals(1, data[0]);
        assertEquals(400, data[1]);
        assertEquals("failure", data[2]);
        assertEquals("human readable failure", data[3]);
    }

    @Test
    public void testCurriedSuccessIsCorrectlyUncurried() {
        final Object[] data = { 0, -1, null };
        final Callback<Object> successCallback = Callback.successCallback(s -> o -> {
            data[0] = (int) data[0] + 1;
            data[1] = s;
            data[2] = o;
        });
        successCallback.onSuccess(200, "success");

        assertEquals(1, data[0]);
        assertEquals(200, data[1]);
        assertEquals("success", data[2]);
    }

    @Test
    public void testNonCurriedSuccessIsCorrectlyPassedOn() {
        final Object[] data = { 0, -1, null };
        final Callback<Object> successCallback = Callback.successCallback((s, o) -> {
            data[0] = (int) data[0] + 1;
            data[1] = s;
            data[2] = o;
        });
        successCallback.onSuccess(200, "success");

        assertEquals(1, data[0]);
        assertEquals(200, data[1]);
        assertEquals("success", data[2]);
    }

    @Test
    public void testErrorIsCorrectlyPassedOn() {
        final Object[] data = { 0, null };
        final Callback<Object> successCallback = Callback.errorCallback(t -> {
            data[0] = (int) data[0] + 1;
            data[1] = t;
        });
        final IllegalArgumentException t = new IllegalArgumentException();
        successCallback.onError(t);

        assertEquals(1, data[0]);
        assertSame(t, data[1]);
    }

    @Test(expected = NullPointerException.class)
    public void testErrorCallbackThrowsOnNullParameter() {
        Callback.errorCallback(null);
    }

    @Test
    public void testCallbackGeneratorDoesNotThrowWhenEmptyMethodIsCalled() {
        final Object[] data = { 0, -1, null };
        final Callback<Object> failureCallback = Callback.failureCallback((s, m, h) -> {
            data[0] = (int) data[0] + 1;
            data[1] = s;
            data[2] = m;
            data[3] = h;
        });
        failureCallback.onError(new IllegalArgumentException());

        assertEquals(0, data[0]);
        assertEquals(-1, data[1]);
        assertEquals(null, data[2]);
    }

    @Test
    public void testAfterTriConsumer() {
        final int[] callCounts = { 0, 0 };
        final TriConsumer<Object, Object, Object> consumer = (a, b, c) -> callCounts[0]++;
        final TriConsumer<Object, Object, Object> andThen = consumer
                .andThen((a, b, c) -> callCounts[1]++);

        andThen.accept("a", 1, 'l');

        assertArrayEquals(new int[] { 1, 1 }, callCounts);
    }

    @Test(expected = NullPointerException.class)
    public void testAfterTriConsumerThrowsWhenAfterIsNull() {
        final int[] callCounts = { 0, 0 };
        final TriConsumer<Object, Object, Object> consumer = (a, b, c) -> callCounts[0]++;
        consumer.andThen(null);
    }
}
