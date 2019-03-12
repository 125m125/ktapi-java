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
package de._125m125.kt.websocket.reactive;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de._125m125.kt.ktapi.core.KtNotificationManager.Priority;
import de._125m125.kt.ktapi.core.entities.Entity;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.KtWebsocketNotificationHandlerTestHelper;
import de._125m125.kt.ktapi.websocket.events.listeners.VerificationMode;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;

public class ReactiveKtWebsocketNotificationHandlerTest
        extends KtWebsocketNotificationHandlerTestHelper<Disposable> {

    @Override
    protected AbstractKtWebsocketNotificationHandler<Disposable> createNotificationListener(
            final KtUserStore store, final VerificationMode mode) {
        return new ReactiveKtWebsocketNotificationHandler(store, mode);
    }

    @Test
    public void testGetPayoutObservableForId() throws Exception {
        final ReactiveKtWebsocketNotificationHandler uut = new ReactiveKtWebsocketNotificationHandler(
                this.store, VerificationMode.UNKNOWN_TKN);
        final TestObserver<Payout> subscriber = new TestObserver<>();
        final Map<String, String> details = new HashMap<>();
        details.put("key", "F");
        details.put("source", Entity.PAYOUT.getUpdateChannel());

        uut.getPayoutObservable(1L, Priority.NORMAL).subscribe(subscriber);

        uut.subjects.get(Priority.NORMAL)
                .onNext(new UpdateNotification<>(false, 15L, "F", details,
                        new Payout[] { new Payout(1, "4", "Cobblestone (4)", 23, "IN_PROGRESS",
                                "Box1", "2018-01-01 00:00:00.0", "") }));
        uut.subjects.get(Priority.NORMAL).onNext(
                new UpdateNotification<>(false, 15L, "F", details, new Payout[] { new Payout(1, "4",
                        "Cobblestone (4)", 23, "SUCCESS", "Box1", "2018-01-01 00:00:00.0", "") }));

        subscriber.assertComplete();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(2);
    }
}
