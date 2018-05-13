package de._125m125.kt.ktapi_java.websocket.events.listeners;

import de._125m125.kt.ktapi_java.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketDisconnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketManagerCreatedEvent;

public class AutoReconnectionHandler {

    private Thread             restart_wait_thread;
    private long               lastDelay;
    private KtWebsocketManager manager;

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException("each reconnection handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
    }

    @WebsocketEventListening
    public synchronized void onWebsocketConnected(final WebsocketConnectedEvent e) {
        this.lastDelay = 0;
        if (this.restart_wait_thread != null) {
            this.restart_wait_thread.interrupt();
        }
    }

    @WebsocketEventListening
    public void onWebsocketDisconnected(final WebsocketDisconnectedEvent e) {
        if (e.getWebsocketStatus().isActive()) {
            reConnectDelayed();
        }
    }

    /**
     * reconnects the websocket after a delay.
     */
    private synchronized void reConnectDelayed() {
        if (this.restart_wait_thread != null && this.restart_wait_thread.isAlive()
                && this.restart_wait_thread != Thread.currentThread()) {
            throw new IllegalStateException("this instance is already waiting for a reconnect");
        }
        this.restart_wait_thread = new Thread(() -> {
            this.lastDelay = this.lastDelay != 0 ? this.lastDelay * 2 : 1000;
            System.out.println(this.lastDelay);
            try {
                Thread.sleep(this.lastDelay);
                this.manager.connect();
            } catch (final InterruptedException e) {
            }
        });
        this.restart_wait_thread.setDaemon(false);
        this.restart_wait_thread.start();
    }
}
