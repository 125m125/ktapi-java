package de._125m125.kt.ktapi_java.simple.parsers;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class JsonParser implements Parser<JsonObject, Object, TypeToken<?>> {

    private static final Gson                       GSON   = new Gson();
    private static final com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

    @Override
    public JsonObject parse(final Reader content) {
        return JsonParser.parser.parse(content).getAsJsonObject();
    }

    @Override
    public Object parse(final Reader content, final TypeToken<?> v) {
        return JsonParser.GSON.fromJson(content, v.getType());
    }

    @Override
    public String getResponseType() {
        return "application/json";
    }
}
