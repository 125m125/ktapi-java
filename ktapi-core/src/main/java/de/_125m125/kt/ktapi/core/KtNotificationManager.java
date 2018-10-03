package de._125m125.kt.ktapi.core;

import de._125m125.kt.ktapi.core.users.UserKey;

/**
 * The Interface KtNotificationManager.
 */
public interface KtNotificationManager<T extends UserKey<?>> {

    /**
     * Subscribe to updates for messages.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     */
    public NotificationListener subscribeToMessages(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for trades of the user.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     */
    public NotificationListener subscribeToTrades(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for the itemlist.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     */
    public NotificationListener subscribeToItems(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for payouts.
     *
     * @param listener
     *            the listener
     * @param user
     *            the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     */
    public NotificationListener subscribeToPayouts(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for orderbooks.
     *
     * @param listener
     *            the listener
     */
    public NotificationListener subscribeToOrderbook(NotificationListener listener);

    // /**
    // * Subscribe to updates for the orderbook of a specific item.
    // *
    // * @param listener
    // * the listener
    // * @param item
    // * the id of the item
    // */
    // public NotificationListener subscribeToOrderbook(NotificationListener listener, String
    // item);

    /**
     * Subscribe to updates for historic values.
     *
     * @param listener
     *            the listener
     */
    public NotificationListener subscribeToHistory(NotificationListener listener);

    // /**
    // * Subscribe to updates for historic values of a specific item.
    // *
    // * @param listener
    // * the listener
    // * @param item
    // * the id of the item
    // */
    // public NotificationListener subscribeToHistory(NotificationListener listener, String
    // item);

    /**
     * Subscribe to all notifications for a specific user.
     *
     * @param listener
     *            the listener
     * @param u
     *            the u
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     */
    public default NotificationListener subscribeToAll(final NotificationListener listener,
            final T userKey, final boolean selfCreated) {
        subscribeToItems(listener, userKey, selfCreated);
        subscribeToMessages(listener, userKey, selfCreated);
        subscribeToPayouts(listener, userKey, selfCreated);
        subscribeToTrades(listener, userKey, selfCreated);
        return listener;
    }

    /**
     * Subscribe to all notification that do not require a logged in user
     *
     * @param listener
     *            the listener
     */
    public default NotificationListener subscribeToAll(final NotificationListener listener) {
        subscribeToHistory(listener);
        subscribeToOrderbook(listener);
        return listener;
    }

    /**
     * Unsubscribe the NotificationListener from all updates it is surrently subscribed to. The
     * underlying Notificationmanager may still receive event messages from the server, but will not
     * forward them to this listener anymore and won't prevent the garbage collector from collecting
     * it.
     * 
     * @param listener
     */
    public void unsubscribe(NotificationListener listener);

    /**
     * Disconnects this NotificationManager from their remote event source. All listeners will stop
     * receiving events and no new listeners can be registered after this method returns.
     */
    public void disconnect();

}