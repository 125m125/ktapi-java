/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
