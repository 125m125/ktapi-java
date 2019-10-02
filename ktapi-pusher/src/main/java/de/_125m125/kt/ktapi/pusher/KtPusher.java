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
package de._125m125.kt.ktapi.pusher;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.UserKey;

public class KtPusher
        implements PrivateChannelEventListener, KtNotificationManager<NotificationListener> {
    private static final class ConnectionEventListenerImplementation
            implements ConnectionEventListener {
        @Override
        public void onConnectionStateChange(final ConnectionStateChange change) {
        }

        @Override
        public void onError(final String message, final String code, final Exception e) {
        }
    }

    private final Pusher                                                    pusher;

    private final Map<String, EnumMap<Priority, Set<NotificationListener>>> listeners;

    private final NotificationParser                                        parser;
    private final TokenUser                                                 user;

    public KtPusher(final TokenUser user, final NotificationParser parser,
            final Authorizer authorizer) {
        this.user = user;
        this.parser = parser;
        this.listeners = new ConcurrentHashMap<>();

        final PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        options.setEncrypted(true);
        options.setAuthorizer(authorizer);
        this.pusher = createPusher(options);
    }

    protected Pusher createPusher(final PusherOptions options) {
        final Pusher pusher = new Pusher("25ba65999fadc5a6e290", options);
        pusher.connect(new ConnectionEventListenerImplementation(), ConnectionState.ALL);
        return pusher;
    }

    @Override
    public void onEvent(final PusherEvent event) {
        final String unescapedData = event.getData().substring(1, event.getData().length() - 1)
                .replaceAll("\\\\\"", "\"");
        final Notification notification = this.parser.parse(unescapedData);
        final EnumMap<Priority, Set<NotificationListener>> receivers =
                this.listeners.get(event.getChannelName());
        if (receivers != null) {
            synchronized (receivers) {
                receivers.values().stream().flatMap(Set::stream)
                        .forEach(r -> r.update(notification));
            }
        }
    }

    @Override
    public void onSubscriptionSucceeded(final String arg0) {
    }

    @Override
    public void onAuthenticationFailure(final String arg0, final Exception arg1) {
        arg1.printStackTrace();
    }

    public CompletableFuture<NotificationListener> subscribe(final String channel,
            final String eventName, final NotificationListener listener, final Priority priority) {
        final CompletableFuture<NotificationListener> result = new CompletableFuture<>();
        try {
            final boolean subscribe;
            final EnumMap<Priority, Set<NotificationListener>> receivers;
            subscribe = !this.listeners.containsKey(channel);
            receivers = this.listeners.computeIfAbsent(channel, e -> new EnumMap<>(Priority.class));
            synchronized (receivers) {
                receivers.computeIfAbsent(priority, p -> new HashSet<>()).add(listener);
            }
            if (subscribe) {
                if (channel.startsWith("private-")) {
                    this.pusher.subscribePrivate(channel, this, eventName);
                } else {
                    this.pusher.subscribe(channel, this, eventName);
                }
            }
            result.complete(listener);
        } catch (final Throwable t) {
            result.completeExceptionally(t);
        }
        return result;
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToMessages(
            final NotificationListener listener, final UserKey user, final boolean selfCreated,
            final Priority priority) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getUserId() + "_rMessages";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener, priority);
        } else {
            return subscribe(channelName, "update", listener, priority);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToTrades(
            final NotificationListener listener, final UserKey user, final boolean selfCreated,
            final Priority priority) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getUserId() + "_rOrders";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener, priority);
        } else {
            return subscribe(channelName, "update", listener, priority);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToItems(
            final NotificationListener listener, final UserKey user, final boolean selfCreated,
            final Priority priority) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getUserId() + "_rItems";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener, priority);
        } else {
            return subscribe(channelName, "update", listener, priority);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToPayouts(
            final NotificationListener listener, final UserKey user, final boolean selfCreated,
            final Priority priority) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getUserId() + "_rPayouts";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener, priority);
        } else {
            return subscribe(channelName, "update", listener, priority);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToOrderbook(
            final NotificationListener listener, final Priority priority) {
        final String channelName = "orderbook";
        return subscribe(channelName, "update", listener, priority);
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToHistory(
            final NotificationListener listener, final Priority priority) {
        final String channelName = "history";
        return subscribe(channelName, "update", listener, priority);
    }

    @Override
    public void disconnect() {
        this.pusher.disconnect();
    }

    @Override
    public void unsubscribe(final NotificationListener listener) {
        this.listeners.values().forEach(l -> {
            synchronized (l) {
                l.values().forEach(v -> v.remove(listener));
            }
        });
    }

}
