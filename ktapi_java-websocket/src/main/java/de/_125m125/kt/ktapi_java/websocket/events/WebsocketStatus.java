package de._125m125.kt.ktapi_java.websocket.events;

public class WebsocketStatus {
    private final boolean active;
    private final boolean connected;

    public WebsocketStatus(final boolean active, final boolean connected) {
        super();
        this.active = active;
        this.connected = connected;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isConnected() {
        return this.connected;
    }

}
