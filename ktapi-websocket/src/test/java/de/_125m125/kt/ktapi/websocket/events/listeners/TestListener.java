package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.ArrayList;
import java.util.List;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;

public class TestListener implements NotificationListener {

    private final List<Notification> lastNotifications = new ArrayList<>();

    @Override
    public void update(final Notification notification) {
        this.getLastNotifications().add(notification);
    }

    public List<Notification> getLastNotifications() {
        return lastNotifications;
    }

}
