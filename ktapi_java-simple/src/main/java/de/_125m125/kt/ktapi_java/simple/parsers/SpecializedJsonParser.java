package de._125m125.kt.ktapi_java.simple.parsers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de._125m125.kt.ktapi_java.core.JsonParser;

public class SpecializedJsonParser<T> implements JsonParser<T> {

    @Override
    public T parse(final String data) {
        return new Gson().fromJson(data, new TypeToken<T>() {
        }.getType());
    }
}
