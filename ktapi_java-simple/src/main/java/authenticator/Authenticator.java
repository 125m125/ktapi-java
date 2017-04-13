package authenticator;

import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public abstract class Authenticator {
    public void beforeUrlBuilding(final String method, final String path, final Map<String, String> params) {
    }

    public void afterUrlBuilding(final StringBuilder url) {
    }

    public HttpsURLConnection afterConnectionCreation(final HttpsURLConnection c) {
        return c;
    }

    public HttpsURLConnection afterConnectionConnect(final HttpsURLConnection c) {
        return c;
    }

}
