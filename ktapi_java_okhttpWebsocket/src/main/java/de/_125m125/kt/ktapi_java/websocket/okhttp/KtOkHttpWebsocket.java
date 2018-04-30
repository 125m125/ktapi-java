package de._125m125.kt.ktapi_java.websocket.okhttp;

import de._125m125.kt.ktapi_java.websocket.KtWebsocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class KtOkHttpWebsocket extends KtWebsocket {

    private final String       url;
    private final boolean      externalClient;
    private final OkHttpClient client;
    private WebSocket          ws;

    /**
     * creates a new OkHttpWebsocket
     */
    public KtOkHttpWebsocket() {
        this(null, null);
    }

    /**
     * creates a new OkHttpWebsocket with the specified target url
     * 
     * @param url
     *            the target url used when connection the websocket
     */
    public KtOkHttpWebsocket(final String url) {
        this(url, null);
    }

    /**
     * creates a new OkHttpWebsocket with the specified target url and a
     * provided client. <br>
     * The Client will not be closed when the websocket exits.
     * 
     * @param url
     *            the target url used when connection the websocket
     * @param client
     *            the client that is used for the websocket.
     */
    public KtOkHttpWebsocket(final String url, final OkHttpClient client) {
        this.url = url != null ? url : KtWebsocket.SERVER_ENDPOINT_URI;
        if (client != null) {
            this.client = client;
            this.externalClient = true;
        } else {
            this.client = new OkHttpClient();
            this.externalClient = false;
        }
        start();
    }

    @Override
    protected void close() {
        this.ws.close(1000, "client shutting down");
        if (!this.externalClient) {
            this.client.dispatcher().executorService().shutdown();
        }
    }

    @Override
    protected boolean connect() {
        final Request request = new Request.Builder().url(this.url).build();
        final WebSocketListener listener = new KtWebsocketListener(this);
        this.ws = this.client.newWebSocket(request, listener);
        return true;
    }

    @Override
    public void sendMessage(final String message) {
        System.out.println(message);
        this.ws.send(message);
    }

    public class KtWebsocketListener extends WebSocketListener {

        private final KtOkHttpWebsocket listener;

        public KtWebsocketListener(final KtOkHttpWebsocket listener) {
            this.listener = listener;
        }

        @Override
        public void onOpen(final WebSocket webSocket, final Response response) {
            this.listener.onOpen();
        }

        @Override
        public void onMessage(final WebSocket webSocket, final String text) {
            this.listener.onMessage(text);
        }

        @Override
        public void onClosed(final WebSocket webSocket, final int code, final String reason) {
            this.listener.onClose(true);
        }

        @Override
        public void onFailure(final WebSocket webSocket, final Throwable t, final Response response) {
            t.printStackTrace();
            this.listener.onClose(true);
        }

    }
}