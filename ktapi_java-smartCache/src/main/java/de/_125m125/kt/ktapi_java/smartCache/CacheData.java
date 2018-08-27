package de._125m125.kt.ktapi_java.smartCache;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.collections4.list.GrowthList;

import de._125m125.kt.ktapi_java.smartCache.objects.TimestampedList;

public class CacheData<T> {
    private final Clock         clock;

    private final GrowthList<T> entries;
    private long                lastInvalidationTime;
    private TimestampedList<T>  allEntries;

    public CacheData() {
        this(Clock.systemDefaultZone());
    }

    /* package */ CacheData(final Clock clock) {
        this.clock = clock;
        this.entries = new GrowthList<>();
        this.lastInvalidationTime = this.clock.millis();
    }

    /**
     * sets the cached entries between start(inclusive) and end(exclusive)
     * 
     * @param newEntries
     *            the entries to insert
     * @param start
     *            the index where the first entry should be inserted
     * @param end
     *            the index behind the last entry to insert
     * @return a TimestampedList containing the provided entries
     * @throws IllegalArgumentException
     *             if the size of defined range does not equal the size of
     *             supplied list
     * @throws IllegalArgumentException
     *             if the start index is negative
     */
    public TimestampedList<T> set(final List<T> newEntries, final int start, final int end) {
        final int size = end - start;
        if (newEntries.size() != size) {
            throw new IllegalArgumentException("Size of defined range does not equal size of supplied list");
        }
        synchronized (this.entries) {
            for (int i = size - 1; i >= 0; i--) {
                this.entries.set(i + start, newEntries.get(i));
            }
            this.allEntries = null;
        }
        return new TimestampedList<>(newEntries, this.lastInvalidationTime, false);
    }

    /**
     * invalidates all cached data
     */
    public void invalidate() {
        synchronized (this.entries) {
            this.entries.clear();
            this.lastInvalidationTime = this.clock.millis();
            this.allEntries = null;
        }
    }

    public Optional<TimestampedList<T>> get(final int start, final int end) {
        final int size = end - start;
        final List<T> result = new ArrayList<>(size);
        synchronized (this.entries) {
            if (end > this.entries.size()) {
                return Optional.empty();
            }
            for (int i = start; i < end; i++) {
                final T entry = this.entries.get(i);
                if (entry == null) {
                    return Optional.empty();
                }
                result.add(entry);
            }
        }
        return Optional.of(new TimestampedList<>(result, this.lastInvalidationTime, true));
    }

    public Optional<TimestampedList<T>> getAll() {
        synchronized (this.entries) {
            if (this.entries.isEmpty()) {
                return Optional.empty();
            }
            if (this.allEntries != null) {
                return Optional.of(this.allEntries);
            }
            final List<T> result = new ArrayList<>(this.entries);
            this.allEntries = new TimestampedList<>(result, this.lastInvalidationTime, true);
            return Optional.of(this.allEntries);
        }
    }

    public Optional<T> get(final int index) {
        synchronized (this.entries) {
            if (index >= this.entries.size()) {
                return Optional.empty();
            }
            return Optional.ofNullable(this.entries.get(index));
        }
    }

    public Optional<T> getAny(final Predicate<T> predicate) {
        synchronized (this.entries) {
            return this.entries.stream().filter(predicate).findAny();
        }
    }

    public long getLastInvalidationTime() {
        synchronized (this.entries) {
            return this.lastInvalidationTime;
        }
    }
}
