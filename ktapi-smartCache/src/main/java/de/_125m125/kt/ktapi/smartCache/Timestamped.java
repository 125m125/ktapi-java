package de._125m125.kt.ktapi.smartCache;

public interface Timestamped {
    public long getTimestamp();

    public boolean wasCacheHit();
}
