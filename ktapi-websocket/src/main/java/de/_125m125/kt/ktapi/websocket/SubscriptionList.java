package de._125m125.kt.ktapi.websocket;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import de._125m125.kt.ktapi.core.KtNotificationManager.Priority;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;

public class SubscriptionList {
    private final EnumMap<Priority, Set<NotificationListener>> otherListeners;
    private final EnumMap<Priority, Set<NotificationListener>> selfListeners;

    public SubscriptionList() {
        this.otherListeners = new EnumMap<>(Priority.class);
        this.selfListeners = new EnumMap<>(Priority.class);
    }

    public synchronized void addListener(final NotificationListener l, final boolean selfCreated,
            final Priority priority) {
        if (selfCreated) {
            this.selfListeners.computeIfAbsent(priority, p -> new HashSet<>()).add(l);
        } else {
            this.otherListeners.computeIfAbsent(priority, p -> new HashSet<>()).add(l);
        }
    }

    public synchronized void notifyListeners(final Notification notification) {
        (notification.isSelfCreated() ? this.selfListeners.values() : this.otherListeners.values())
                .stream().flatMap(Set::stream).forEach(nl -> nl.update(notification));
    }

    public synchronized void removeListener(final NotificationListener listener) {
        Stream.concat(this.selfListeners.values().stream(), this.otherListeners.values().stream())
                .forEach(nl -> nl.remove(listener));
    }

    @Override
    public synchronized String toString() {
        return "SubscriptionList [otherListeners=" + this.otherListeners + ", selfListeners="
                + this.selfListeners + "]";
    }

}
