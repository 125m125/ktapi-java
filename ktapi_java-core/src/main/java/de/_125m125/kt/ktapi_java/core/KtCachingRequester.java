package de._125m125.kt.ktapi_java.core;

import java.util.Map;

public interface KtCachingRequester extends KtRequester {

    /**
     * Perform a request to the kadcontrade api ignoring the cache of the
     * CachingPusherKt.
     *
     * @param <T>
     *            the generic type of the helper
     * @param <U>
     *            the generic type of the result
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
     * @return the parsed result
     * @throws ClassCastException
     *             if the result of the parser is not of the Type &lt;U&gt;
     */
    <T, U> U performUncachedRequest(String method, String path, Map<String, String> params, boolean auth,
            Parser<?, ?, T> parser, T helper);

    /**
     * Checks if a given object might have updated on the server.
     *
     * @param toCheck
     *            the object to check
     * @return true, if the given object might have updated on the server
     */
    boolean hasUpdated(Object toCheck);

}