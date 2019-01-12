package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.collections4.list.GrowthList;

import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;

public abstract class CacheData<T> {
    private final Clock         clock;

    private final GrowthList<T> entries;
    private long                lastInvalidationTime;
    private TimestampedList<T>  allEntries;

    private final Class<T>      clazz;

    public CacheData(final Class<T> clazz) {
        this(clazz, Clock.systemDefaultZone());
    }

    /* package */ CacheData(final Class<T> clazz, final Clock clock) {
        this.clock = clock;
        this.clazz = clazz;
        this.entries = new GrowthList<>();
        this.lastInvalidationTime = this.clock.millis();
    }

    /**
     * sets the cached entries starting from the given start index
     * 
     * @param newEntries
     *            the entries to insert
     * @param start
     *            the index where the first entry should be inserted
     * @return a TimestampedList containing the provided entries
     * @throws IllegalArgumentException
     *             if the size of defined range does not equal the size of supplied list
     * @throws IllegalArgumentException
     *             if the start index is negative
     */
    public TimestampedList<T> set(final List<T> newEntries, final int start) {
        synchronized (this.entries) {
            for (int i = newEntries.size() - 1; i >= 0; i--) {
                this.entries.set(i + start, newEntries.get(i));
            }
            this.allEntries = null;
        }
        return new TimestampedList<>(newEntries, this.lastInvalidationTime, false);
    }

    /**
     * adds the cached entries at the given index, pushing the remaining entries backwards.
     * 
     * @param newEntries
     *            the entries to add
     * @param start
     *            the index where the first entry should be inserted
     * @return a TimestampedList containing the provided entries
     * @throws IllegalArgumentException
     *             if the size of defined range does not equal the size of supplied list
     * @throws IllegalArgumentException
     *             if the start index is negative
     */
    public TimestampedList<T> add(final List<T> newEntries, final int index) {
        synchronized (this.entries) {
            this.lastInvalidationTime = this.clock.millis();
            this.entries.addAll(index, newEntries);
            this.allEntries = null;
        }
        return new TimestampedList<>(newEntries, this.lastInvalidationTime, false);
    }

    /**
     * Incorporates the changed entries into this cache. If changedEntries is empty or null, all
     * entries will be reset.
     */
    public void invalidate(final T[] changedEntries) {
        if (changedEntries == null || changedEntries.length == 0) {
            synchronized (this.entries) {
                this.lastInvalidationTime = this.clock.millis();
                this.entries.clear();
                this.allEntries = null;
            }
        } else {
            updateEntries(changedEntries);
        }
    }

    protected abstract void updateEntries(T[] changedEntries);

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

    public Class<T> getClazz() {
        return this.clazz;
    }

    protected GrowthList<T> getGrowthList() {
        return this.entries;
    }
}
