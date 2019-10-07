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
package de._125m125.kt.ktapi.websocket.javawebsocket;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import de._125m125.kt.ktapi.websocket.KtWebsocket;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;

public class KtJavaWebsocket implements KtWebsocket {

    private final URI                 endpointUri;
    private final Map<String, String> headers;

    private InnerWebsocketClient client;
    private KtWebsocketManager   manager;

    private boolean firstConnect = true;
    private boolean connecting   = false;

    public KtJavaWebsocket(String appName) {
        this(KtWebsocket.DEFAULT_SERVER_ENDPOINT_URI, appName);
    }

    public KtJavaWebsocket(String uri, String appName) {
        this(URI.create(uri), appName);
    }

    public KtJavaWebsocket(URI uri, String appName) {
        this.endpointUri = uri;
        this.headers = new HashMap<>();
        this.headers.put("user-agent", "KtApi-Java-JavaWebsocket-" + appName);
    }

    @Override
    public void close() {
        this.client.close(1000, "client shutting down");
    }

    @Override
    public boolean connect() {
        this.connecting = true;
        if (this.firstConnect) {
            this.client.connect();
            this.firstConnect = false;
        } else {
            this.client.reconnect();
        }
        return true;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        this.client.send(message);
    }

    private void destroyAndRecreateClient() {
        if (this.client.isOpen()) {
            this.client.close();
        }
        this.client = new InnerWebsocketClient();
    }

    @Override
    public void setManager(KtWebsocketManager manager) {
        this.manager = manager;
        this.client = new InnerWebsocketClient();
    }

    private class InnerWebsocketClient extends WebSocketClient {

        public InnerWebsocketClient() {
            super(KtJavaWebsocket.this.endpointUri, KtJavaWebsocket.this.headers);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            KtJavaWebsocket.this.connecting = false;
            new Thread(KtJavaWebsocket.this.manager::websocketConnected).start();
        }

        @Override
        public void onMessage(String message) {
            KtJavaWebsocket.this.manager.receiveMessage(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            new Thread(KtJavaWebsocket.this.manager::websocketDisconnected).start();
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
            // while connecting, JavaWebsocket wont send close events, so we have to manually inform
            // listeners of failed connection attempts.
            if (KtJavaWebsocket.this.connecting) {
                // JavaWebsocket is sometimes unable to reconnect, so we have to destroy the client
                // and instantiate a new one.
                destroyAndRecreateClient();
                KtJavaWebsocket.this.connecting = false;
                KtJavaWebsocket.this.firstConnect = true;
                new Thread(KtJavaWebsocket.this.manager::websocketDisconnected).start();
            }
        }

    }
}
