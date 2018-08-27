package de._125m125.kt.ktapi_java.smartCache.objects;

import java.util.AbstractList;
import java.util.List;

import de._125m125.kt.ktapi_java.smartCache.Timestamped;

public class TimestampedList<T> extends AbstractList<T> implements Timestamped {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7176069598333249778L;
    private final long        timestamp;
    private final boolean     cacheHit;
    private final List<T>     messages;

    public TimestampedList(final List<T> messages, final long timestamp, final boolean cacheHit) {
        this.messages = messages;
        this.timestamp = timestamp;
        this.cacheHit = cacheHit;
    }

    public TimestampedList(final List<T> messages, final long timestamp) {
        this(messages, timestamp, false);
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public T get(final int index) {
        return this.messages.get(index);
    }

    @Override
    public int size() {
        return this.messages.size();
    }

    @Override
    public boolean wasCacheHit() {
        return this.cacheHit;
    }

}
