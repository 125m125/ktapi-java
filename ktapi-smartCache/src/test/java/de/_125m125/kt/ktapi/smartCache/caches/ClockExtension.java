package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class ClockExtension extends Clock {
    private long current = 1000;

    @Override
    public ZoneId getZone() {
        return ZoneId.of("Z");
    }

    @Override
    public Clock withZone(final ZoneId zone) {
        throw new RuntimeException("This clock does not support Clock#whithZone()");
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(this.current);
    }

    public void progress() {
        this.current += 1000;
    }
}

