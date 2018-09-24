package de._125m125.kt.ktapi.smartCache.objects;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;

public class TimestampedObjectFactory {
    @SuppressWarnings("unchecked")
    public <T> T create(final T source, final long timestamp, final boolean hit) {
        if (source instanceof HistoryEntry) {
            return (T) new TimestampedHistoryEntry((HistoryEntry) source, timestamp, hit);
        }
        return source;
    }
}
