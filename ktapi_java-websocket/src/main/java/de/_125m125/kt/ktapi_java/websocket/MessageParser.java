package de._125m125.kt.ktapi_java.websocket;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageParser {
    public Optional<Object> parse(final String message) {
        final JsonElement parse = new JsonParser().parse(message);
        if (parse instanceof JsonObject) {
            final JsonObject jsonObject = (JsonObject) parse;
            if (jsonObject.has("rid")) {
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
