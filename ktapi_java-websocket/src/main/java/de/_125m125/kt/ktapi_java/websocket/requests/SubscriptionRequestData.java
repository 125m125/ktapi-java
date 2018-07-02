package de._125m125.kt.ktapi_java.websocket.requests;

import de._125m125.kt.ktapi_java.core.entities.User;

public class SubscriptionRequestData {
    private final String  channel;
    private final String  uid;
    private final String  tid;
    private final String  tkn;
    private final boolean selfCreated;

    public SubscriptionRequestData(final String channel, final User user, final boolean selfCreated) {
        this.channel = channel;
        this.selfCreated = selfCreated;
        if (user != null) {
            this.uid = user.getUid();
            this.tid = user.getTid();
            this.tkn = user.getTkn();
        } else {
            this.uid = null;
            this.tid = null;
            this.tkn = null;
        }
    }

    public SubscriptionRequestData(final String channel) {
        this(channel, null, false);
    }

    public String getChannel() {
        return this.channel;
    }

    public String getUid() {
        return this.uid;
    }

    public String getTid() {
        return this.tid;
    }

    public String getTkn() {
        return this.tkn;
    }

    public boolean isSelfCreated() {
        return this.selfCreated;
    }

}
