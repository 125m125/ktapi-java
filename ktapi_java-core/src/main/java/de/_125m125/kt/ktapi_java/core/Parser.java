package de._125m125.kt.ktapi_java.core;

import java.io.Reader;

public interface Parser<T, U, V> {
    public T parse(Reader content);

    public U parse(Reader content, V helper);

    public String getResponseType();
}
