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
    public double getUnitVolume() {
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