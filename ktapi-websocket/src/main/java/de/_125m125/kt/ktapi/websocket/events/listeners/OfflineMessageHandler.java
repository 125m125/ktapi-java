package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.ArrayList;
import java.util.List;

import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.BeforeMessageSendEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;

public class OfflineMessageHandler {

    private KtWebsocketManager         manager;

    private final List<RequestMessage> waitingRequests = new ArrayList<>();

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException("each session handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
    }

    @WebsocketEventListening
    public void beforeMessageSend(final BeforeMessageSendEvent e) {
        if (!e.getWebsocketStatus().isConnected()) {
            synchronized (this.waitingRequests) {
                this.waitingRequests.add(e.getMessage());
            }
            e.softCancel();
        }
    }

    @WebsocketEventListening
    public void onWebsocketConnect(final WebsocketConnectedEvent e) {
        new Thread(() -> {
            final List<RequestMessage> oldMessages;
            synchronized (this.waitingRequests) {
                oldMessages = new ArrayList<>(this.waitingRequests);
                this.waitingRequests.clear();
            }
            oldMessages.forEach(this.manager::sendMessage);
        }, "ResendCapturedOfflineMessageThread").start();
    }
}
