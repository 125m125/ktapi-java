package de._125m125.kt.ktapi_java.retrofitRequester.builderModifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import de._125m125.kt.ktapi_java.core.KtUserStore;
import de._125m125.kt.ktapi_java.core.entities.User;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import okhttp3.Request;

public class BasicAuthenticator extends HeaderAdder {

    private static String generateAuthString(final KtUserStore<User> userStore, final Request r) throws IOException {
        final String header = r.header("userKey");
        if (header == null || header.isEmpty()) {
            return null;
        }
        final User user = userStore.get(UserKey.fromString(header));
        if (user == null) {
            throw new IOException("user is not contained in the KtUserStore");
        }
        return "Basic " + Base64.getEncoder()
                .encodeToString((user.getTid() + ":" + user.getTkn()).getBytes(Charset.forName("UTF-8")));
    }

    public BasicAuthenticator(final KtUserStore<User> userStore) {
        super("Authorization", r -> generateAuthString(userStore, r));
    }

}
