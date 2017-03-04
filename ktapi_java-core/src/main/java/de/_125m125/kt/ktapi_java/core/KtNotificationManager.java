package de._125m125.kt.ktapi_java.core;

import de._125m125.kt.ktapi_java.core.objects.User;

/**
 * The Interface KtNotificationManager.
 */
public interface KtNotificationManager {

    /**
     * Subscribe to updates for messages.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen to self created notifications
     */
    void subscribeToMessages(NotificationListener listener, User user, boolean selfCreated);

    /**
     * Subscribe to updates for trades of the user.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen to self created notifications
     */
    void subscribeToTrades(NotificationListener listener, User user, boolean selfCreated);

    /**
     * Subscribe to updates for the itemlist.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen to self created notifications
     */
    void subscribeToItems(NotificationListener listener, User user, boolean selfCreated);

    /**
     * Subscribe to updates for payouts.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen to self created notifications
     */
    void subscribeToPayouts(NotificationListener listener, User user, boolean selfCreated);

    /**
     * Subscribe to updates for orderbooks.
     *
     * @param listener
     *            the listener
     */
    void subscribeToOrderbook(NotificationListener listener);

    /**
     * Subscribe to updates for historic values.
     *
     * @param listener
     *            the listener
     */
    void subscribeToHistory(NotificationListener listener);

    /**
     * Subscribe to all notifications.
     *
     * @param listener
     *            the listener
     * @param u
     *            the u
     * @param selfCreated
     *            true to listen to self created notifications
     */
    void subscribeToAll(NotificationListener listener, User u, boolean selfCreated);

    /**
     * Subscribe to updates for all types.
     *
     * @param listener
     *            the listener
     * @param u
     *            the u
     * @param path
     *            the path
     * @param selfCreated
     *            true to listen to self created notifications
     */
    void subscribeToUpdates(NotificationListener listener, User u, String path, boolean selfCreated);

}