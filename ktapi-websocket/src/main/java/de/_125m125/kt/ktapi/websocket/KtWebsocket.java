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
package de._125m125.kt.ktapi.websocket;

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
     *         ({@link KtWebsocketManager#websocketConnected()} or
     *         {@link KtWebsocketManager#websocketDisconnected()}, false if the connection attempt
     *         failed and a reconnection attempt should be started without waiting for events
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
