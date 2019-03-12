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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class ResultTest {

    private Result<String> uut;

    @Before
    public void beforeResultTest() {
        this.uut = new Result<>();
    }

    @Test
    public void testInvokesSuccessCallbacksRegisteredBeforeCompletion() throws Exception {
        @SuppressWarnings("unchecked")
        final Callback<String> callback = mock(Callback.class);

        this.uut.addCallback(callback);
        this.uut.setSuccessResult(200, "test");

        verify(callback).onSuccess(200, "test");
    }

    @Test
    public void testInvokesSuccessCallbacksRegisteredAfterCompletion() throws Exception {
        @SuppressWarnings("unchecked")
        final Callback<String> callback = mock(Callback.class);

        this.uut.setSuccessResult(200, "test2");
        this.uut.addCallback(callback);

        verify(callback).onSuccess(200, "test2");
    }

    @Test
    public void testInvokesFailureCallbacksRegisteredBeforeCompletion() throws Exception {
        @SuppressWarnings("unchecked")
        final Callback<String> callback = mock(Callback.class);

        this.uut.addCallback(callback);
        this.uut.setFailureResult(400, "test", "humanReadableTest");

        verify(callback).onFailure(400, "test", "humanReadableTest");
    }

    @Test
    public void testInvokesFailureCallbacksRegisteredAfterCompletion() throws Exception {
        @SuppressWarnings("unchecked")
        final Callback<String> callback = mock(Callback.class);

        this.uut.setFailureResult(402, "test2", "anotherHumanReadableTest");
        this.uut.addCallback(callback);
        verify(callback).onFailure(402, "test2", "anotherHumanReadableTest");
    }

    @Test
    public void testInvokesErrorCallbacksRegisteredBeforeCompletion() throws Exception {
        @SuppressWarnings("unchecked")
        final Callback<String> callback = mock(Callback.class);

        final Throwable t = new Throwable("some exception");
        this.uut.addCallback(callback);
        this.uut.setErrorResult(t);

        verify(callback).onError(t);
    }

    @Test
    public void testInvokesErrorCallbacksRegisteredAfterCompletion() throws Exception {
        @SuppressWarnings("unchecked")
        final Callback<String> callback = mock(Callback.class);

        final Throwable t = new Throwable("another exception");
        this.uut.setErrorResult(t);
        this.uut.addCallback(callback);

        verify(callback).onError(t);
    }

    @Test
    public void testContinuesPublishingToCallbackAfterException() throws Exception {
        final RuntimeException e1 = new RuntimeException("callback1");
        final RuntimeException e2 = new RuntimeException("callback2");

        @SuppressWarnings("unchecked")
        final Callback<String> callback1 = mock(Callback.class);
        doThrow(e1).when(callback1).onFailure(anyInt(), any(), any());
        @SuppressWarnings("unchecked")
        final Callback<String> callback2 = mock(Callback.class);
        doThrow(e2).when(callback1).onFailure(anyInt(), any(), any());

        this.uut.addCallback(callback1);
        this.uut.addCallback(callback2);
        this.uut.setFailureResult(402, "test2", "anotherHumanReadableTest");

        verify(callback1).onFailure(anyInt(), any(), any());
        verify(callback2).onFailure(anyInt(), any(), any());
    }

    @Test
    public void testExceptionsInCallbacksAddedAfterCompletionDontPropagateBackToCompleter()
            throws Exception {
        final RuntimeException e1 = new RuntimeException("callback1");
        final RuntimeException e2 = new RuntimeException("callback2");

        @SuppressWarnings("unchecked")
        final Callback<String> callback1 = mock(Callback.class);
        doThrow(e1).when(callback1).onSuccess(anyInt(), any());
        @SuppressWarnings("unchecked")
        final Callback<String> callback2 = mock(Callback.class);
        doThrow(e2).when(callback1).onSuccess(anyInt(), any());

        this.uut.setSuccessResult(200, "test2");
        this.uut.addCallback(callback1);
        this.uut.addCallback(callback2);

        verify(callback1).onSuccess(anyInt(), any());
        verify(callback2).onSuccess(anyInt(), any());
    }

}
