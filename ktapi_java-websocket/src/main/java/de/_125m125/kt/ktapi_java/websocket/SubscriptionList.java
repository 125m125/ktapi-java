package de._125m125.kt.ktapi_java.websocket;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.entities.Notification;
import de._125m125.kt.ktapi_java.core.entities.User;

public class SubscriptionList {
    private final List<NotificationListener> otherListeners;
    private final List<NotificationListener> selfListeners;
    private Optional<User>                   owner;

    public SubscriptionList() {
        this(null);
    }

    public SubscriptionList(final User u) {
        this.owner = Optional.ofNullable(u);
        this.otherListeners = new CopyOnWriteArrayList<>();
        this.selfListeners = new CopyOnWriteArrayList<>();
    }

    public void setOwner(final User u) {
        this.owner = Optional.of(u);
    }

    public void addListener(final NotificationListener l, final boolean selfCreated) {
        if (selfCreated) {
            this.selfListeners.add(l);
        } else {
            this.otherListeners.add(l);
        }
    }

    public void notifyListeners(final Notification notification) {
        for (final NotificationListener nl : notification.isSelfCreated() ? this.selfListeners : this.otherListeners) {
            nl.update(notification);
        }
    }

    public Optional<User> getOwner() {
        return this.owner;
    }

}
