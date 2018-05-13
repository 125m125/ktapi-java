package de._125m125.kt.ktapi_java.websocket.events;

import java.util.Optional;

import com.google.gson.JsonObject;

public class UnparsableMessageEvent extends WebsocketEvent {

    private final String               rawMessage;
    private final Optional<JsonObject> json;

    public UnparsableMessageEvent(final WebsocketStatus websocketStatus, final String rawMessage,
            final Optional<JsonObject> json) {
        super(websocketStatus);
        this.rawMessage = rawMessage;
        this.json = json;
    }

    public String getRawMessage() {
        return this.rawMessage;
    }

    public Optional<JsonObject> getJson() {
        return this.json;
    }

}
