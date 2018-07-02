package de._125m125.kt.ktapi_java.websocket.okhttp;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.KtUserStore;
import de._125m125.kt.ktapi_java.core.entities.User;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi_java.websocket.events.listeners.AutoReconnectionHandler;
import de._125m125.kt.ktapi_java.websocket.events.listeners.KtWebsocketNotificationHandler;
import de._125m125.kt.ktapi_java.websocket.events.listeners.OfflineMessageHandler;
import de._125m125.kt.ktapi_java.websocket.events.listeners.SessionHandler;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final User user = new User("1", "1", "1");
        final KtOkHttpWebsocket ws = new KtOkHttpWebsocket("ws://localhost:8080/api/websocket");
        final KtNotificationManager<UserKey> manager = new KtWebsocketNotificationHandler<>(new KtUserStore<>(user));
        KtWebsocketManager.builder(ws).addDefaultParsers().addListener(new OfflineMessageHandler())
                .addListener(new SessionHandler()).addListener(manager).addListener(new AutoReconnectionHandler())
                .buildAndOpen();

        Thread.sleep(100);
        System.out.println("subscribing...");
        manager.subscribeToAll(System.out::println);
        manager.subscribeToAll(System.out::println, user, true);

        Thread.sleep(600_000);
        System.out.println("closing");
        manager.disconnect();
    }
}
