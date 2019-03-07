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
package de._125m125.kt.ktapi.websocket.requests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;

public class WebsocketResult {
    private final CompletableFuture<ResponseMessage> result = new CompletableFuture<>();

    public WebsocketResult() {

    }

    public ResponseMessage get() throws InterruptedException {
        try {
            return this.result.get();
        } catch (final ExecutionException e) {
            return new ResponseMessage("retrieval of result failed", e);
        }
    }

    public ResponseMessage get(final long maxWait, final TimeUnit unit)
            throws InterruptedException, TimeoutException {
        try {
            return this.result.get(maxWait, unit);
        } catch (final ExecutionException e) {
            return new ResponseMessage("retrieval of result failed", e);
        }
    }

    public synchronized void addCallback(final Consumer<ResponseMessage> consumer) {
        this.result.thenAccept(consumer::accept);
    }

    public synchronized void setResponse(final ResponseMessage responseMessage) {
        this.result.complete(responseMessage);
    }

    public synchronized boolean isDone() {
        return this.result.isDone();
    }
}
