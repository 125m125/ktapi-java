package authenticator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.simple.KtRequesterImpl;

public class URLParamAuthenticator extends Authenticator {
    /** The algorithm used for signatures. */
    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";

    /** The user. */
    private final User          user;
    /** The time offset. */
    private long                timeOffset;
    /** The last used timestamp. */
    private long                lastTime;
    /** The maximum offset of the timestamp from the current time. */
    private static final long   MAX_OFFSET          = 4 * 60 * 1000;

    public URLParamAuthenticator(final User user) {
        this.user = user;
    }

    @Override
    public void beforeUrlBuilding(final String method, final String path, final Map<String, String> params) {
        params.put("uid", this.user.getUID());
        params.put("tid", this.user.getTID());
        params.put("timestamp", String.valueOf(getCurrentTimestamp()));
    }

    @Override
    public void afterUrlBuilding(final StringBuilder url) {
        final String signature = getSignatureFor(url.deleteCharAt(url.length() - 1).toString());
        url.append("&signature=").append(signature).append("&");
    }

    /**
     * Synchronizes the time used for requests with the server by using
     * Cristian's algorithm.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void syncTime() throws IOException {
        final long start = System.currentTimeMillis();
        final String time;
        try (BufferedReader is = new BufferedReader(new InputStreamReader(
                new URL(KtRequesterImpl.BASE_URL + "/api/ping").openStream(), Charset.forName("UTF-8")))) {
            time = is.readLine();
        }
        final long end = System.currentTimeMillis();
        final long serverTime = Long.parseLong(time.trim());

        this.timeOffset = serverTime - (start + end) / 2;
        this.lastTime = System.currentTimeMillis() + URLParamAuthenticator.MAX_OFFSET;
    }

    /**
     * Gets the timestamp to use for a new request.
     *
     * @return the current timestamp
     */
    private synchronized long getCurrentTimestamp() {
        if (this.lastTime != 0) {
            if (this.lastTime < System.currentTimeMillis() - URLParamAuthenticator.MAX_OFFSET) {
                this.lastTime = System.currentTimeMillis() + URLParamAuthenticator.MAX_OFFSET;
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
                URLParamAuthenticator.SIGNATURE_ALGORITHM);
        String encoded = null;
        try {
            final Mac mac = Mac.getInstance(URLParamAuthenticator.SIGNATURE_ALGORITHM);
            mac.init(signingKey);
            final byte[] rawHmac = mac.doFinal(data.getBytes(Charset.forName("UTF-8")));
            encoded = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encoded;
    }
}
