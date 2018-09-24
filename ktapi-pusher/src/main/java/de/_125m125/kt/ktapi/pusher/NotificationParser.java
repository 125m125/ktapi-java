package de._125m125.kt.ktapi.pusher;

import de._125m125.kt.ktapi.core.entities.Notification;

public interface NotificationParser {

    public Notification parse(final String unescapedData);

}
