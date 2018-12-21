package de._125m125.kt.ktapi.websocket.events.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestDataFactory;
import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

@RunWith(JUnitParamsRunner.class)
public class AbstractKtWebsocketNotificationHandlerTest {
    private static final class AbstrKtWebsocketNotificationHandlerExt
            extends AbstractKtWebsocketNotificationHandler<NotificationListener> {

        Set<NotificationListener> listeners = new HashSet<>();

        private AbstrKtWebsocketNotificationHandlerExt(final Logger logger,
                final KtUserStore userStore, final VerificationMode mode,
                final KtWebsocketManager manager) {
            super(logger, userStore, mode, new SubscriptionRequestDataFactory());
            onWebsocketManagerCreated(new WebsocketManagerCreatedEvent(manager));
        }

        @Override
        public void unsubscribe(final NotificationListener listener) {
        }

        @Override
        public void onMessageReceived(final MessageReceivedEvent e) {
        }

        @Override
        protected void addListener(final SubscriptionRequestData request, final String source,
                final String key, final NotificationListener listener,
                final CompletableFuture<NotificationListener> result) {
            this.listeners.add(listener);
            result.complete(listener);
        }
    }

    @Rule
    public MockitoRule                             rule       = MockitoJUnit.rule();

    private final TokenUser                        knownUser  = new TokenUser("1", "2", "4");
    private final TokenUser                        knownUser2 = new TokenUser("1", "3", "5");
    private final TokenUser                        knownUser3 = new TokenUser("2", "3", "4");

    @Mock
    private KtWebsocketManager                     manager;

    @Mock
    private Logger                                 logger;

    private KtUserStore                            store;

    private AbstrKtWebsocketNotificationHandlerExt uut;

    @Before
    public void beforeKtWebsocketNotificationHandlerTest() {
        this.store = new KtUserStore(this.knownUser, this.knownUser2, this.knownUser3);
        this.uut = new AbstrKtWebsocketNotificationHandlerExt(this.logger, this.store,
                VerificationMode.UNKNOWN_TKN, this.manager);

        doCallRealMethod().when(this.manager).sendRequest(any());
        doAnswer(invocation -> {
            final RequestMessage message = invocation.getArgumentAt(0, RequestMessage.class);
            message.getResult().setResponse(
                    new ResponseMessage(message.getRequestId().orElse(null), null, null, null));
            return null;
        }).when(this.manager).sendMessage(any());
    }

    @Test
    public void testListenerReceivesNotificationAfterSkippedSubscribe() {
        final TestListener listener1 = new TestListener();
        this.uut.subscribeToMessages(listener1, this.knownUser.getKey(), false);

        final TestListener listener2 = new TestListener();
        this.uut.subscribeToMessages(listener2, this.knownUser.getKey(), false);

        assertTrue(this.uut.listeners.contains(listener1));
        assertTrue(this.uut.listeners.contains(listener2));
    }

    @Test
    @Parameters(method = "multipleSubscribeParameters")
    @TestCaseName("testMultipleSubscribes: {0}")
    public void testMultipleSubscribes(final String name, final VerificationMode mode,
            final BiFunction<AbstractKtWebsocketNotificationHandler<NotificationListener>, TestListener, CompletableFuture<NotificationListener>> subscribe1,
            final BiFunction<AbstractKtWebsocketNotificationHandler<NotificationListener>, TestListener, CompletableFuture<NotificationListener>> subscribe2,
            final boolean success1, final boolean success2, final int requestCount) {
        final AbstractKtWebsocketNotificationHandler<NotificationListener> localUut = new AbstrKtWebsocketNotificationHandlerExt(
                this.logger, this.store, mode, this.manager);

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

    private BiFunction<AbstractKtWebsocketNotificationHandler<NotificationListener>, TestListener, CompletableFuture<NotificationListener>> createSubscribeMessages(
            final TokenUserKey key, final boolean selfCreated) {
        return (k, l) -> k.subscribeToMessages(l, key, selfCreated);
    }

    private BiFunction<AbstractKtWebsocketNotificationHandler<NotificationListener>, TestListener, CompletableFuture<NotificationListener>> createSubscribeItems(
            final TokenUserKey key, final boolean selfCreated) {
        return (k, l) -> k.subscribeToItems(l, key, selfCreated);
    }
}
