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
package de._125m125.kt.ktapi.smartcache.objects;

import java.time.LocalDate;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.smartcache.Timestamped;

public class TimestampedHistoryEntry extends HistoryEntry implements Timestamped {

    private final HistoryEntry entry;
    private final long         timestamp;
    private final boolean      cacheHit;

    public TimestampedHistoryEntry(final HistoryEntry entry, final long timestamp,
            final boolean cacheHit) {
        super();
        this.entry = entry;
        this.timestamp = timestamp;
        this.cacheHit = cacheHit;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean wasCacheHit() {
        return this.cacheHit;
    }

    @Override
    public String getDatestring() {
        return this.entry.getDatestring();
    }

    @Override
    public LocalDate getDate() {
        return this.entry.getDate();
    }

    @Override
    public double getOpen() {
        return this.entry.getOpen();
    }

    @Override
    public double getClose() {
        return this.entry.getClose();
    }

    @Override
    public Double getLow() {
        return this.entry.getLow();
    }

    @Override
    public Double getHigh() {
        return this.entry.getHigh();
    }

    @Override
    public int getUnitVolume() {
        return this.entry.getUnitVolume();
    }

    @Override
    public double getDollarVolume() {
        return this.entry.getDollarVolume();
    }

    @Override
    public String toString() {
        return this.entry.toString();
    }

    @Override
    public int hashCode() {
        return this.entry.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TimestampedHistoryEntry) {
            return this.entry.equals(((TimestampedHistoryEntry) obj).entry);
        }
        return this.entry.equals(obj);
    }

}
