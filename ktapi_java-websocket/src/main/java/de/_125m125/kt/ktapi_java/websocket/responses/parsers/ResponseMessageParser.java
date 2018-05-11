package de._125m125.kt.ktapi_java.websocket.responses.parsers;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;

public class ResponseMessageParser implements WebsocketMessageParser<ResponseMessage> {

    @Override
    public boolean parses(final String raw, final Optional<JsonObject> json) {
        return json.filter(j -> j.has("rid")).isPresent();
    }

    @Override
    public ResponseMessage parse(final String raw, final Optional<JsonObject> json) {
        return new Gson().fromJson(json.get(), ResponseMessage.class);
    }

}
