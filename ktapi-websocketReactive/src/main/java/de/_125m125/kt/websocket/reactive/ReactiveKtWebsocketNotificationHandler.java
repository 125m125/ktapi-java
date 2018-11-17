package de._125m125.kt.websocket.reactive;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.VerificationMode;
import de._125m125.kt.ktapi.websocket.requests.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.responses.UpdateNotification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ReactiveKtWebsocketNotificationHandler<T extends TokenUserKey>
        extends AbstractKtWebsocketNotificationHandler<T, Disposable> {
    private static final Logger               logger  = LoggerFactory
            .getLogger(ReactiveKtWebsocketNotificationHandler.class);
    private final Subject<UpdateNotification> subject = PublishSubject.<UpdateNotification>create()
            .toSerialized();

    public ReactiveKtWebsocketNotificationHandler(final KtUserStore userStore,
            final VerificationMode mode) {
        super(ReactiveKtWebsocketNotificationHandler.logger, userStore, mode);
    }

    @Override
    public void unsubscribe(final Disposable listener) {
        listener.dispose();
    }

    @Override
    protected void addListener(final SubscriptionRequestData request, final String source,
            final String key, final NotificationListener listener,
            final CompletableFuture<Disposable> result) {
        Observable<UpdateNotification> filter = this.subject
                .filter(n -> source.equals(n.getSource()));
        if (key != null) {
            filter = filter.filter(n -> key.equals(n.getKey()));
        }
        filter = filter.filter(n -> request.isSelfCreated() == n.isSelfCreated());
        result.complete(filter.subscribe(listener::update));
    }

    @Override
    public void disconnect() {
        super.disconnect();
        this.subject.onComplete();
    }

    @WebsocketEventListening
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getMessage() instanceof UpdateNotification) {
            ReactiveKtWebsocketNotificationHandler.logger.trace("Received UpdateNotification {}",
                    e.getMessage());
            this.subject.onNext((UpdateNotification) e.getMessage());
        }
    }

}
