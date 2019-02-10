package de._125m125.kt.ktapi.core;

import java.util.concurrent.CompletableFuture;

import de._125m125.kt.ktapi.core.users.UserKey;

/**
 * The Interface KtNotificationManager.
 */
public interface KtNotificationManager<U> {

    /**
     * The Priority of subscriptions indicates the order, in which listeners should be invoked.
     * Listeners with highest priority are invoked first, listeners with lowest priority last. There
     * are no guarantees on the order in which listeners with the same priority are notified.
     */
    public static enum Priority {
    HIGHEST,
    HIGH,
    NORMAL,
    LOW,
    LOWEST,
    }

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
    public default CompletableFuture<U> subscribeToMessages(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated) {
        return subscribeToMessages(listener, userKey, selfCreated, Priority.NORMAL);
    }

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
     * @param priority
     *            the priority
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToMessages(NotificationListener listener, UserKey userKey,
            boolean selfCreated, Priority priority);

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
    public default CompletableFuture<U> subscribeToTrades(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated) {
        return subscribeToTrades(listener, userKey, selfCreated, Priority.NORMAL);
    }

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
     * @param priority
     *            the priority
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToTrades(NotificationListener listener, UserKey userKey,
            boolean selfCreated, Priority priority);

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
    public default CompletableFuture<U> subscribeToItems(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated) {
        return subscribeToItems(listener, userKey, selfCreated, Priority.NORMAL);
    }

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
     * @param priority
     *            the priority
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToItems(NotificationListener listener, UserKey userKey,
            boolean selfCreated, Priority priority);

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
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public default CompletableFuture<U> subscribeToPayouts(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated) {
        return subscribeToPayouts(listener, userKey, selfCreated, Priority.NORMAL);
    }

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
     * @param priority
     *            the priority
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToPayouts(NotificationListener listener, UserKey userKey,
            boolean selfCreated, Priority priority);

    /**
     * Subscribe to updates for orderbooks.
     *
     * @param listener
     *            the listener
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public default CompletableFuture<U> subscribeToOrderbook(final NotificationListener listener) {
        return subscribeToOrderbook(listener, Priority.NORMAL);
    }

    /**
     * Subscribe to updates for orderbooks.
     *
     * @param listener
     *            the listener
     * @param priority
     *            the priority
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToOrderbook(NotificationListener listener,
            Priority priority);

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
    public default CompletableFuture<U> subscribeToHistory(final NotificationListener listener) {
        return subscribeToHistory(listener, Priority.NORMAL);
    }

    /**
     * Subscribe to updates for historic values.
     *
     * @param listener
     *            the listener
     * @param priority
     *            the priority
     * @return a CompletableFuture that will receive the subscribing listener when the subscription
     *         was successful. If the subscription fails, the cause will be forwarded to the future.
     *         That listener can later be used to unsubscribe.
     */
    public CompletableFuture<U> subscribeToHistory(final NotificationListener listener,
            Priority priority);

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
            final UserKey userKey, final boolean selfCreated) {
        return subscribeToAll(listener, userKey, selfCreated, Priority.NORMAL);
    }

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
     * @param priority
     *            the priority
     * @return an array of CompletableFutures, where each entry is the result of one subscription.
     *         Index 0: items, index 1: messages, index 2: payouts, index 3: orders. All futures
     *         will return the same listener.
     */
    public default CompletableFuture<U>[] subscribeToAll(final NotificationListener listener,
            final UserKey userKey, final boolean selfCreated, final Priority priority) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<U>[] results = new CompletableFuture[4];
        results[0] = subscribeToItems(listener, userKey, selfCreated, priority);
        results[1] = subscribeToMessages(listener, userKey, selfCreated, priority);
        results[2] = subscribeToPayouts(listener, userKey, selfCreated, priority);
        results[3] = subscribeToTrades(listener, userKey, selfCreated, priority);
        return results;
    }

    /**
     * Subscribe to all notification that do not require a logged in user.
     *
     * @param listener
     *            the listener
     * @return an array of CompletableFutures, where each entry is the result of one subscription.
     *         Index 0: history, index 1: orderbook. All futures will return the same listener.
     */
    public default CompletableFuture<U>[] subscribeToAll(final NotificationListener listener) {
        return subscribeToAll(listener, Priority.NORMAL);
    }

    /**
     * Subscribe to all notification that do not require a logged in user.
     *
     * @param listener
     *            the listener
     * @param priority
     *            the priority
     * @return an array of CompletableFutures, where each entry is the result of one subscription.
     *         Index 0: history, index 1: orderbook. All futures will return the same listener.
     */
    public default CompletableFuture<U>[] subscribeToAll(final NotificationListener listener,
            final Priority priority) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<U>[] results = new CompletableFuture[4];
        results[0] = subscribeToHistory(listener, priority);
        results[1] = subscribeToOrderbook(listener, priority);
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