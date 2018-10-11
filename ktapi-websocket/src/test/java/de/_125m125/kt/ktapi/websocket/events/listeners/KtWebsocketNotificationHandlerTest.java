package de._125m125.kt.ktapi.websocket.events.listeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketStatus;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi.websocket.responses.UpdateNotification;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

@RunWith(JUnitParamsRunner.class)
public class KtWebsocketNotificationHandlerTest {

    private KtWebsocketManager                           manager;
    private KtUserStore                                  store;
    private KtWebsocketNotificationHandler<TokenUserKey> uut;
    private final TokenUser                              knownUser   = new TokenUser("1", "2", "4");
    private final TokenUser                              knownUser2  = new TokenUser("1", "3", "5");
    private final TokenUser                              knownUser3  = new TokenUser("2", "3", "4");
    private final TokenUser                              unknownUser = new TokenUser("8", "16",
            "32");

    @Before
    public void beforeKtWebsocketNotificationHandlerTest() {
        this.manager = mock(KtWebsocketManager.class);
        this.store = new KtUserStore(this.knownUser, this.knownUser2, this.knownUser3);
        this.uut = new KtWebsocketNotificationHandler<>(this.store);
        this.uut.onWebsocketManagerCreated(new WebsocketManagerCreatedEvent(this.manager));

        doCallRealMethod().when(this.manager).sendRequest(any());
        doAnswer(invocation -> {
            final RequestMessage message = invocation.getArgumentAt(0, RequestMessage.class);
            message.getResult().setResponse(
                    new ResponseMessage(message.getRequestId().orElse(null), null, null, null));
            return null;
        }).when(this.manager).sendMessage(any());
    }

    @Test
    @Parameters(method = "multipleSubscribeParameters")
    @TestCaseName("testMultipleSubscribes: {0}")
    public void testMultipleSubscribes(final String name, final VerificationMode mode,
            final BiFunction<KtWebsocketNotificationHandler<TokenUserKey>, TestListener, CompletableFuture<NotificationListener>> subscribe1,
            final BiFunction<KtWebsocketNotificationHandler<TokenUserKey>, TestListener, CompletableFuture<NotificationListener>> subscribe2,
            final boolean success1, final boolean success2, final int requestCount) {
        final KtWebsocketNotificationHandler<TokenUserKey> localUut = new KtWebsocketNotificationHandler<>(
                this.store, mode);
        localUut.onWebsocketManagerCreated(new WebsocketManagerCreatedEvent(this.manager));

        doAnswer(invocation -> {
            final RequestMessage message = invocation.getArgumentAt(0, RequestMessage.class);
            message.getResult().setResponse(new ResponseMessage(message.getRequestId().orElse(null),
                    null, success1 ? null : "someError", null));
            return null;
        }).doAnswer(invocation -> {
            final RequestMessage message = invocation.getArgumentAt(0, RequestMessage.class);
            message.getResult().setResponse(new ResponseMessage(message.getRequestId().orElse(null),
                    null, success2 ? null : "someError", null));
            return null;
        }).when(this.manager).sendMessage(any());

        final TestListener listener1 = new TestListener();
        final CompletableFuture<NotificationListener> result1 = subscribe1.apply(localUut,
                listener1);

        final TestListener listener2 = new TestListener();
        final CompletableFuture<NotificationListener> result2 = subscribe2.apply(localUut,
                listener2);

        assertEquals(!success1, result1.isCompletedExceptionally());
        assertEquals(!success2, result2.isCompletedExceptionally());

        verify(this.manager, times(requestCount)).sendMessage(any());
    }

