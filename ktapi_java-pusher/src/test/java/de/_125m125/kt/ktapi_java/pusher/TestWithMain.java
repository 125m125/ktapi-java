package de._125m125.kt.ktapi_java.pusher;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.simple.parsers.SpecializedJsonParser;

public class TestWithMain {
    public static void main(final String[] args) {
        final User user = new User("1", "1", "1");
        final KtNotificationManager pusherKt = new PusherKt(user, new SpecializedJsonParser<>(),
                "https://kt.125m125.de/api/");

        pusherKt.subscribeToMessages(notification -> System.out.println(notification), user, true);

        pusherKt.subscribeToPayouts(notification -> System.out.println(notification), user, true);

        pusherKt.subscribeToTrades(notification -> System.out.println(notification), user, true);

        pusherKt.subscribeToItems(notification -> System.out.println(notification), user, true);

        pusherKt.subscribeToHistory(notification -> System.out.println(notification));

        pusherKt.subscribeToOrderbook(notification -> System.out.println(notification));

        try {
            Thread.sleep(60000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
