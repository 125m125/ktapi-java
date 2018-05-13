package de._125m125.kt.ktapi_java.websocket;

import java.io.IOException;

public interface KtWebsocket {
    /** The URL endpoint for Kadcontrade websockets. */
    public static final String DEFAULT_SERVER_ENDPOINT_URI = "wss://kt.125m125.de/api/websocket";

    /**
     * Closes the websocket connection.
     */
    public void close();

    /**
     * creates a new websocket connection.
     *
     * @return true, if the success or failure is determined by events
     *         ({@link #onOpen()} or {@link #onClose(boolean)}, false if the
     *         connection attempt failed and a reconnection attempt should be
     *         started without waiting for events
     */
    public boolean connect();

    /**
     * Send message.
     *
     * @param message
     *            the message
     */
    public void sendMessage(final String message) throws IOException;

    public void setManager(final KtWebsocketManager manager);

}
