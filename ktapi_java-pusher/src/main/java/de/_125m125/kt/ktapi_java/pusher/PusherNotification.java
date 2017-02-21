package de._125m125.kt.ktapi_java.pusher;

import java.util.Map;

public class PusherNotification {
    private final boolean             selfCreated;
    private final long                uid;
    private final String              base32Uid;
    private final String              type;
    private final Map<String, String> details;

    public PusherNotification(final boolean selfCreated, final long uid, final String base32Uid, final String type,
            final Map<String, String> details) {
        super();
        this.selfCreated = selfCreated;
        this.uid = uid;
        this.base32Uid = base32Uid;
        this.type = type;
        this.details = details;
    }

    public boolean isSelfCreated() {
        return this.selfCreated;
    }

    public long getUid() {
        return this.uid;
    }

    public String getBase32Uid() {
        return this.base32Uid;
    }

    public String getType() {
        return this.type;
    }

    public Map<String, String> getDetails() {
        return this.details;
    }

    @Override
    public String toString() {
        return "PusherNotification [selfCreated=" + this.selfCreated + ", uid=" + this.uid + ", base32Uid="
                + this.base32Uid + ", type=" + this.type + ", details=" + this.details + "]";
    }

}
