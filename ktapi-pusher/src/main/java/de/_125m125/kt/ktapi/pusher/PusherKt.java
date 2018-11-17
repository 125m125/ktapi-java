package de._125m125.kt.ktapi.pusher;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.StampedLock;

import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;

public class PusherKt implements PrivateChannelEventListener,
        KtNotificationManager<TokenUserKey, NotificationListener> {
    private final Pusher                                 pusher;

    private final Map<String, Set<NotificationListener>> listeners     = new HashMap<>();
    private final StampedLock                            listenersLock = new StampedLock();

    private final NotificationParser                     parser;
    private final TokenUser                              user;

    public PusherKt(final TokenUser user, final NotificationParser parser,
            final Authorizer authorizer) {
        this.user = user;
        this.parser = parser;

        final PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        options.setEncrypted(true);
        options.setAuthorizer(authorizer);
        this.pusher = new Pusher("25ba65999fadc5a6e290", options);
        this.pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(final ConnectionStateChange change) {
            }

            @Override
            public void onError(final String message, final String code, final Exception e) {
            }
        }, ConnectionState.ALL);
    }

    @Override
    public void onEvent(final String channelname, final String eventName, final String data) {
        final String unescapedData = data.substring(1, data.length() - 1).replaceAll("\\\\\"",
                "\"");
        final Notification notification = this.parser.parse(unescapedData);
        final long stamp = this.listenersLock.tryOptimisticRead();
        Set<NotificationListener> receivers = this.listeners.get(channelname);
        if (!this.listenersLock.validate(stamp)) {
            final long readLock = this.listenersLock.readLock();
            try {
                receivers = this.listeners.get(channelname);
            } finally {
                this.listenersLock.unlockRead(readLock);
            }
        }
        for (final NotificationListener pl : receivers) {
            pl.update(notification);
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
            final String eventName, final NotificationListener listener) {
        final CompletableFuture<NotificationListener> result = new CompletableFuture<>();
        try {
            final boolean subscribe;
            final Set<NotificationListener> receivers;
            final long writeLock = this.listenersLock.writeLock();
            try {
                subscribe = !this.listeners.containsKey(channel);
                receivers = this.listeners.computeIfAbsent(channel,
                        e -> new CopyOnWriteArraySet<>());
            } finally {
                this.listenersLock.unlock(writeLock);
            }
            receivers.add(listener);
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
            final NotificationListener listener, final TokenUserKey user,
            final boolean selfCreated) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rMessages";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener);
        } else {
            return subscribe(channelName, "update", listener);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToTrades(
            final NotificationListener listener, final TokenUserKey user,
            final boolean selfCreated) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rOrders";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener);
        } else {
            return subscribe(channelName, "update", listener);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToItems(
            final NotificationListener listener, final TokenUserKey user,
            final boolean selfCreated) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rItems";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener);
        } else {
            return subscribe(channelName, "update", listener);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToPayouts(
            final NotificationListener listener, final TokenUserKey user,
            final boolean selfCreated) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rPayouts";
        if (selfCreated) {
            return subscribe(channelName.concat(".selfCreated"), "update", listener);
        } else {
            return subscribe(channelName, "update", listener);
        }
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToOrderbook(
            final NotificationListener listener) {
        final String channelName = "orderbook";
        return subscribe(channelName, "update", listener);
    }

    @Override
    public CompletableFuture<NotificationListener> subscribeToHistory(
            final NotificationListener listener) {
        final String channelName = "history";
        return subscribe(channelName, "update", listener);
    }

    @Override
    public CompletableFuture<NotificationListener>[] subscribeToAll(
            final NotificationListener listener, final TokenUserKey user,
            final boolean selfCreated) {
        if (!this.user.getKey().equals(user)) {
            throw new IllegalArgumentException(
                    "PusherKt only supports subscriptions for a single user");
        }
        return KtNotificationManager.super.subscribeToAll(listener, user, selfCreated);
    }

    @Override
    public void disconnect() {
        this.pusher.disconnect();
    }

    @Override
    public void unsubscribe(final NotificationListener listener) {
        final long writeLock = this.listenersLock.writeLock();
        try {
            this.listeners.values().forEach(l -> l.remove(listener));
        } finally {
            this.listenersLock.unlock(writeLock);
        }
    }

}
