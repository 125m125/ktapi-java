package authenticator;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de._125m125.kt.ktapi_java.core.objects.User;

public class BasicAuthAuthenticator extends Authenticator {

    /** The user. */
    private final User user;

    public BasicAuthAuthenticator(final User user) {
        this.user = user;
    }

    @Override
    public void beforeUrlBuilding(final String method, final String path, final Map<String, String> params) {
        params.put("uid", this.user.getUID());
    }

    @Override
    public HttpsURLConnection afterConnectionCreation(final HttpsURLConnection c) {
        c.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
                .encodeToString((this.user.getTID() + ":" + this.user.getTKN()).getBytes(Charset.forName("UTF-8"))));
        return c;
    }

}
