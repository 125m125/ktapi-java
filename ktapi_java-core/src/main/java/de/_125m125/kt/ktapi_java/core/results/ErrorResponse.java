package de._125m125.kt.ktapi_java.core.results;

public class ErrorResponse {
    private final int    code;
    private final String message;
    private final String humanReadableMessage;

    public ErrorResponse(final int code, final String message, final String humanReadableMessage) {
        super();
        this.code = code;
        this.message = message;
        this.humanReadableMessage = humanReadableMessage;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getHumanReadableMessage() {
        return this.humanReadableMessage;
    }
}
