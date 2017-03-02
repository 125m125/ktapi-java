package de._125m125.kt.ktapi_java.cachingPusher.objects;

import java.util.AbstractList;
import java.util.List;

import de._125m125.kt.ktapi_java.cachingPusher.Timestamped;

public class TimestampedList<T> extends AbstractList<T> implements Timestamped {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7176069598333249778L;
    private final long        timestamp;
    private final List<T>     messages;

    public TimestampedList(final List<T> messages, final long timestamp) {
        this.messages = messages;
        this.timestamp = timestamp;
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

}
