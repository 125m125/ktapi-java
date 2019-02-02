package de._125m125.kt.ktapi.smartcache.caches;

import java.time.Clock;
import java.util.List;
import java.util.function.Function;

public class ReplaceOrPrependOrInvalidateOnEmptyCacheData<T> extends ReplaceOrPrependCacheData<T> {

    public ReplaceOrPrependOrInvalidateOnEmptyCacheData(final Class<T> clazz,
            final Function<T, Object> keyMapper, final Clock clock) {
        super(clazz, keyMapper, clock);
    }

    public ReplaceOrPrependOrInvalidateOnEmptyCacheData(final Class<T> clazz,
            final Function<T, Object> keyMapper) {
        super(clazz, keyMapper);
    }

    @Override
    protected synchronized void updateEntries(final T[] changedEntries) {
        if (isEmpty()) {
            invalidate(null);
            return;
        }
        super.updateEntries(changedEntries);
    }

    @Override
    protected synchronized void updateEntries(final List<T> changedEntries) {
        if (isEmpty()) {
            invalidate(null);
            return;
        }
        super.updateEntries(changedEntries);
    }

}
