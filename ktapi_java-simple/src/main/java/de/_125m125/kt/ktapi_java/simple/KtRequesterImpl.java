package de._125m125.kt.ktapi_java.simple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import authenticator.Authenticator;
import authenticator.BasicAuthAuthenticator;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.Parser;
import de._125m125.kt.ktapi_java.core.objects.User;

public class KtRequesterImpl implements KtRequester {
    /** The base url of the api. */
    public static final String  BASE_URL = "https://kt.125m125.de/api/";

    private final Authenticator authenticator;

    public KtRequesterImpl(final User user) {
        this.authenticator = new BasicAuthAuthenticator(user);
    }

    public KtRequesterImpl(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequester#performRequest(java.lang.String, java.lang.String, java.util.Map, boolean, de._125m125.kt.ktapi_java.simple.parsers.Parser, java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, U> U performRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<?, ?, T> parser, final T helper) {
        final StringBuilder fullUrl = new StringBuilder(KtRequesterImpl.BASE_URL).append(path);
        final StringBuilder paramString = new StringBuilder();
        if ((params != null && !params.isEmpty()) || auth) {
            final TreeMap<String, String> sortedParams;
            if (params == null) {
                sortedParams = new TreeMap<>();
            } else {
                sortedParams = new TreeMap<>(params);
            }
            if (auth) {
                this.authenticator.beforeUrlBuilding(method, path, sortedParams);
            }
            for (final Entry<String, String> entry : sortedParams.entrySet()) {
                paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (auth) {
            this.authenticator.afterUrlBuilding(paramString);
        }
        if (paramString.length() > 0) {
            fullUrl.append("?").append(paramString.deleteCharAt(paramString.length() - 1));
        }

        HttpsURLConnection connection = null;
        try {
            connection = createConnection(fullUrl.toString());
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", parser.getResponseType());
            if (auth) {
                this.authenticator.afterConnectionCreation(connection);
            }

            connection.connect();
            if (auth) {
                this.authenticator.afterConnectionConnect(connection);
            }
            try (Reader r = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"))) {
                if (helper != null) {
                    return (U) parser.parse(r, helper);
                } else {
                    return (U) parser.parse(r);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected HttpsURLConnection createConnection(final String fullUrl) throws MalformedURLException, IOException {
        HttpsURLConnection connection;
        final URL url = new URL(fullUrl.toString());
        connection = (HttpsURLConnection) url.openConnection();
        return connection;
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequester#performPlainRequest(java.lang.String, java.lang.String, java.util.Map, boolean, de._125m125.kt.ktapi_java.simple.parsers.Parser)
     */
    @Override
    public <T> T performPlainRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<T, ?, ?> parser) {
        return performRequest(method, path, params, auth, parser, null);
    }
}