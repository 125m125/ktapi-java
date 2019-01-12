package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.collections4.list.GrowthList;

public class ReplaceOrInvalidateCacheData<T> extends CacheData<T> {

    private final Function<T, Object> keyMapper;

    public ReplaceOrInvalidateCacheData(final Class<T> clazz, final Clock clock,
            final Function<T, Object> keyMapper) {
        super(clazz, clock);
        this.keyMapper = keyMapper;
    }

    public ReplaceOrInvalidateCacheData(final Class<T> clazz, final Function<T, Object> keyMapper) {
        super(clazz);
        this.keyMapper = keyMapper;
    }

    @Override
    protected void updateEntries(final T[] changedEntries) {
        final Map<Object, T> entries = new HashMap<>(changedEntries.length * 2);
        final GrowthList<T> growthList = getGrowthList();
        synchronized (growthList) {
            for (int i = 0; i < growthList.size() && !entries.isEmpty(); i++) {
                final T old = growthList.get(i);
                if (old == null) {
                    continue;
                }
                final Object key = this.keyMapper.apply(old);
                if (entries.containsKey(key)) {
                    growthList.set(i, entries.remove(key));
                }
            }
        }
        if (!entries.isEmpty()) {
            super.invalidate(null);
        }
    }
}
