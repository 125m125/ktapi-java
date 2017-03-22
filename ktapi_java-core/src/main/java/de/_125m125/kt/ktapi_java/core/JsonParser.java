package de._125m125.kt.ktapi_java.core;

/**
 * The Interface JsonParser.
 *
 * @param <T>
 *            the generic type of Objects that are parsed from the json Strings
 */
@FunctionalInterface
public interface JsonParser<T> {

    /**
     * Parses a json String to an Object of the Type T.
     *
     * @param data
     *            the json String
     * @return the parsed result
     */
    public T parse(String data);
}
