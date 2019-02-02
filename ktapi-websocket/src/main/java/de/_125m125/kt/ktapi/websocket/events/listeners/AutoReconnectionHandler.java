package de._125m125.kt.ktapi.websocket.events.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketDisconnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;

public class AutoReconnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutoReconnectionHandler.class);

    private Thread              restartWaitThread;
    private long                lastDelay;
    private KtWebsocketManager  manager;

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException(
                    "each reconnection handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
    }

    @WebsocketEventListening
    public synchronized void onWebsocketConnected(final WebsocketConnectedEvent e) {
        AutoReconnectionHandler.logger
                .debug("Websocket was connected successfully. Resetting reconnection delay");
        this.lastDelay = 0;
        if (this.restartWaitThread != null) {
            this.restartWaitThread.interrupt();
        }
    }

    @WebsocketEventListening
    public void onWebsocketDisconnected(final WebsocketDisconnectedEvent e) {
        if (e.getWebsocketStatus().isActive()) {
            AutoReconnectionHandler.logger
                    .info("Websocket was disconnected while still being active. "
                            + "Starting delay reconnection.");
            reConnectDelayed();
        }
    }

    /**
     * reconnects the websocket after a delay.
     */
    private synchronized void reConnectDelayed() {
        if (this.restartWaitThread != null && this.restartWaitThread.isAlive()
                && this.restartWaitThread != Thread.currentThread()) {
            throw new IllegalStateException("this instance is already waiting for a reconnect");
        }
        final KtWebsocketManager myManager = this.manager;
        this.restartWaitThread = new Thread(() -> {
            this.lastDelay = this.lastDelay != 0 ? this.lastDelay * 2 : 1000;
            System.out.println(this.lastDelay);
            try {
                AutoReconnectionHandler.logger.debug("Wating for {} ms before reconnecting.",
                        this.lastDelay);
                Thread.sleep(this.lastDelay);
            } catch (final InterruptedException e) {
                AutoReconnectionHandler.logger
                        .warn("Reconnect was interrupted while waiting for delay. "
                                + "Reconnecting immediately.");
            }
            AutoReconnectionHandler.logger.debug("Attempting reconnection");
            try {
                myManager.connect();
            } catch (final IllegalStateException e) {
                AutoReconnectionHandler.logger.info("Websocket refused reconnection attempt.", e);
            }
        });
        this.restartWaitThread.setDaemon(false);
        this.restartWaitThread.start();
    }
}
