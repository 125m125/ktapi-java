package de._125m125.kt.ktapi_java.pusher;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.StampedLock;

import com.google.gson.reflect.TypeToken;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import de._125m125.kt.ktapi_java.simple.Kt;
import de._125m125.kt.ktapi_java.simple.objects.User;

public class PusherKt extends Kt implements PrivateChannelEventListener {
    private final Pusher                            pusher;

    private final Map<String, List<PusherListener>> listeners     = new HashMap<>();
    private final StampedLock                       listenersLock = new StampedLock();

    public PusherKt(final User user) {
        super(user);
        final HttpAuthorizer authorizer = new HttpAuthorizer(Kt.BASE_URL + "pusher");
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
        final PusherNotification notification = (PusherNotification) Kt.jsonParser
                .parse(new StringReader(unescapedData), new TypeToken<PusherNotification>() {
                });
        final long stamp = this.listenersLock.tryOptimisticRead();
        List<PusherListener> receivers = this.listeners.get(channelname);
        if (!this.listenersLock.validate(stamp)) {
            final long readLock = this.listenersLock.readLock();
            try {
                receivers = this.listeners.get(channelname);
            } finally {
                this.listenersLock.unlockRead(readLock);
            }
        }
        for (final PusherListener pl : receivers) {
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

    public void subscribe(final String channel, final String eventName, final PusherListener listener) {
        final boolean subscribe;
        final List<PusherListener> receivers;
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

    public void subscribeToMessages(final PusherListener listener, final boolean selfCreated) {
        final String channelName = "private-" + this.user.getTID() + "_rMessages";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    public void subscribeToTrades(final PusherListener listener, final boolean selfCreated) {
        final String channelName = "private-" + this.user.getTID() + "_rOrders";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    public void subscribeToItems(final PusherListener listener, final boolean selfCreated) {
        final String channelName = "private-" + this.user.getTID() + "_rItems";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    public void subscribeToPayouts(final PusherListener listener, final boolean selfCreated) {
        final String channelName = "private-" + this.user.getTID() + "_rPayouts";
        subscribe(channelName, "update", listener);
        if (selfCreated) {
            subscribe(channelName.concat(".selfCreated"), "update", listener);
        }
    }

    public void subscribeToOrderbook(final PusherListener listener) {
        final String channelName = "order";
        subscribe(channelName, "update", listener);
    }

    public void subscribeToHistory(final PusherListener listener) {
        final String channelName = "history";
        subscribe(channelName, "update", listener);
    }

    public void subscribeToAll(final PusherListener listener, final boolean selfCreated) {
        subscribeToHistory(listener);
        subscribeToItems(listener, selfCreated);
        subscribeToMessages(listener, selfCreated);
        subscribeToOrderbook(listener);
        subscribeToPayouts(listener, selfCreated);
        subscribeToTrades(listener, selfCreated);
    }

    public void subscribeToUpdates(final PusherListener listener, final String path, final boolean selfCreated) {
        switch (path) {
            case "messages":
                subscribeToMessages(listener, selfCreated);
                break;
            case "trades":
                subscribeToTrades(listener, selfCreated);
                break;
            case "itemlist":
                subscribeToItems(listener, selfCreated);
                break;
            case "payouts":
                subscribeToPayouts(listener, selfCreated);
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
