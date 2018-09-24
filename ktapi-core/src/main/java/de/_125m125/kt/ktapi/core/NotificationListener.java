package de._125m125.kt.ktapi.core;

import de._125m125.kt.ktapi.core.entities.Notification;

@FunctionalInterface
public interface NotificationListener {
    /**
     * called when a Notification was received
     * 
     * @param notification
     *            the received notification
     */
    public void update(Notification notification);
}
