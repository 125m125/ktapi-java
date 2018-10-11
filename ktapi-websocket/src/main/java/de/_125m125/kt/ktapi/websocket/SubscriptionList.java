package de._125m125.kt.ktapi.websocket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;

public class SubscriptionList {
    private final List<NotificationListener> otherListeners;
    private final List<NotificationListener> selfListeners;

    public SubscriptionList() {
        this.otherListeners = new CopyOnWriteArrayList<>();
        this.selfListeners = new CopyOnWriteArrayList<>();
    }

    public void addListener(final NotificationListener l, final boolean selfCreated) {
        if (selfCreated) {
            this.selfListeners.add(l);
        } else {
            this.otherListeners.add(l);
        }
    }

    public void notifyListeners(final Notification notification) {
        for (final NotificationListener nl : notification.isSelfCreated() ? this.selfListeners
                : this.otherListeners) {
            nl.update(notification);
        }
    }

    public void removeListener(final NotificationListener listener) {
        this.otherListeners.remove(listener);
        this.selfListeners.remove(listener);
    }

    @Override
    public String toString() {
        return "SubscriptionList [otherListeners=" + this.otherListeners + ", selfListeners="
                + this.selfListeners + "]";
    }

}
