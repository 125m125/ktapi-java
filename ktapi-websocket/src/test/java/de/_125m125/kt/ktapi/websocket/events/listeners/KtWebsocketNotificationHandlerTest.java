package de._125m125.kt.ktapi.websocket.events.listeners;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.users.KtUserStore;

public class KtWebsocketNotificationHandlerTest
        extends KtWebsocketNotificationHandlerTestHelper<NotificationListener> {

    @Override
    protected AbstractKtWebsocketNotificationHandler<NotificationListener> createNotificationListener(
            final KtUserStore store, final VerificationMode mode) {
        return new KtWebsocketNotificationHandler(store, mode);
    }

}
