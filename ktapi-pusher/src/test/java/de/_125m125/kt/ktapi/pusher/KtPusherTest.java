package de._125m125.kt.ktapi.pusher;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;

import de._125m125.kt.ktapi.core.KtNotificationManager.Priority;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.users.TokenUser;

public class KtPusherTest {
    private KtPusher    uut;
    private TokenUser   user;
    private KtRequester requester;
    private Pusher      pusher;

    @Before
    public void beforeKtPusherTest() {
        this.requester = mock(KtRequester.class);
        this.user = new TokenUser("1", "2", "4");
        this.pusher = mock(Pusher.class);
        this.uut = new KtPusher(this.user, unescapedData -> {
            System.out.println(unescapedData);
            return new Gson().fromJson(unescapedData, Notification.class);
        }, new KtPusherAuthorizer(this.user.getKey(), this.requester)) {
            @Override
            protected Pusher createPusher(final PusherOptions options) {
                return KtPusherTest.this.pusher;
            }
        };
    }

    @Test
    public void testListenersReceiveNotificationsOrderedBypriorityInorderSubscribe() {
        final NotificationListener[] listeners = new NotificationListener[5];
        for (int i = 0; i < Priority.values().length; i++) {
            final Priority priority = Priority.values()[i];
            listeners[i] = mock(NotificationListener.class);
            this.uut.subscribeToOrderbook(listeners[i], priority);
        }
        final InOrder inOrder = Mockito.inOrder((Object[]) listeners);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "orderbook");
        details.put("key", "1");
        details.put("channel", "orderbook");
        final Notification notification = new Notification(false, 0, "0", "update", details);
        this.uut.onEvent("orderbook", "update",
                "\"" + new Gson().toJson(notification).replaceAll("\"", "\\\\\"") + "\"");

        for (int i = 0; i < listeners.length; i++) {
            inOrder.verify(listeners[i]).update(notification);
        }
    }

    @Test
    public void testListenersReceiveNotificationsOrderedBypriorityReverseSubscribe() {
        final NotificationListener[] listeners = new NotificationListener[5];
        for (int i = Priority.values().length - 1; i >= 0; i--) {
            final Priority priority = Priority.values()[i];
            listeners[i] = mock(NotificationListener.class);
            this.uut.subscribeToOrderbook(listeners[i], priority);
        }
        final InOrder inOrder = Mockito.inOrder((Object[]) listeners);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "orderbook");
        details.put("key", "1");
        details.put("channel", "orderbook");
        final Notification notification = new Notification(false, 0, "0", "update", details);
        this.uut.onEvent("orderbook", "update",
                "\"" + new Gson().toJson(notification).replaceAll("\"", "\\\\\"") + "\"");

        for (int i = 0; i < listeners.length; i++) {
            inOrder.verify(listeners[i]).update(notification);
        }
    }
}
