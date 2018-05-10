package de._125m125.kt.ktapi_java.websocket.events;

public class WebsocketEvent {
    private final WebsocketStatus websocketStatus;

    public WebsocketEvent(final WebsocketStatus websocketStatus) {
        super();
        this.websocketStatus = websocketStatus;
    }

    public WebsocketStatus getWebsocketStatus() {
        return this.websocketStatus;
    }

}
