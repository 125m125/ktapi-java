package de._125m125.kt.ktapi_java.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.NotificationListener;
import de._125m125.kt.ktapi_java.core.entities.User;

@ClientEndpoint
public class KtWebsocket implements KtNotificationManager {
    public static final String                                SERVER_ENDPOINT_URI = "wss://kt.125m125.de/websocket";

    private final Map<String, List<NotificationListener>>     listeners           = new HashMap<>();
    private final Map<Integer, Consumer<Map<String, Object>>> waiting             = new HashMap<>();
    private boolean                                           active              = true;
    private final AtomicInteger                               lastRequestId       = new AtomicInteger();
    private Session                                           session;
    private Thread                                            restart_wait_thread;
    private final URI                                         serverEndpointUri;

    public KtWebsocket() {
        try {
            this.serverEndpointUri = new URI(KtWebsocket.SERVER_ENDPOINT_URI);
        } catch (final URISyntaxException e) {
            throw new RuntimeException("The default websocket uri is invalid", e);
        }
    }

    public KtWebsocket(final String uri) throws URISyntaxException {
        this.serverEndpointUri = new URI(uri);
    }

    public synchronized void stop() {
        this.active = false;
        if (this.session != null) {
            try {
                this.session.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            this.session = null;
        }
        this.restart_wait_thread.interrupt();
    }

    public synchronized void start() {
        if (this.active) {
            return;
        }
        this.active = true;
        reconnect(500);
    }

    private synchronized void reconnect(final long previousDelay) {
        if (!this.active) {
            return;
        }
        final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, this.serverEndpointUri);
        } catch (final DeploymentException e) {
            throw new RuntimeException("KtWebsocket is not a valid ClientEnpoint", e);
        } catch (final IOException e) {
            reConnectDelayed(previousDelay * 2);
        }
    }

    private synchronized void reConnectDelayed(final long delay) {
        if (this.restart_wait_thread.isAlive() && this.restart_wait_thread != Thread.currentThread()) {
            throw new IllegalStateException("this instance is already waiting for a reconnect");
        }
        this.restart_wait_thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            reconnect(delay);
        });
        this.restart_wait_thread.setDaemon(true);
        this.restart_wait_thread.start();
    }

    @Override
    public void subscribeToMessages(final NotificationListener listener, final User user, final boolean selfCreated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToTrades(final NotificationListener listener, final User user, final boolean selfCreated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToItems(final NotificationListener listener, final User user, final boolean selfCreated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToPayouts(final NotificationListener listener, final User user, final boolean selfCreated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToOrderbook(final NotificationListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToHistory(final NotificationListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToAll(final NotificationListener listener, final User u, final boolean selfCreated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToAll(final NotificationListener ktCachingRequesterIml, final boolean selfCreated) {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

    @OnClose
    public synchronized void onClose(final Session session, final CloseReason closeReason) {
        if (session != this.session) {
            return;
        }
        this.session = null;
        if (this.active && closeReason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
            reConnectDelayed(1000L);
        }
    }

    @OnError
    public void onError(final Session session, final Throwable t) {
        t.printStackTrace();
    }

    @OnOpen
    public void onOpen(final Session session) {
        if (this.session != null && session != this.session) {
            try {
                session.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        this.session = session;
        // TODO reregister listeners
    }

    @OnMessage
    public void onMessage(final String message, final Session session) {
        // TODO Auto-generated method stub
    }

    public void sendMessage(final String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (final IOException ex) {

        }
    }

    public void send(final Object data, final Consumer<Map<String, Object>> callback) {

    }

}
