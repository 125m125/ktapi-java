package de._125m125.kt.ktapi_java.pusher;

@FunctionalInterface
public interface PusherListener {
    public void update(PusherNotification notification);
}
