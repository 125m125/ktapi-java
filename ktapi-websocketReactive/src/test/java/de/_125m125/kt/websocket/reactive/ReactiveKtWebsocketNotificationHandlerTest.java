package de._125m125.kt.websocket.reactive;

import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.KtWebsocketNotificationHandlerTestHelper;
import de._125m125.kt.ktapi.websocket.events.listeners.VerificationMode;
import io.reactivex.disposables.Disposable;

public class ReactiveKtWebsocketNotificationHandlerTest
        extends KtWebsocketNotificationHandlerTestHelper<Disposable> {

    @Override
    protected AbstractKtWebsocketNotificationHandler<TokenUserKey, Disposable> createNotificationListener(
            final KtUserStore store, final VerificationMode mode) {
        return new ReactiveKtWebsocketNotificationHandler<>(store, mode);
    }
}
