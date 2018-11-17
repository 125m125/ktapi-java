package de._125m125.kt.ktapi.core;

import java.util.concurrent.CompletableFuture;

import de._125m125.kt.ktapi.core.users.UserKey;

/**
 * The Interface KtNotificationManager.
 */
public interface KtNotificationManager<T extends UserKey<?>, U> {

    /**
     * Subscribe to updates for messages.
     *
     * @param listener
     *            the listener
     * @param userKey
     *            the user key
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToMessages(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for trades of the key for the user.
     *
     * @param listener
     *            the listener
     * @param userKey
     *            the key for the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToTrades(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for the itemlist.
     *
     * @param listener
     *            the listener
     * @param userKey
     *            the key for the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToItems(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for payouts.
     *
     * @param listener
     *            the listener
     * @param userKey
     *            the key for the user
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     */
    public CompletableFuture<U> subscribeToPayouts(NotificationListener listener, T userKey,
            boolean selfCreated);

    /**
     * Subscribe to updates for orderbooks.
     *
     * @param listener
     *            the listener
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToOrderbook(NotificationListener listener);

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
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToHistory(NotificationListener listener);

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
     * @param userKey
     *            the userKey
     * @param selfCreated
     *            true to listen only to self-created notifications, false to only listen to
     *            non-self-created notification
     * @return an array of CompletableFutures, where each entry is the result of one subscription.
     *         Index 0: items, index 1: messages, index 2: payouts, index 3: orders. All futures
     *         will return the same listener.
     */
    public default CompletableFuture<U>[] subscribeToAll(final NotificationListener listener,
            final T userKey, final boolean selfCreated) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<U>[] results = new CompletableFuture[4];
        results[0] = subscribeToItems(listener, userKey, selfCreated);
        results[1] = subscribeToMessages(listener, userKey, selfCreated);
        results[2] = subscribeToPayouts(listener, userKey, selfCreated);
        results[3] = subscribeToTrades(listener, userKey, selfCreated);
        return results;
    }

    /**
     * Subscribe to all notification that do not require a logged in user
     *
     * @param listener
     *            the listener
     * @return an array of CompletableFutures, where each entry is the result of one subscription.
     *         Index 0: history, index 1: orderbook. All futures will return the same listener.
     */
    public default CompletableFuture<U>[] subscribeToAll(final NotificationListener listener) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<U>[] results = new CompletableFuture[4];
        results[0] = subscribeToHistory(listener);
        results[1] = subscribeToOrderbook(listener);
        return results;
    }

    /**
     * Unsubscribe the NotificationListener from all updates it is currently subscribed to. The
     * underlying Notificationmanager may still receive event messages from the server, but will not
     * forward them to this listener anymore and won't prevent the garbage collector from collecting
     * it.
     * 
     * @param listener
     *            the listener that should be unsubscribed
     */
    public void unsubscribe(U listener);

    public default void unsubscribe(final CompletableFuture<U> listener) {
        listener.thenAccept(this::unsubscribe);
    }

    /**
     * Disconnects this NotificationManager from their remote event source. All listeners will stop
     * receiving events and no new listeners can be registered after this method returns.
     */
    public void disconnect();

}