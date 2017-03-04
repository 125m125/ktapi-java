package de._125m125.kt.ktapi_java.core;

import java.util.Map;

public interface KtRequester {

    /**
     * Perform a request to the kadcontrade api.
     *
     * @param kt
     *            TODO
     * @param method
     *            the method of the request
     * @param path
     *            the path of the request
     * @param params
     *            the parameters for the request
     * @param auth
     *            true, if authentication is required for the request
     * @param parser
     *            the parser
     * @param helper
     *            the helper for the parser
     * @param <T>
     *            the generic type of the helper
     * @param <U>
     *            the generic type of the result
     * @return the parsed result
     * @throws ClassCastException
     *             if the result of the parser is not of the Type &lt;U&gt;
     */
    <T, U> U performRequest(String method, String path, Map<String, String> params, boolean auth,
            Parser<?, ?, T> parser, T helper);

    /**
     * Perform a plain request.
     *
     * @param <T>
     *            the generic type of the result
     * @param method
     *            the request method (GET, POST)
     * @param path
     *            the path of the request
     * @param params
     *            the parameters to send with the request
     * @param auth
     *            true if authentication is required
     * @param parser
     *            the parser for the result
     * @return the t
     */
    <T> T performPlainRequest(String method, String path, Map<String, String> params, boolean auth,
            Parser<T, ?, ?> parser);

}