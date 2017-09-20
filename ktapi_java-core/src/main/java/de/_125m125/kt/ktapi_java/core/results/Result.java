package de._125m125.kt.ktapi_java.core.results;

import java.util.concurrent.CountDownLatch;

public abstract class Result<T> {
    private final CountDownLatch cdl = new CountDownLatch(1);

    private T                    content;
    private String               errorMessage;
    private String               humanReadableErrorMessage;
    private int                  status;
    private boolean              successful;
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

    protected synchronized void setErrorResult(final int status, final String errorMessage,
            final String humanReadableErrorMessage) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.status = status;
        this.errorMessage = errorMessage;
        this.humanReadableErrorMessage = humanReadableErrorMessage;
        this.cdl.countDown();
    }

    protected synchronized void setFailureResult(final Throwable t) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.throwable = t;
        this.cdl.countDown();
    }

    protected synchronized void setErrorResult(final ErrorResponse errorResponse) {
        if (this.cdl.getCount() == 0) {
            throw new IllegalStateException("This result is already populated");
        }
        this.status = errorResponse.getCode();
        this.errorMessage = errorResponse.getMessage();
        this.humanReadableErrorMessage = errorResponse.getHumanReadableMessage();
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

    public void addCallback(final Callback<T> callback) {
        new CallbackResult<>(this, callback);
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
