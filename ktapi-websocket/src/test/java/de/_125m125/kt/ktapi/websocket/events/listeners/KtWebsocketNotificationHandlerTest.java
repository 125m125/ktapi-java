package de._125m125.kt.ktapi.websocket.events.listeners;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;

public class KtWebsocketNotificationHandlerTest
        extends KtWebsocketNotificationHandlerTestHelper<NotificationListener> {

    @Override
    protected AbstractKtWebsocketNotificationHandler<TokenUser, TokenUserKey, NotificationListener> createNotificationListener(
            final KtUserStore store, final VerificationMode mode) {
        return new KtWebsocketNotificationHandler<>(store, mode);
    }

}
