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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.BeforeMessageSendEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;

public class OfflineMessageHandler {
    private static final Logger        logger          = LoggerFactory
            .getLogger(OfflineMessageHandler.class);

    private KtWebsocketManager         manager;

    private final List<RequestMessage> waitingRequests = new ArrayList<>();

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException(
                    "each session handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
    }

    @WebsocketEventListening
    public void beforeMessageSend(final BeforeMessageSendEvent e) {
        if (!e.getWebsocketStatus().isConnected() && e.getMessage().getRequestId().isPresent()) {
            OfflineMessageHandler.logger.info(
                    "intercepting message {} since websocket is not connected",
                    e.getMessage().getRequestId().get());
            synchronized (this.waitingRequests) {
                this.waitingRequests.add(e.getMessage());
            }
            e.softCancel();
        }
    }

    @WebsocketEventListening
    public synchronized void onWebsocketConnect(final WebsocketConnectedEvent e) {
        final KtWebsocketManager myManager = this.manager;
        new Thread(() -> {
            final List<RequestMessage> oldMessages;
            synchronized (this.waitingRequests) {
                oldMessages = new ArrayList<>(this.waitingRequests);
                this.waitingRequests.clear();
            }
            OfflineMessageHandler.logger.info("resending messages {}",
                    oldMessages.stream().map(m -> m.getRequestId().get().toString())
                            .collect(Collectors.joining(",", "[", "]")));
            oldMessages.forEach(myManager::sendMessage);
        }, "ResendCapturedOfflineMessageThread").start();
    }
}
