package de._125m125.kt.ktapi_java.websocket.events;

import de._125m125.kt.ktapi_java.websocket.requests.RequestMessage;

public class BeforeMessageSendEvent extends CancelableWebsocketEvent {

    private final RequestMessage message;

    public BeforeMessageSendEvent(final WebsocketStatus websocketStatus, final RequestMessage message) {
        super(websocketStatus);
        this.message = message;
    }

    public RequestMessage getMessage() {
        return message;
    }

}
