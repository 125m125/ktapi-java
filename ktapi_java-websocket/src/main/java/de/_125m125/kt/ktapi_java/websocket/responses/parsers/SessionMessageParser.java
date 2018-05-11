package de._125m125.kt.ktapi_java.websocket.responses.parsers;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de._125m125.kt.ktapi_java.websocket.responses.SessionResponse;

public class SessionMessageParser implements WebsocketMessageParser<SessionResponse> {

    @Override
    public boolean parses(final String raw, final Optional<JsonObject> json) {
        return json.filter(j -> j.has("session")).isPresent();
    }

    @Override
    public SessionResponse parse(final String raw, final Optional<JsonObject> json) {
        return new Gson().fromJson(json.get(), SessionResponse.class);
    }

}
