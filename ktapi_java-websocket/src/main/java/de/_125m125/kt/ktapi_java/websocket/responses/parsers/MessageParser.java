package de._125m125.kt.ktapi_java.websocket.responses.parsers;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi_java.websocket.responses.SessionResponse;
import de._125m125.kt.ktapi_java.websocket.responses.UpdateNotification;

public class MessageParser {
    public Optional<Object> parse(final String message) {
        final JsonElement parse = new JsonParser().parse(message);
        if (parse instanceof JsonObject) {
            final JsonObject jsonObject = (JsonObject) parse;
            if (jsonObject.has("rid")) {
                if (jsonObject.has("session")) {
                    return Optional.of(new Gson().fromJson(jsonObject, SessionResponse.class));
                }
                return Optional.of(new Gson().fromJson(jsonObject, ResponseMessage.class));
            }
            if (jsonObject.has("type")) {
                if ("update".equals(jsonObject.get("type").getAsString())) {
                    return Optional.of(new Gson().fromJson(jsonObject, UpdateNotification.class));
                }
            }
        }
        return Optional.empty();
    }
}
