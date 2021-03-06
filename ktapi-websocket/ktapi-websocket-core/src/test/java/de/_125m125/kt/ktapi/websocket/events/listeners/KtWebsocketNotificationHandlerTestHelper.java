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
package de._125m125.kt.ktapi.websocket.events.listeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;

import de._125m125.kt.ktapi.core.KtNotificationManager.Priority;
import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketStatus;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;

public abstract class KtWebsocketNotificationHandlerTestHelper<T> {

    @Rule
    public MockitoRule                                  rule        = MockitoJUnit.rule();

    protected final TokenUser                           knownUser   = new TokenUser("1", "2", "4");
    protected final TokenUser                           knownUser2  = new TokenUser("1", "3", "5");
    protected final TokenUser                           knownUser3  = new TokenUser("2", "3", "4");
    protected final TokenUser                           unknownUser = new TokenUser("8", "16",
            "32");

    @Mock
    protected KtWebsocketManager                        manager;

    @Mock
    protected Logger                                    logger;

    protected KtUserStore                               store;

    protected AbstractKtWebsocketNotificationHandler<T> uut;

    @Before
    public void beforeKtWebsocketNotificationHandlerTestHelper() {
        this.store = new KtUserStore(this.knownUser, this.knownUser2, this.knownUser3);
        this.uut = createNotificationListener(this.store, VerificationMode.UNKNOWN_TKN);
        this.uut.onWebsocketManagerCreated(new WebsocketManagerCreatedEvent(this.manager));

        doCallRealMethod().when(this.manager).sendRequest(any());
        doAnswer(invocation -> {
            final RequestMessage message = invocation.getArgumentAt(0, RequestMessage.class);
            message.getResult().setResponse(
                    new ResponseMessage(message.getRequestId().orElse(null), null, null, null));
            return null;
        }).when(this.manager).sendMessage(any());
    }

    @After
    public void afterKtWebsocketNotificationHandlerTestHelper() {
        this.uut.disconnect();
    }

    protected abstract AbstractKtWebsocketNotificationHandler<T> createNotificationListener(
            KtUserStore store, VerificationMode mode);

    @Test
    public void testClassListenerReceivesNotification() {
        final TestListener tl = new TestListener();
        this.uut.subscribeToHistory(tl);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification<?> updateNotification = new UpdateNotification<>(false, 0, "0",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(updateNotification, tl.getLastNotifications().get(0));
        assertEquals(1, tl.getLastNotifications().size());
    }

    @Test
    public void testLambdaListenerReceivesNotification() {
        final Notification[] un = new Notification[1];
        this.uut.subscribeToHistory(u -> un[0] = u);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification<?> updateNotification = new UpdateNotification<>(false, 0, "0",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(updateNotification, un[0]);
    }

    @Test
    public void testUnsubscribedClassDoesNotReceiveNotifications() {
        final TestListener tl = new TestListener();
        final CompletableFuture<T> subscribeToHistory = this.uut.subscribeToHistory(tl);
        this.uut.unsubscribe(subscribeToHistory);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification<?> updateNotification = new UpdateNotification<>(false, 0, "0",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(0, tl.getLastNotifications().size());
    }

    @Test
    public void testUnsubscribedLambdaDoesNotReceiveNotifications() throws Exception {
        final Notification[] un = new Notification[1];
        final CompletableFuture<T> subscribeToHistory = this.uut.subscribeToHistory(u -> un[0] = u);
        this.uut.unsubscribe(subscribeToHistory.get());

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification<?> updateNotification = new UpdateNotification<>(false, 0, "0",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(null, un[0]);
    }

    @Test
    public void testMessageNotifications() {
        this.store.add(this.unknownUser);

        final TestListener tl = new TestListener();
        this.uut.subscribeToMessages(tl, this.knownUser.getKey(), false);
        final TestListener tl2 = new TestListener();
        this.uut.subscribeToMessages(tl2, this.knownUser.getKey(), true);
        final TestListener tl3 = new TestListener();
        this.uut.subscribeToMessages(tl3, this.unknownUser.getKey(), true);
        final TestListener tl4 = new TestListener();
        this.uut.subscribeToMessages(tl4, this.knownUser.getKey(), false);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "messages");
        details.put("key", "1");
        details.put("channel", "rMessages");
        final UpdateNotification<?> updateNotification = new UpdateNotification<>(false, 1, "1",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(updateNotification, tl.getLastNotifications().get(0));
        assertEquals(1, tl.getLastNotifications().size());
        assertEquals(0, tl2.getLastNotifications().size());
        assertEquals(0, tl3.getLastNotifications().size());
        assertEquals(updateNotification, tl4.getLastNotifications().get(0));
        assertEquals(1, tl4.getLastNotifications().size());
    }

    @Test
    public void testItemNotifications() {
        this.store.add(this.unknownUser);

        final TestListener tl = new TestListener();
        this.uut.subscribeToItems(tl, this.knownUser.getKey(), false);
        final TestListener tl2 = new TestListener();
        this.uut.subscribeToItems(tl2, this.knownUser.getKey(), true);
        final TestListener tl3 = new TestListener();
        this.uut.subscribeToItems(tl3, this.unknownUser.getKey(), true);
        final TestListener tl4 = new TestListener();
        this.uut.subscribeToItems(tl4, this.knownUser.getKey(), false);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "items");
        details.put("key", "1");
        details.put("channel", "rItems");
        final UpdateNotification<?> updateNotification = new UpdateNotification<>(true, 1, "1",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(0, tl.getLastNotifications().size());
        assertEquals(updateNotification, tl2.getLastNotifications().get(0));
        assertEquals(1, tl2.getLastNotifications().size());
        assertEquals(0, tl3.getLastNotifications().size());
        assertEquals(0, tl4.getLastNotifications().size());
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
        final UpdateNotification<Object> notification = new UpdateNotification<>(false, 0, "0",
                details);
        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), notification));

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
        final UpdateNotification<Object> notification = new UpdateNotification<>(false, 0, "0",
                details);
        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), notification));

        for (int i = 0; i < listeners.length; i++) {
            inOrder.verify(listeners[i]).update(notification);
        }
    }
}