    public Object[][] multipleSubscribeParameters() {
        return new Object[][] {
                // @formatter:off
                { "ALWAYS rechecks every time", VerificationMode.ALWAYS,
                        createSubscribeMessages(this.knownUser.getKey(), false),
                        createSubscribeMessages(this.knownUser.getKey(), true), true, true, 2
                },
                { "UNKNOWN_UID checks only first success", VerificationMode.UNKNOWN_UID,
                        createSubscribeMessages(this.knownUser.getKey(), false),
                        createSubscribeMessages(this.knownUser.getKey(), true), true, true, 1
                },
                { "UNKNOWN_UID rechecks after first failure", VerificationMode.UNKNOWN_UID,
                        createSubscribeMessages(this.knownUser.getKey(), true),
                        createSubscribeMessages(this.knownUser.getKey(), true), false, true, 2
                },
                { "UNKNOWN_UID rechecks for ignores different token", VerificationMode.UNKNOWN_UID,
                        createSubscribeMessages(this.knownUser.getKey(), true),
                        createSubscribeMessages(this.knownUser2.getKey(), true), true, true, 1
                },
                { "UNKNOWN_UID rechecks for different user", VerificationMode.UNKNOWN_UID,
                        createSubscribeMessages(this.knownUser.getKey(), true),
                        createSubscribeMessages(this.knownUser3.getKey(), true), true, true, 2
                },
                { "UNKNOWN_UID rechecks for different type", VerificationMode.UNKNOWN_UID,
                        createSubscribeMessages(this.knownUser.getKey(), false),
                        createSubscribeItems(this.knownUser.getKey(), true), true, true, 2
                },
                { "UNKNOWN_TKN checks only first success", VerificationMode.UNKNOWN_TKN,
                        createSubscribeMessages(this.knownUser.getKey(), true),
                        createSubscribeMessages(this.knownUser.getKey(), true), true, true, 1
                },
                { "UNKNOWN_TKN rechecks after first failure", VerificationMode.UNKNOWN_TKN,
                        createSubscribeMessages(this.knownUser.getKey(), true),
                        createSubscribeMessages(this.knownUser.getKey(), true), false, true, 2
                },
                { "UNKNOWN_TKN rechecks for different token", VerificationMode.UNKNOWN_TKN,
                        createSubscribeMessages(this.knownUser.getKey(), false),
                        createSubscribeMessages(this.knownUser2.getKey(), true), true, true, 2
                },
                { "UNKNOWN_TKN rechecks for different user", VerificationMode.UNKNOWN_TKN,
                        createSubscribeMessages(this.knownUser.getKey(), true),
                        createSubscribeMessages(this.knownUser3.getKey(), true), true, true, 2
                },
                { "UNKNOWN_TKN rechecks for different type", VerificationMode.UNKNOWN_TKN,
                        createSubscribeMessages(this.knownUser.getKey(), false),
                        createSubscribeItems(this.knownUser.getKey(), true), true, true, 2
                },
                // @formatter:on
        };
    }

    private BiFunction<KtWebsocketNotificationHandler<TokenUserKey>, TestListener, CompletableFuture<NotificationListener>> createSubscribeMessages(
            final TokenUserKey key, final boolean selfCreated) {
        return (k, l) -> k.subscribeToMessages(l, key, selfCreated);
    }

    private BiFunction<KtWebsocketNotificationHandler<TokenUserKey>, TestListener, CompletableFuture<NotificationListener>> createSubscribeItems(
            final TokenUserKey key, final boolean selfCreated) {
        return (k, l) -> k.subscribeToItems(l, key, selfCreated);
    }

    @Test
    public void testListenerReceivesNotificationAfterSkippedSubscribe() {
        final Map<String, String> details = new HashMap<>();
        details.put("source", "messages");
        details.put("key", "1");
        details.put("channel", "rMessages");
        final UpdateNotification updateNotification = new UpdateNotification(false, 1, "1",
                details);

        final TestListener listener1 = new TestListener();
        this.uut.subscribeToMessages(listener1, this.knownUser.getKey(), false);

        final TestListener listener2 = new TestListener();
        this.uut.subscribeToMessages(listener2, this.knownUser.getKey(), false);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(updateNotification, listener1.getLastNotifications().get(0));
        assertEquals(1, listener1.getLastNotifications().size());

        assertEquals(updateNotification, listener2.getLastNotifications().get(0));
        assertEquals(1, listener2.getLastNotifications().size());
    }

    @Test
    public void testClassListenerReceivesNotification() {
        final TestListener tl = new TestListener();
        this.uut.subscribeToHistory(tl);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification updateNotification = new UpdateNotification(false, 0, "0",
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
        final UpdateNotification updateNotification = new UpdateNotification(false, 0, "0",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(updateNotification, un[0]);
    }

    @Test
    public void testUnsubscribedClassDoesNotReceiveNotifications() {
        final TestListener tl = new TestListener();
        this.uut.subscribeToHistory(tl);
        this.uut.unsubscribe(tl);

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification updateNotification = new UpdateNotification(false, 0, "0",
                details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(0, tl.getLastNotifications().size());
    }

    @Test
    public void testUnsubscribedLambdaDoesNotReceiveNotifications() throws Exception {
        final Notification[] un = new Notification[1];
        final CompletableFuture<NotificationListener> subscribeToHistory = this.uut
                .subscribeToHistory(u -> un[0] = u);
        this.uut.unsubscribe(subscribeToHistory.get());

        final Map<String, String> details = new HashMap<>();
        details.put("source", "history");
        details.put("key", "");
        details.put("channel", "history");
        final UpdateNotification updateNotification = new UpdateNotification(false, 0, "0",
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
        final UpdateNotification updateNotification = new UpdateNotification(false, 1, "1",
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
        final UpdateNotification updateNotification = new UpdateNotification(true, 1, "1", details);

        this.uut.onMessageReceived(
                new MessageReceivedEvent(new WebsocketStatus(true, true), updateNotification));

        assertEquals(0, tl.getLastNotifications().size());
        assertEquals(updateNotification, tl2.getLastNotifications().get(0));
        assertEquals(1, tl2.getLastNotifications().size());
        assertEquals(0, tl3.getLastNotifications().size());
        assertEquals(0, tl4.getLastNotifications().size());
    }
}
