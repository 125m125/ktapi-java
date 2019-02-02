package de._125m125.kt.ktapi.smartcache;

public interface Timestamped {
    public long getTimestamp();

    public boolean wasCacheHit();
}
