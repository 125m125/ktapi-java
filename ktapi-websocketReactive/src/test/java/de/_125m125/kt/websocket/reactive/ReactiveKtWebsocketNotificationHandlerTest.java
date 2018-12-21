package de._125m125.kt.websocket.reactive;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.KtWebsocketNotificationHandlerTestHelper;
import de._125m125.kt.ktapi.websocket.events.listeners.VerificationMode;
import de._125m125.kt.ktapi.websocket.responses.UpdateNotification;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;

public class ReactiveKtWebsocketNotificationHandlerTest
        extends KtWebsocketNotificationHandlerTestHelper<Disposable> {

    @Override
    protected AbstractKtWebsocketNotificationHandler<TokenUser, TokenUserKey, Disposable> createNotificationListener(
            final KtUserStore store, final VerificationMode mode) {
        return new ReactiveKtWebsocketNotificationHandler<>(store, mode);
    }

    @Test
    public void testGetPayoutObservableForId() throws Exception {
        final ReactiveKtWebsocketNotificationHandler<TokenUser, TokenUserKey> uut = new ReactiveKtWebsocketNotificationHandler<>(
                this.store, VerificationMode.UNKNOWN_TKN);
        final TestObserver<Payout> subscriber = new TestObserver<>();
        final Map<String, String> details = new HashMap<>();
        details.put("key", "F");
        details.put("source", AbstractKtWebsocketNotificationHandler.PAYOUTS);

        uut.getPayoutObservable(1L).subscribe(subscriber);

        uut.subject.onNext(new UpdateNotification<>(false, 15L, "F", details,
                new Payout[] { new Payout(1, "4", "Cobblestone (4)", 23, "IN_PROGRESS", "Box1",
                        "2018-01-01 00:00:00.0", "") }));
        uut.subject.onNext(
                new UpdateNotification<>(false, 15L, "F", details, new Payout[] { new Payout(1, "4",
                        "Cobblestone (4)", 23, "SUCCESS", "Box1", "2018-01-01 00:00:00.0", "") }));

        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(2);
    }
}
