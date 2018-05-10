package de._125m125.kt.ktapi_java.websocket.events;

public class MessageReceivedEvent extends WebsocketEvent {

    private final Object message;

    public MessageReceivedEvent(final WebsocketStatus websocketStatus, final Object parsedResponse) {
        super(websocketStatus);
        this.message = parsedResponse;
    }

    public Object getMessage() {
        return this.message;
    }

}
