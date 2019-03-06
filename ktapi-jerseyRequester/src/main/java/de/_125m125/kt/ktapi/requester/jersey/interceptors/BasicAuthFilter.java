package de._125m125.kt.ktapi.requester.jersey.interceptors;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.UserKey;

public class BasicAuthFilter extends SelfRegistrator implements ClientRequestFilter {

    private final KtUserStore userStore;

    public BasicAuthFilter(final KtUserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public void filter(final ClientRequestContext context) throws IOException {
        Object property = context.getProperty("user");
        if (property == null) {
            property = context.getConfiguration().getProperty("user");
        }
        if (property != null && property instanceof UserKey) {
            final TokenUser user = this.userStore.get((UserKey) property, TokenUser.class);
            if (user != null) {
                context.getHeaders().add("Authorization",
                        "Basic " + Base64.getEncoder()
                                .encodeToString((user.getTokenId() + ":" + user.getToken())
                                        .getBytes(Charset.forName("UTF-8"))));
            }
        }
    }

}
