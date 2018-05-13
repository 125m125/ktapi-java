package de._125m125.kt.ktapi_java.websocket.exceptions;

public class MessageCancelException extends MessageSendException {

    private static final long serialVersionUID = 3875862675513053738L;

    public MessageCancelException() {
        super();
    }

    public MessageCancelException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageCancelException(final String message) {
        super(message);
    }

    public MessageCancelException(final Throwable cause) {
        super(cause);
    }
}
