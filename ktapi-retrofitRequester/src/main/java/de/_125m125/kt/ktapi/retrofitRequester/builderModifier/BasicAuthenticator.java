package de._125m125.kt.ktapi.retrofitRequester.builderModifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import okhttp3.Request;

public class BasicAuthenticator extends HeaderAdder {

    private static String generateAuthString(final KtUserStore userStore, final Request r) throws IOException {
        final String header = r.header("userKey");
        if (header == null || header.isEmpty()) {
            return null;
        }
        final TokenUser user = userStore.get(header, TokenUserKey.class);
        if (user == null) {
            throw new IOException("user is not contained in the KtUserStore");
        }
        return "Basic " + Base64.getEncoder()
                .encodeToString((user.getTokenId() + ":" + user.getToken()).getBytes(Charset.forName("UTF-8")));
    }

    public BasicAuthenticator(final KtUserStore userStore) {
        super("Authorization", r -> generateAuthString(userStore, r));
    }

}
