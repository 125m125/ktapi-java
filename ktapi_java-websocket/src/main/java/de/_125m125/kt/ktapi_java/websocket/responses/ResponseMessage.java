package de._125m125.kt.ktapi_java.websocket.responses;

import java.util.Optional;

public class ResponseMessage {
    private final Integer   rid;
    private final Long      pong;
    private final String    error;
    private final Throwable errorCause;

    public ResponseMessage(final Integer rid, final Long pong, final String error, final Throwable errorCause) {
        super();
        this.rid = rid;
        this.pong = pong;
        this.error = error;
        this.errorCause = errorCause;
    }

    public ResponseMessage(final String error, final Throwable errorCause) {
        this(null, null, error, errorCause);
    }

    public Optional<Integer> getRequestId() {
        return Optional.ofNullable(this.rid);
    }

    public Optional<Long> getServerTime() {
        return Optional.ofNullable(this.pong);
    }

    public Optional<String> getError() {
        return this.error == null || "false".equals(this.error) ? Optional.empty() : Optional.of(this.error);
    }

    public Optional<Throwable> getErrorCause() {
        return Optional.ofNullable(this.errorCause);
    }

    public boolean success() {
        return !getError().isPresent();
    }
}
