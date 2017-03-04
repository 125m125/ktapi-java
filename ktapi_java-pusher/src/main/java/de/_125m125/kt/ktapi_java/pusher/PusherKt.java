package de._125m125.kt.ktapi_java.pusher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.StampedLock;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import de._125m125.kt.ktapi_java.core.JsonParser;
import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.Notification;
import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.objects.User;

public class PusherKt implements PrivateChannelEventListener, KtNotificationManager {
    private final Pusher                                  pusher;

    private final Map<String, List<NotificationListener>> listeners     = new HashMap<>();
    private final StampedLock                             listenersLock = new StampedLock();

    private final JsonParser<Notification>                parser;
    private final User                                    user;

    public PusherKt(final User user, final JsonParser<Notification> parser, final String baseUrl) {
        this.user = user;
        this.parser = parser;
        final HttpAuthorizer authorizer = new HttpAuthorizer(baseUrl + "pusher");
        final HashMap<String, String> parameters = new HashMap<>(3);
        parameters.put("uid", user.getUID());
        parameters.put("tid", user.getTID());
        parameters.put("tkn", user.getTKN());
        authorizer.setQueryStringParameters(parameters);
        final PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        options.setEncrypted(true);
        options.setAuthorizer(authorizer);
        this.pusher = new Pusher("25ba65999fadc5a6e290", options);
        this.pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(final ConnectionStateChange change) {
                System.out
                        .println("State changed to " + change.getCurrentState() + " from " + change.getPreviousState());
            }

            @Override
            public void onError(final String message, final String code, final Exception e) {
                System.out.println("There was a problem connecting!");
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
        System.out.println("subscribeSuccess");
    }

    @Override
    public void onAuthenticationFailure(final String arg0, final Exception arg1) {
        System.out.println(arg0);
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
            final Channel subChannel;
            if (channel.startsWith("private-")) {
                subChannel = this.pusher.subscribePrivate(channel, this, eventName);
            } else {
                subChannel = this.pusher.subscribe(channel, this, eventName);
            }
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToMessages(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToMessages(final NotificationListener listener, final User user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTID() + "_rMessages";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToTrades(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToTrades(final NotificationListener listener, final User user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTID() + "_rOrders";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToItems(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToItems(final NotificationListener listener, final User user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTID() + "_rItems";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToPayouts(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, boolean)
     */
    @Override
    public void subscribeToPayouts(final NotificationListener listener, final User user, final boolean selfCreated) {
        if (!user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        final String channelName = "private-" + user.getTID() + "_rPayouts";
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
        final String channelName = "order";
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
    public void subscribeToAll(final NotificationListener listener, final User u, final boolean selfCreated) {
        if (!this.user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        subscribeToHistory(listener);
        subscribeToItems(listener, u, selfCreated);
        subscribeToMessages(listener, u, selfCreated);
        subscribeToOrderbook(listener);
        subscribeToPayouts(listener, u, selfCreated);
        subscribeToTrades(listener, u, selfCreated);
    }

    /* (non-Javadoc)
     * @see de._125m125.kt.ktapi_java.pusher.KtNotificationManager#subscribeToUpdates(de._125m125.kt.ktapi_java.pusher.NotificationListener, de._125m125.kt.ktapi_java.core.objects.User, java.lang.String, boolean)
     */
    @Override
    public void subscribeToUpdates(final NotificationListener listener, final User u, final String path,
            final boolean selfCreated) {
        if (!this.user.equals(this.user)) {
            throw new IllegalArgumentException("PusherKt only supports subscriptions for a single user");
        }
        switch (path) {
            case "messages":
                subscribeToMessages(listener, u, selfCreated);
                break;
            case "trades":
                subscribeToTrades(listener, u, selfCreated);
                break;
            case "itemlist":
                subscribeToItems(listener, u, selfCreated);
                break;
            case "payouts":
                subscribeToPayouts(listener, u, selfCreated);
                break;
            case "history":
                subscribeToHistory(listener);
                break;
            case "order":
                subscribeToOrderbook(listener);
                break;
        }
    }

}
