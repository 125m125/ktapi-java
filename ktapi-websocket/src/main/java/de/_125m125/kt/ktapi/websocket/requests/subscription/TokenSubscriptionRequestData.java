package de._125m125.kt.ktapi.websocket.requests.subscription;

import com.google.gson.annotations.Expose;

import de._125m125.kt.ktapi.core.users.AbstractTokenUser;

public class TokenSubscriptionRequestData<T extends AbstractTokenUser<T>>
        extends SubscriptionRequestData<T> {

    @Expose
    private final String tid;
    @Expose
    private final String tkn;

    public TokenSubscriptionRequestData(final String channel, final T user,
            final boolean selfCreated) {
        super(channel, user, selfCreated);
        if (user != null) {
            this.tid = user.getTokenId();
            this.tkn = user.getToken();
        } else {
            this.tid = null;
            this.tkn = null;
        }
    }

    public String getTid() {
        return this.tid;
    }

    public String getTkn() {
        return this.tkn;
    }

}
