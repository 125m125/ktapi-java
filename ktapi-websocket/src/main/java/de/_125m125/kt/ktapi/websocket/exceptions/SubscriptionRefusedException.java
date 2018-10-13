package de._125m125.kt.ktapi.websocket.exceptions;

public class SubscriptionRefusedException extends RuntimeException {

    private static final long serialVersionUID = -6243169521236931009L;

    public SubscriptionRefusedException() {
        super();
    }

    public SubscriptionRefusedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SubscriptionRefusedException(final String message) {
        super(message);
    }

    public SubscriptionRefusedException(final Throwable cause) {
        super(cause);
    }

}
