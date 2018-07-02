package de._125m125.kt.ktapi_java.websocket.exceptions;

public class SendFailedException extends RuntimeException {

    private static final long serialVersionUID = -8093153725086382778L;

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
