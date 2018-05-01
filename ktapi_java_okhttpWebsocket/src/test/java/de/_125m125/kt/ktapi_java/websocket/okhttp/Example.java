package de._125m125.kt.ktapi_java.websocket.okhttp;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.entities.User;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final KtNotificationManager manager = new KtOkHttpWebsocket("ws://localhost:8080/api/websocket");
        Thread.sleep(100);
        manager.subscribeToAll(System.out::println);
        manager.subscribeToAll(System.out::println, new User("sovo5g", "a2h8et", "9gp108p9pe1km"), true);

        Thread.sleep(600_000);
        System.out.println("closing");
        manager.disconnect();
    }
}
