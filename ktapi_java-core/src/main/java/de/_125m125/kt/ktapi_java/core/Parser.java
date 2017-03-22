package de._125m125.kt.ktapi_java.core;

import java.io.Reader;

/**
 * The Interface Parser.
 *
 * @param <T>
 *            the generic type of the Result returned when parsing without a
 *            helper
 * @param <U>
 *            the generic type of the Result returned when parsing with a helper
 * @param <V>
 *            the generic type of the helper
 */
public interface Parser<T, U, V> {

    /**
     * Parses the reader.
     *
     * @param content
     *            the reader to parse
     * @return the parsed result
     */
    public T parse(Reader content);

    /**
     * Parses the reader using a helper Object.
     *
     * @param content
     *            the reader to parse
     * @param helper
     *            the helper Object
     * @return the parsed result
     */
    public U parse(Reader content, V helper);

    /**
     * Gets response type expected from requests.
     *
     * @return the response type expected from requests
     */
    public String getResponseType();
}
