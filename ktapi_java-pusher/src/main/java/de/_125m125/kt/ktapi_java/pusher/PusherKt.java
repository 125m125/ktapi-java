package de._125m125.kt.ktapi_java.pusher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.StampedLock;

import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.entities.Notification;
import de._125m125.kt.ktapi_java.core.entities.UserKey;

public class PusherKt<T extends UserKey> implements PrivateChannelEventListener, KtNotificationManager<T> {
    private final Pusher                                  pusher;

    private final Map<String, List<NotificationListener>> listeners     = new HashMap<>();
    private final StampedLock                             listenersLock = new StampedLock();

    private final NotificationParser                      parser;
    private final T                                       user;

    public PusherKt(final T user, final NotificationParser parser, final Authorizer authorizer) {
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
        final String unescapedData = data.substring(1, data.length() - 1).replaceAll("\\\\\"", "\"");
        final Notification notification = this.parser.parse(unescapedData);
        final long stamp = this.listenersLock.tryOptimisticRead();
        List<NotificationListener> receivers = this.listeners.get(channelname);
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

    public void subscribe(final String channel, final String eventName, final NotificationListener listener) {
        final boolean subscribe;
        final List<NotificationListener> receivers;
        final long writeLock = this.listenersLock.writeLock();
        try {
            subscribe = !this.listeners.containsKey(channel);
            receivers = this.listeners.computeIfAbsent(channel, e -> new CopyOnWriteArrayList<>());
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
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToMessages(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToMessages(final NotificationListener listener, final T user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rMessages";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToTrades(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToTrades(final NotificationListener listener, final T user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rOrders";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToItems(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToItems(final NotificationListener listener, final T user, final boolean selfCreated) {
        if (!this.user.equals(user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rItems";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToPayouts(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToPayouts(final NotificationListener listener, final T user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTid() + "_rPayouts";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToOrderbook(de._125m125.kt.ktapi_java.pusher.NotificationListener)
     */
    @Override
    public void subscribeToOrderbook(final NotificationListener listener) {
        final String channelName = "orderbook";
        subscribe(channelName, "update", listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToHistory(de._125m125.kt.ktapi_java.pusher.NotificationListener)
     */
    @Override
    public void subscribeToHistory(final NotificationListener listener) {
        final String channelName = "history";
        subscribe(channelName, "update", listener);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToAll(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToAll(final NotificationListener listener, final T user, final boolean selfCreated) {
        if (!this.user.equals(user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        KtNotificationManager.super.subscribeToAll(listener, user, selfCreated);
    }

    @Override
    public void disconnect() {
        this.pusher.disconnect();
    }

}
