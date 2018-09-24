package de._125m125.kt.ktapi.websocket.exceptions;

public class MessageSendException extends RuntimeException {
    private static final long serialVersionUID = 8156302203651877630L;

    public MessageSendException() {
        super();
    }

    public MessageSendException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageSendException(final String message) {
        super(message);
    }

    public MessageSendException(final Throwable cause) {
        super(cause);
    }

}
