package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class ReplaceOrInvalidateCacheData<T> extends CacheData<T> {

    private final Function<T, Object> keyMapper;

    /* package */ ReplaceOrInvalidateCacheData(final Class<T> clazz,
            final Function<T, Object> keyMapper, final Clock clock) {
        super(clazz, clock);
        this.keyMapper = keyMapper;
    }

    public ReplaceOrInvalidateCacheData(final Class<T> clazz, final Function<T, Object> keyMapper) {
        super(clazz);
        this.keyMapper = keyMapper;
    }

    @Override
    protected synchronized void updateEntries(final T[] changedEntries) {
        final Collection<T> unmatched = replace(Arrays.asList(changedEntries), this.keyMapper);
        if (!unmatched.isEmpty()) {
            super.invalidate(null);
        }
    }
}
