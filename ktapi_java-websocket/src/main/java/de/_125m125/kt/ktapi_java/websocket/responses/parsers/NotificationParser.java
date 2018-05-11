package de._125m125.kt.ktapi_java.websocket.responses.parsers;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de._125m125.kt.ktapi_java.websocket.responses.UpdateNotification;

public class NotificationParser implements WebsocketMessageParser<UpdateNotification> {

    @Override
    public boolean parses(final String raw, final Optional<JsonObject> json) {
        return json.filter(j -> j.has("type") && "update".equals(j.get("type").getAsString())).isPresent();
    }

    @Override
    public UpdateNotification parse(final String raw, final Optional<JsonObject> json) {
        return new Gson().fromJson(json.get(), UpdateNotification.class);
    }

}
