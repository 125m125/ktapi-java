package de._125m125.kt.ktapi_java.core.entities;

import java.util.Map;

/**
 * The Class Notification.
 */
public class Notification {

    /** True, if the notification was caused by the user himself. */
    private final boolean             selfCreated;

    /** The id of the subscribed user. */
    private final long                uid;

    /** The base 32 id of the subscribed user. */
    private final String              base32Uid;

    /** The type of this notification. */
    private final String              type;

    /** The details for this notification. */
    private final Map<String, String> details;

    /**
     * Instantiates a new notification.
     *
     * @param selfCreated
     *            true, if the notification was caused by the user himself
     * @param uid
     *            the id of the subscribed user
     * @param base32Uid
     *            the base 32 if of the subscribed user
     * @param type
     *            the type of the notification
     * @param details
     *            the details for this notification
     */
    public Notification(final boolean selfCreated, final long uid, final String base32Uid, final String type,
            final Map<String, String> details) {
        super();
        this.selfCreated = selfCreated;
        this.uid = uid;
        this.base32Uid = base32Uid;
        this.type = type;
        this.details = details;
    }

    /**
     * Checks if the notification was caused by the user.
     *
     * @return true, if the notification was caused by the subscribed user
     */
    public boolean isSelfCreated() {
        return this.selfCreated;
    }

    /**
     * Gets the if of the subscribed uer.
     * 
     * @return the id of the subscribed user
     */
    public long getUid() {
        return this.uid;
    }

    /**
     * Gets the base 32 id of the subscribed user.
     *
     * @return the base 32 id of the subscribed user
     */
    public String getBase32Uid() {
        return this.base32Uid;
    }

    /**
     * Gets the type of the notification.
     *
     * @return the type of the notification
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the details of the notification.
     *
     * @return the details of the notification
     */
    public Map<String, String> getDetails() {
        return this.details;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Notification [selfCreated=" + this.selfCreated + ", uid=" + this.uid + ", base32Uid=" + this.base32Uid
                + ", type=" + this.type + ", details=" + this.details + "]";
    }

}
