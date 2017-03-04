package de._125m125.kt.ktapi_java.core;

@FunctionalInterface
public interface JsonParser<T> {
    public T parse(String data);
}
