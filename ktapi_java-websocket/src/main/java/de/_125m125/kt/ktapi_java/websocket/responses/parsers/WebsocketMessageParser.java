package de._125m125.kt.ktapi_java.websocket.responses.parsers;

import java.util.Optional;

import com.google.gson.JsonObject;

public interface WebsocketMessageParser<T> {
    public boolean parses(String raw, Optional<JsonObject> json);

    public T parse(final String raw, final Optional<JsonObject> json);
}
