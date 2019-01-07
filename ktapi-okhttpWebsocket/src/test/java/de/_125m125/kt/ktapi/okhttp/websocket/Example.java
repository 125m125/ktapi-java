package de._125m125.kt.ktapi.okhttp.websocket;

import java.io.File;

import de._125m125.kt.ktapi.core.users.CertificateUser;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.User;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.AutoReconnectionHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.KtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.OfflineMessageHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.SessionHandler;
import de._125m125.kt.okhttp.helper.OkHttpClientBuilder;
import de._125m125.kt.okhttp.helper.modifier.ClientCertificateAdder;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final OkHttpClientBuilder builder = new OkHttpClientBuilder();
        final ClientModifier createUnchecked = ClientCertificateAdder.createUnchecked(
                new File("../../certificateHelper/certificate2.p12"), "a".toCharArray());
        builder.addModifier(createUnchecked);

        final User user = new CertificateUser("1d0tat5", "../../certificateHelper/certificate2.p12",
                "a".toCharArray());
        final KtOkHttpWebsocket ws = new KtOkHttpWebsocket("wss://kt.125m125.de/api/websocket",
                builder);
        final AbstractKtWebsocketNotificationHandler<?> manager = new KtWebsocketNotificationHandler(
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