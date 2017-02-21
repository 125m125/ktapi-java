package de._125m125.kt.ktapi_java.pusher;

import de._125m125.kt.ktapi_java.simple.objects.User;

public class TestWithMain {
    public static void main(final String[] args) {
        final PusherKt pusherKt = new PusherKt(new User("1", "1", "1"));

        pusherKt.subscribeToMessages(notification -> System.out.println(notification), true);

        pusherKt.subscribeToPayouts(notification -> System.out.println(notification), true);

        pusherKt.subscribeToTrades(notification -> System.out.println(notification), true);

        pusherKt.subscribeToItems(notification -> System.out.println(notification), true);

        pusherKt.subscribeToHistory(notification -> System.out.println(notification));

        pusherKt.subscribeToOrderbook(notification -> System.out.println(notification));

        try {
            Thread.sleep(60000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
