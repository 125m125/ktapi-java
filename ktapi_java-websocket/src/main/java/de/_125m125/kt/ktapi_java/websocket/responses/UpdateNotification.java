package de._125m125.kt.ktapi_java.websocket.responses;

import java.util.Map;

import de._125m125.kt.ktapi_java.core.entities.Notification;

public class UpdateNotification extends Notification {

    public UpdateNotification(final boolean selfCreated, final long uid, final String base32Uid,
            final Map<String, String> details) {
        super(selfCreated, uid, base32Uid, "update", details);
    }

    public String getSource() {
        return getDetails().get("source");
    }

    public String getKey() {
        return getDetails().get("key");
    }

    public String getChannel() {
        return getDetails().get("channel");
    }

}
