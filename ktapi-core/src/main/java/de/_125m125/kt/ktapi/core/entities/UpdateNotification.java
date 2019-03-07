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

import java.util.Arrays;
import java.util.Map;

public class UpdateNotification<T> extends Notification {

    protected T[] changedEntries;

    public UpdateNotification(final boolean selfCreated, final long uid, final String base32Uid,
            final Map<String, String> details) {
        this(selfCreated, uid, base32Uid, details, null);
    }

    public UpdateNotification(final boolean selfCreated, final long uid, final String base32Uid,
            final Map<String, String> details, final T[] changedEntries) {
        super(selfCreated, uid, base32Uid, "update", details);
        this.changedEntries = changedEntries == null ? null
                : Arrays.copyOf(changedEntries, changedEntries.length);
    }

    public String getSource() {
        return getDetails().get("source");
    }

    public String getKey() {
        return getDetails().get("key");
    }

    public String getChannel() {
        return getDetails().get("channel");
    }

    public boolean hasChangedEntries() {
        return this.changedEntries != null && this.changedEntries.length > 0;
    }

    public T[] getChangedEntries() {
        return Arrays.copyOf(this.changedEntries, this.changedEntries.length);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(this.changedEntries);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UpdateNotification<?> other = (UpdateNotification<?>) obj;
        if (!Arrays.equals(this.changedEntries, other.changedEntries)) {
            return false;
        }
        return true;
    }

}
