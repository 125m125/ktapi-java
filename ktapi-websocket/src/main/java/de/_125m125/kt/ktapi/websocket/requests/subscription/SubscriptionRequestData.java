package de._125m125.kt.ktapi.websocket.requests.subscription;

import com.google.gson.annotations.Expose;

import de._125m125.kt.ktapi.core.users.User;

public class SubscriptionRequestData<T extends User<T>> {
    @Expose
    private final String  channel;
    @Expose
    private final String  uid;
    @Expose
    private final boolean selfCreated;

    private final T       user;

    public SubscriptionRequestData(final String channel, final T user, final boolean selfCreated) {
        this.channel = channel;
        this.user = user;
        this.selfCreated = selfCreated;
        if (user != null) {
            this.uid = user.getUserId();
        } else {
            this.uid = null;
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

    public boolean isSelfCreated() {
        return this.selfCreated;
    }

    public T getUser() {
        return this.user;
    }
}
