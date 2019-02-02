package de._125m125.kt.ktapi.okhttp.websocket;

import de._125m125.kt.ktapi.websocket.KtWebsocket;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.okhttp.helper.OkHttpClientBuilder;
import de._125m125.kt.okhttp.helper.modifier.CertificatePinnerAdder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class KtOkHttpWebsocket implements KtWebsocket {

    private final String              url;
    private final OkHttpClientBuilder clientBuilder;
    private final OkHttpClient        client;
    private WebSocket                 ws;
    private KtWebsocketManager        manager;

    /**
     * Creates a new OkHttpWebsocket with default url and default OkHttpClient.
     */
    public KtOkHttpWebsocket() {
        this(null, null);
    }

    /**
     * Creates a new OkHttpWebsocket with the specified target url and the default OkHttpClient.
     *
     * @param url
     *            the target url used when connection the websocket
     */
    public KtOkHttpWebsocket(final String url) {
        this(url, null);
    }

    /**
     * creates a new OkHttpWebsocket with the specified target url and a
     * provided client.
     *
     * @param url
     *            the target url used when connection the websocket
     * @param clientBuilder
     *            the client that is used for the websocket.
     */
    public KtOkHttpWebsocket(final String url, final OkHttpClientBuilder clientBuilder) {
        this.url = url != null ? url : KtWebsocket.DEFAULT_SERVER_ENDPOINT_URI;
        if (clientBuilder != null) {
            this.clientBuilder = clientBuilder;
        } else {
            this.clientBuilder = new OkHttpClientBuilder()
                    .addModifier(new CertificatePinnerAdder());
        }
        this.client = this.clientBuilder.build(this);
    }

    @Override
    public synchronized void close() {
        this.ws.close(1000, "client shutting down");
        this.clientBuilder.close(this);
    }

    @Override
    public synchronized boolean connect() {
        final Request request = new Request.Builder().url(this.url).build();
        final WebSocketListener listener = new KtWebsocketListener();
        this.ws = this.client.newWebSocket(request, listener);
        return true;
    }

    @Override
    public synchronized void sendMessage(final String message) {
        this.ws.send(message);
    }

    @Override
    public void setManager(final KtWebsocketManager manager) {
        this.manager = manager;
    }

    public class KtWebsocketListener extends WebSocketListener {

        @Override
        public void onOpen(final WebSocket webSocket, final Response response) {
            new Thread(KtOkHttpWebsocket.this.manager::websocketConnected).start();
        }

        @Override
        public void onMessage(final WebSocket webSocket, final String text) {
            KtOkHttpWebsocket.this.manager.receiveMessage(text);
        }

        @Override
        public void onClosed(final WebSocket webSocket, final int code, final String reason) {
            new Thread(KtOkHttpWebsocket.this.manager::websocketDisconnected).start();
        }

        @Override
        public void onClosing(final WebSocket webSocket, final int code, final String reason) {
            new Thread(KtOkHttpWebsocket.this.manager::websocketDisconnected).start();
        }

        @Override
        public void onFailure(final WebSocket webSocket, final Throwable t,
                final Response response) {
            t.printStackTrace();
            new Thread(KtOkHttpWebsocket.this.manager::websocketDisconnected).start();
        }

    }
}
