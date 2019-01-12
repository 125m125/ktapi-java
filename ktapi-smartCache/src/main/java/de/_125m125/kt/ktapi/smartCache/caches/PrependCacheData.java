package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

public class PrependCacheData<T> extends CacheData<T> {

    public PrependCacheData(final Class<T> clazz, final Clock clock) {
        super(clazz, clock);
    }

    public PrependCacheData(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void updateEntries(final T[] changedEntries) {
        updateEntries(Arrays.asList(changedEntries));
    }

    protected void updateEntries(final List<T> changedEntries) {
        add(changedEntries, 0);
    }
}
