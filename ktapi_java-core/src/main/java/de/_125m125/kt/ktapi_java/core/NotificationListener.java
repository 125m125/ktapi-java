package de._125m125.kt.ktapi_java.core;

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
