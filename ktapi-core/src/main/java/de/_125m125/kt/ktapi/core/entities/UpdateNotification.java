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
}
