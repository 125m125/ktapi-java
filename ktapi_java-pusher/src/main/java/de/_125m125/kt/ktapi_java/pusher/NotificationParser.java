package de._125m125.kt.ktapi_java.pusher;

import de._125m125.kt.ktapi_java.core.entities.Notification;

public interface NotificationParser {

    public Notification parse(final String unescapedData);

}
