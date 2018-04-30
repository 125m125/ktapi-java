package de._125m125.kt.ktapi_java.core;

import de._125m125.kt.ktapi_java.core.entities.User;

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
    public void subscribeToMessages(NotificationListener listener, User user, boolean selfCreated);

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
    public void subscribeToTrades(NotificationListener listener, User user, boolean selfCreated);

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
    public void subscribeToItems(NotificationListener listener, User user, boolean selfCreated);

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
    public void subscribeToPayouts(NotificationListener listener, User user, boolean selfCreated);

    /**
     * Subscribe to updates for orderbooks.
     *
     * @param listener
     *            the listener
     */
    public void subscribeToOrderbook(NotificationListener listener);

    /**
     * Subscribe to updates for historic values.
     *
     * @param listener
     *            the listener
     */
    public void subscribeToHistory(NotificationListener listener);

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
    public default void subscribeToAll(final NotificationListener listener, final User user,
            final boolean selfCreated) {
        subscribeToItems(listener, user, selfCreated);
        subscribeToMessages(listener, user, selfCreated);
        subscribeToPayouts(listener, user, selfCreated);
        subscribeToTrades(listener, user, selfCreated);
    }

    public default void subscribeToAll(final NotificationListener listener) {
        subscribeToHistory(listener);
        subscribeToOrderbook(listener);
    }

    public void disconnect();

}