package de._125m125.kt.ktapi_java.core;

import de._125m125.kt.ktapi_java.core.objects.User;

public interface KtNotificationManager {

    void subscribeToMessages(NotificationListener listener, User user, boolean selfCreated);

    void subscribeToTrades(NotificationListener listener, User user, boolean selfCreated);

    void subscribeToItems(NotificationListener listener, User user, boolean selfCreated);

    void subscribeToPayouts(NotificationListener listener, User user, boolean selfCreated);

    void subscribeToOrderbook(NotificationListener listener);

    void subscribeToHistory(NotificationListener listener);

    void subscribeToAll(NotificationListener listener, User u, boolean selfCreated);

    void subscribeToUpdates(NotificationListener listener, User u, String path, boolean selfCreated);

}