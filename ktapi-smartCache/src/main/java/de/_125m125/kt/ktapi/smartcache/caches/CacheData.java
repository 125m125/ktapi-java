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
package de._125m125.kt.ktapi.smartcache.caches;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.GrowthList;

import de._125m125.kt.ktapi.smartcache.objects.TimestampedList;

public abstract class CacheData<T> {
    private final Clock         clock;
    private final Class<T>      clazz;

    private final GrowthList<T> entries;
    private long                lastInvalidationTime;
    private boolean             fetchedLast = false;
    private TimestampedList<T>  allEntries;

    public CacheData(final Class<T> clazz) {
        this(clazz, Clock.systemDefaultZone());
    }

    /* package */ CacheData(final Class<T> clazz, final Clock clock) {
        this.clock = clock;
        this.clazz = clazz;
        this.entries = new GrowthList<>();
        updateInvalidationTime();
    }

    /**
     * Sets the cached entries starting from the given start index.
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
    public synchronized TimestampedList<T> set(final List<T> newEntries, final int start) {
        return set(newEntries, start, false);
    }

    /**
     * Sets the cached entries starting from the given start index.
     *
     * @param newEntries
     *            the entries to insert
     * @param start
     *            the index where the first entry should be inserted
     * @param containsLast
     *            true, if the last entry in the list is the last entry on the server
     * @return a TimestampedList containing the provided entries
     * @throws IllegalArgumentException
     *             if the size of defined range does not equal the size of supplied list
     * @throws IllegalArgumentException
     *             if the start index is negative
     */
    public synchronized TimestampedList<T> set(final List<T> newEntries, final int start,
            final boolean containsLast) {
        for (int i = newEntries.size() - 1; i >= 0; i--) {
            this.entries.set(i + start, newEntries.get(i));
        }
        this.fetchedLast |= containsLast;
        this.allEntries = null;
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
    public synchronized TimestampedList<T> add(final List<T> newEntries, final int index) {
        updateInvalidationTime();
        this.entries.addAll(index, newEntries);
        this.allEntries = null;
        return new TimestampedList<>(newEntries, this.lastInvalidationTime, false);
    }

    public Collection<T> replace(final List<T> replacements, final Function<T, Object> keyMapper) {
        final Map<Object, T> entries = new LinkedHashMap<>(replacements.size() * 2);
        for (final T t : replacements) {
            entries.put(keyMapper.apply(t), t);
        }
        return replaceMatches(entries, keyMapper);
    }

    private Collection<T> replaceMatches(final Map<Object, T> replacements,
            final Function<T, Object> keyMapper) {
        boolean change = false;
        synchronized (this) {
            for (int i = 0; i < this.entries.size() && !replacements.isEmpty(); i++) {
                final T old = this.entries.get(i);
                if (old == null) {
                    continue;
                }
                final Object key = keyMapper.apply(old);
                if (replacements.containsKey(key)) {
                    this.entries.set(i, replacements.remove(key));
                    change = true;
                }
            }
            if (change) {
                updateInvalidationTime();
            }
        }
        return replacements.values();
    }

    public void removeAll(final List<T> toRemove) {
        synchronized (this) {
            if (this.entries.removeAll(toRemove)) {
                updateInvalidationTime();
            }
        }
    }

    protected void removeMatches(final List<T> toRemove, final Function<T, Object> keyMapper) {
        final Set<Object> removeKeys = toRemove.stream().map(keyMapper::apply)
                .collect(Collectors.toSet());
        this.entries.removeIf(e -> removeKeys.contains(keyMapper.apply(e)));
    }

    /**
     * Incorporates the changed entries into this cache. If changedEntries is empty or null, all
     * entries will be reset.
     */
    public void invalidate(final T[] changedEntries) {
        if (changedEntries == null || changedEntries.length == 0) {
            synchronized (this) {
                updateInvalidationTime();
                this.fetchedLast = false;
                this.entries.clear();
            }
        } else {
            updateEntries(changedEntries);
        }
    }

    protected synchronized void updateInvalidationTime() {
        this.lastInvalidationTime = this.clock.millis();
        this.allEntries = null;
    }

    protected abstract void updateEntries(T[] changedEntries);

    public synchronized Optional<T> get(final int index) {
        if (index >= this.entries.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.entries.get(index));
    }

    public synchronized Optional<TimestampedList<T>> get(final int start, int end) {
        final List<T> result = new ArrayList<>(end - start);
        if (end > this.entries.size()) {
            if (!this.fetchedLast) {
                return Optional.empty();
            } else {
                end = this.entries.size();
            }
        }
        for (int i = start; i < end; i++) {
            final T entry = this.entries.get(i);
            if (entry == null) {
                return Optional.empty();
            }
            result.add(entry);
        }
        return Optional.of(new TimestampedList<>(result, this.lastInvalidationTime, true));
    }

    public synchronized Optional<TimestampedList<T>> getAll() {
        if (this.allEntries != null) {
            return Optional.of(this.allEntries);
        }
        if (this.entries.isEmpty() && !this.fetchedLast) {
            return Optional.empty();
        }
        final List<T> result = new ArrayList<>(this.entries);
        this.allEntries = new TimestampedList<>(result, this.lastInvalidationTime, true);
        return Optional.of(this.allEntries);
    }

    public synchronized Optional<T> getAny(final Predicate<T> predicate) {
        return this.entries.stream().filter(predicate).findAny();
    }

    public synchronized boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public synchronized long getLastInvalidationTime() {
        return this.lastInvalidationTime;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

}
