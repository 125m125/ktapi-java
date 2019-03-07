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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Result<T> {

    private static final Logger     logger    = LoggerFactory.getLogger(Result.class);

    private final CountDownLatch    cdl       = new CountDownLatch(1);

    @SuppressFBWarnings(
            justification = "content is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
            value = "IS2_INCONSISTENT_SYNC")
    private T                       content;
    @SuppressFBWarnings(
            justification = "errorMessage is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
            value = "IS2_INCONSISTENT_SYNC")
    private String                  errorMessage;
    @SuppressFBWarnings(
            justification = "humanReadableErrorMessage is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
            value = "IS2_INCONSISTENT_SYNC")
    private String                  humanReadableErrorMessage;
    @SuppressFBWarnings(
            justification = "status is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
            value = "IS2_INCONSISTENT_SYNC")
    private int                     status;
    @SuppressFBWarnings(
            justification = "successful is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
            value = "IS2_INCONSISTENT_SYNC")
    private boolean                 successful;
    @SuppressFBWarnings(
            justification = "throwable is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
            value = "IS2_INCONSISTENT_SYNC")
    private Throwable               throwable;

    private final List<Callback<T>> callbacks = new ArrayList<>();

    protected synchronized void setSuccessResult(final int status, final T content) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.successful = true;
        this.status = status;
        this.content = content;
        complete();
    }

    protected synchronized void setFailureResult(final ErrorResponse errorResponse) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.status = errorResponse.getCode();
        this.errorMessage = errorResponse.getMessage();
        this.humanReadableErrorMessage = errorResponse.getHumanReadableMessage();
        complete();
    }

    protected synchronized void setFailureResult(final int status, final String errorMessage,
            final String humanReadableErrorMessage) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.status = status;
        this.errorMessage = errorMessage;
        this.humanReadableErrorMessage = humanReadableErrorMessage;
        complete();
    }

    protected synchronized void setErrorResult(final Throwable t) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.throwable = t;
        complete();
    }

    private void complete() {
        this.cdl.countDown();
        this.callbacks.forEach(this::invokeCallback);
    }

    public T getContent() throws InterruptedException {
        await();
        return this.content;
    }

    public String getErrorMessage() throws InterruptedException {
        await();
        return this.errorMessage;
    }

    public String getHumanReadableErrorMessage() throws InterruptedException {
        await();
        return this.humanReadableErrorMessage;
    }

    public int getStatus() throws InterruptedException {
        await();
        return this.status;
    }

    public boolean isSuccessful() throws InterruptedException {
        await();
        return this.successful;
    }

    public synchronized Result<T> addCallback(final Callback<T> callback) {
        if (this.cdl.getCount() == 0) {
            invokeCallback(callback);
        } else {
            this.callbacks.add(callback);
        }
        return this;
    }

    protected void invokeCallback(final Callback<T> callback) {
        try {
            if (this.throwable != null) {
                callback.onError(this.throwable);
            } else if (this.successful) {
                callback.onSuccess(this.status, this.content);
            } else {
                callback.onFailure(this.status, this.errorMessage, this.humanReadableErrorMessage);
            }
        } catch (final Exception e) {
            Result.logger.error("An exception occured while trying to invoke result callback", e);
        }
    }

    public void await() throws InterruptedException {
        this.cdl.await();
        if (this.throwable != null) {
            throw new ResultFetchException(this.throwable);
        }
    }

    public static class ResultFetchException extends RuntimeException {
        private static final long serialVersionUID = -6429467083525394827L;

        public ResultFetchException(final Throwable cause) {
            super(cause);
        }
    }
}
