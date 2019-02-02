package de._125m125.kt.ktapi.core.results;

import java.util.concurrent.CountDownLatch;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class Result<T> {
    private final CountDownLatch cdl = new CountDownLatch(1);

    @SuppressFBWarnings(
            justification = "content is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
                    value = "IS2_INCONSISTENT_SYNC")
    private T                    content;
    @SuppressFBWarnings(
            justification = "errorMessage is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
                    value = "IS2_INCONSISTENT_SYNC")
    private String               errorMessage;
    @SuppressFBWarnings(
            justification = "humanReadableErrorMessage is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
                    value = "IS2_INCONSISTENT_SYNC")
    private String               humanReadableErrorMessage;
    @SuppressFBWarnings(
            justification = "status is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
                    value = "IS2_INCONSISTENT_SYNC")
    private int                  status;
    @SuppressFBWarnings(
            justification = "successful is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
                    value = "IS2_INCONSISTENT_SYNC")
    private boolean              successful;
    @SuppressFBWarnings(
            justification = "throwable is set once in synchronized method. "
                    + "Read access is protected by CountDownlatch happens-before",
                    value = "IS2_INCONSISTENT_SYNC")
    private Throwable            throwable;

    protected synchronized void setSuccessResult(final int status, final T content) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.successful = true;
        this.status = status;
        this.content = content;
        this.cdl.countDown();
    }

    protected synchronized void setFailureResult(final ErrorResponse errorResponse) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.status = errorResponse.getCode();
        this.errorMessage = errorResponse.getMessage();
        this.humanReadableErrorMessage = errorResponse.getHumanReadableMessage();
        this.cdl.countDown();
    }

    protected synchronized void setFailureResult(final int status, final String errorMessage,
            final String humanReadableErrorMessage) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.status = status;
        this.errorMessage = errorMessage;
        this.humanReadableErrorMessage = humanReadableErrorMessage;
        this.cdl.countDown();
    }

    protected synchronized void setErrorResult(final Throwable t) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.throwable = t;
        this.cdl.countDown();
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

    public Result<T> addCallback(final Callback<T> callback) {
        new CallbackResult<>(this, callback);
        return this;
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
