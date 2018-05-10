package de._125m125.kt.ktapi_java.websocket.events;

import java.io.IOException;

import de._125m125.kt.ktapi_java.websocket.requests.RequestMessage;

public class MessageDeliveryFailedEvent extends CancelableWebsocketEvent {

    private final IOException    reason;
    private final RequestMessage requestMessage;

    public MessageDeliveryFailedEvent(final WebsocketStatus websocketStatus, final RequestMessage requestMessage,
            final IOException reason) {
        super(websocketStatus);
        this.requestMessage = requestMessage;
        this.reason = reason;
    }

    public IOException getReason() {
        return this.reason;
    }

    public RequestMessage getRequestMessage() {
        return requestMessage;
    }

}
