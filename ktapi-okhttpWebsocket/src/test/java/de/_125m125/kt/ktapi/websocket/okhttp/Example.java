package de._125m125.kt.ktapi.websocket.okhttp;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.listeners.AutoReconnectionHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.KtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.OfflineMessageHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.SessionHandler;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final TokenUser user = new TokenUser("1d0tat5", "3tdsgkl", "38r9l9i94qpdl");
        final KtOkHttpWebsocket ws = new KtOkHttpWebsocket("wss://kt.125m125.de/api/websocket");
        final KtNotificationManager<TokenUserKey> manager = new KtWebsocketNotificationHandler<>(
                new KtUserStore(user));
        KtWebsocketManager.builder(ws).addDefaultParsers().addListener(new OfflineMessageHandler())
                .addListener(new SessionHandler()).addListener(manager)
                .addListener(new AutoReconnectionHandler()).buildAndOpen();

        Thread.sleep(100);
        System.out.println("subscribing...");
        manager.subscribeToItems(System.out::println, user.getKey(), false)
                .whenComplete((listener, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Subscription failed: " + throwable.getMessage());
                    } else {
                        System.out.println("Subscription succeeded");
                    }
                });
        manager.subscribeToAll(System.out::println);
        manager.subscribeToAll(System.out::println, user.getKey(), true);

        Thread.sleep(600_000);
        System.out.println("closing");
        manager.disconnect();
    }
}
