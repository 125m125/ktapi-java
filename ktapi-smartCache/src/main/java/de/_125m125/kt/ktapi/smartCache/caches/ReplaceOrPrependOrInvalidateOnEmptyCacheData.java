package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.list.GrowthList;

public class ReplaceOrPrependOrInvalidateOnEmptyCacheData<T> extends ReplaceOrPrependCacheData<T> {

    public ReplaceOrPrependOrInvalidateOnEmptyCacheData(final Class<T> clazz, final Function<T, Object> keyMapper,
            final Clock clock) {
        super(clazz, keyMapper, clock);
    }

    public ReplaceOrPrependOrInvalidateOnEmptyCacheData(final Class<T> clazz, final Function<T, Object> keyMapper) {
        super(clazz, keyMapper);
    }

    @Override
    protected void updateEntries(final T[] changedEntries) {
        final GrowthList<T> growthList = getGrowthList();
        synchronized (growthList) {
            if (growthList.isEmpty()) {
                invalidate(null);
                return;
            }
            super.updateEntries(changedEntries);
        }
    }

    @Override
    protected void updateEntries(final List<T> changedEntries) {
        final GrowthList<T> growthList = getGrowthList();
        synchronized (growthList) {
            if (growthList.isEmpty()) {
                invalidate(null);
                return;
            }
            super.updateEntries(changedEntries);
        }
    }

}
