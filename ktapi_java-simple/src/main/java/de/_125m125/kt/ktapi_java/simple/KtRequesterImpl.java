package de._125m125.kt.ktapi_java.simple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.Parser;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.simple.parsers.StringParser;

public class KtRequesterImpl implements KtRequester {
    /** The base url of the api. */
    public static final String  BASE_URL            = "https://kt.125m125.de/api/";
    /** The algorithm used for signatures. */
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";
    /** The maximum offset of the timestamp from the current time. */
    private static final long   MAX_OFFSET          = 4 * 60 * 1000;

    /** The user. */
    private final User          user;
    /** The time offset. */
    private long                timeOffset;
    /** The last used timestamp. */
    private long                lastTime;

    public KtRequesterImpl(final User user) {
        this.user = user;
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
                sortedParams.put("uid", this.user.getUID());
                sortedParams.put("tid", this.user.getTID());
                sortedParams.put("timestamp", String.valueOf(getCurrentTimestamp()));
            }
            for (final Entry<String, String> entry : sortedParams.entrySet()) {
                paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (auth) {
            final String signature = getSignatureFor(paramString.deleteCharAt(paramString.length() - 1).toString());
            paramString.append("&signature=").append(signature).append("&");
        }
        if (paramString.length() > 0) {
            fullUrl.append("?").append(paramString.deleteCharAt(paramString.length() - 1));
        }

        HttpsURLConnection connection = null;
        try {
            final URL url = new URL(fullUrl.toString());
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Accept", parser.getResponseType());

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

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.simple.KtRequester#performPlainRequest(java.lang.String, java.lang.String, java.util.Map, boolean, de._125m125.kt.ktapi_java.simple.parsers.Parser)
     */
    @Override
    public <T> T performPlainRequest(final String method, final String path, final Map<String, String> params,
            final boolean auth, final Parser<T, ?, ?> parser) {
        return performRequest(method, path, params, auth, parser, null);
    }

    /**
     * Synchronizes the time used for requests with the server by using
     * Cristian's algorithm.
     */
    public void syncTime() {
        final long start = System.currentTimeMillis();
        final String time = performPlainRequest("GET", "ping", null, false, new StringParser());
        final long end = System.currentTimeMillis();
        final long serverTime = Long.parseLong(time.trim());

        this.timeOffset = serverTime - (start + end) / 2;
        this.lastTime = System.currentTimeMillis() + KtRequesterImpl.MAX_OFFSET;
    }

    /**
     * Gets the timestamp to use for a new request.
     *
     * @return the current timestamp
     */
    private synchronized long getCurrentTimestamp() {
        if (this.lastTime != 0) {
            if (this.lastTime < System.currentTimeMillis() - KtRequesterImpl.MAX_OFFSET) {
                this.lastTime = System.currentTimeMillis() + KtRequesterImpl.MAX_OFFSET;
            }
            return this.lastTime + this.timeOffset;
        }
        return System.currentTimeMillis();
    }

    /**
     * Gets the signature for a String.
     *
     * @param data
     *            the data for which to create a signature
     * @return the signature
     */
    public String getSignatureFor(final String data) {
        final SecretKeySpec signingKey = new SecretKeySpec(this.user.getTKN().getBytes(),
                KtRequesterImpl.SIGNATURE_ALGORITHM);
        String encoded = null;
        try {
            final Mac mac = Mac.getInstance(KtRequesterImpl.SIGNATURE_ALGORITHM);
            mac.init(signingKey);
            final byte[] rawHmac = mac.doFinal(data.getBytes(Charset.forName("UTF-8")));
            encoded = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encoded;
    }
}