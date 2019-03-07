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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.core.NotificationListener;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.websocket.SubscriptionList;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestData;
import de._125m125.kt.ktapi.websocket.requests.subscription.SubscriptionRequestDataFactory;

public class KtWebsocketNotificationHandler
        extends AbstractKtWebsocketNotificationHandler<NotificationListener> {
    private static final Logger                              logger        = LoggerFactory
            .getLogger(KtWebsocketNotificationHandler.class);

    /**
     * all active subscriptions. First key: channel. Second key: key. Null in the second key means,
     * that the client is interested in all events on this channel.
     */
    private final Map<String, Map<String, SubscriptionList>> subscriptions = new HashMap<>();

    public KtWebsocketNotificationHandler(final KtUserStore userStore) {
        this(userStore, VerificationMode.UNKNOWN_TKN);
    }

    public KtWebsocketNotificationHandler(final KtUserStore userStore,
            final VerificationMode mode) {
        this(userStore, mode, new SubscriptionRequestDataFactory());
    }

    public KtWebsocketNotificationHandler(final KtUserStore userStore, final VerificationMode mode,
            final SubscriptionRequestDataFactory factory) {
        super(KtWebsocketNotificationHandler.logger, userStore, mode, factory);
    }

    @Override
    @WebsocketEventListening
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getMessage() instanceof UpdateNotification) {
            KtWebsocketNotificationHandler.logger.trace("Received UpdateNotification {}",
                    e.getMessage());
            SubscriptionList keyList = null;
            SubscriptionList unkeyedList = null;
            final UpdateNotification<?> notificationMessage = (UpdateNotification<?>) e
                    .getMessage();
            synchronized (this.subscriptions) {
                final Map<String, SubscriptionList> sourceMap = this.subscriptions
                        .get(notificationMessage.getSource());
                if (sourceMap != null) {
                    keyList = sourceMap.get(notificationMessage.getKey());
                    unkeyedList = sourceMap.get(null);
                }
            }
            if (keyList != null) {
                keyList.notifyListeners(notificationMessage);
            } else {
                KtWebsocketNotificationHandler.logger.debug(
                        "No listeners found for notifications on {}.{}",
                        notificationMessage.getSource(), notificationMessage.getKey());
            }
            if (unkeyedList != null) {
                unkeyedList.notifyListeners(notificationMessage);
            } else {
                KtWebsocketNotificationHandler.logger.debug(
                        "No listeners found for notifications on {}.*",
                        notificationMessage.getSource());
            }
        }
    }

    @Override
    protected void addListener(final SubscriptionRequestData request, final String source,
            final String key, final NotificationListener listener,
            final CompletableFuture<NotificationListener> result, final Priority priority) {
        final SubscriptionList subList;
        synchronized (this.subscriptions) {
            subList = this.subscriptions.computeIfAbsent(source, n -> new HashMap<>())
                    .computeIfAbsent(key, n -> new SubscriptionList());
        }
        subList.addListener(listener, request.isSelfCreated(), priority);
        result.complete(listener);
        KtWebsocketNotificationHandler.logger.info("successfully added listener {} to {}.{}",
                listener, source, key);
        KtWebsocketNotificationHandler.logger.debug("new listener map: {}", this.subscriptions);
    }

    @Override
    public void unsubscribe(final NotificationListener listener) {
        synchronized (this.subscriptions) {
            this.subscriptions.values()
                    .forEach(m -> m.values().forEach(sl -> sl.removeListener(listener)));
            KtWebsocketNotificationHandler.logger.info("unsubscribed listener {}", listener);
            KtWebsocketNotificationHandler.logger.debug("new listener map: {}", this.subscriptions);
        }
    }

}
