/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.core.entities;

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
    public Notification(final boolean selfCreated, final long uid, final String base32Uid,
            final String type, final Map<String, String> details) {
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
        return "Notification [selfCreated=" + this.selfCreated + ", uid=" + this.uid
                + ", base32Uid=" + this.base32Uid + ", type=" + this.type + ", details="
                + this.details + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.base32Uid == null) ? 0 : this.base32Uid.hashCode());
        result = prime * result + ((this.details == null) ? 0 : this.details.hashCode());
        result = prime * result + (this.selfCreated ? 1231 : 1237);
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + (int) (this.uid ^ (this.uid >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (this.base32Uid == null) {
            if (other.base32Uid != null) {
                return false;
            }
        } else if (!this.base32Uid.equals(other.base32Uid)) {
            return false;
        }
        if (this.details == null) {
            if (other.details != null) {
                return false;
            }
        } else if (!this.details.equals(other.details)) {
            return false;
        }
        if (this.selfCreated != other.selfCreated) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        if (this.uid != other.uid) {
            return false;
        }
        return true;
    }

}
