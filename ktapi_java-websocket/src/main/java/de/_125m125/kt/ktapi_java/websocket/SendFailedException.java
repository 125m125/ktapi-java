package de._125m125.kt.ktapi_java.websocket;

public class SendFailedException extends RuntimeException {

    public SendFailedException() {
        super();
    }

    public SendFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SendFailedException(final String message) {
        super(message);
    }

    public SendFailedException(final Throwable cause) {
        super(cause);
    }

}
