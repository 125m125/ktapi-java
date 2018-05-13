package de._125m125.kt.ktapi_java.websocket.exceptions;

public class MessageDeliveryException extends MessageSendException {

    private static final long serialVersionUID = 1881080429628417569L;

    public MessageDeliveryException() {
        super();
    }

    public MessageDeliveryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageDeliveryException(final String message) {
        super(message);
    }

    public MessageDeliveryException(final Throwable cause) {
        super(cause);
    }

}
