package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ReplaceOrPrependCacheData<T> extends PrependCacheData<T> {

    private final Function<T, Object> keyMapper;

    /* package */ ReplaceOrPrependCacheData(final Class<T> clazz,
            final Function<T, Object> keyMapper, final Clock clock) {
        super(clazz, clock);
        this.keyMapper = keyMapper;
    }

    public ReplaceOrPrependCacheData(final Class<T> clazz, final Function<T, Object> keyMapper) {
        super(clazz);
        this.keyMapper = keyMapper;
    }

    @Override
    protected synchronized void updateEntries(final T[] changedEntries) {
        updateEntries(Arrays.asList(changedEntries));
    }

    @Override
    protected synchronized void updateEntries(final List<T> changedEntries) {
        final Collection<T> remainingEntries = replace(changedEntries, this.keyMapper);
        super.updateEntries(new ArrayList<>(remainingEntries));
    }

}
