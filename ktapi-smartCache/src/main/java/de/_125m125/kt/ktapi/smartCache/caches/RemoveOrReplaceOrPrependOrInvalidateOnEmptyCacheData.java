package de._125m125.kt.ktapi.smartCache.caches;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RemoveOrReplaceOrPrependOrInvalidateOnEmptyCacheData<T>
        extends ReplaceOrPrependOrInvalidateOnEmptyCacheData<T> {

    private Predicate<T> removeCondition;

    /* package */ RemoveOrReplaceOrPrependOrInvalidateOnEmptyCacheData(final Class<T> clazz,
            final Function<T, Object> keyMapper, final Predicate<T> removeCondition,
            final Clock clock) {
        super(clazz, keyMapper, clock);
        this.removeCondition = removeCondition;
    }

    public RemoveOrReplaceOrPrependOrInvalidateOnEmptyCacheData(final Class<T> clazz,
            final Function<T, Object> keyMapper, final Predicate<T> removeCondition) {
        super(clazz, keyMapper);
        this.removeCondition = removeCondition;
    }

    @Override
    protected synchronized void updateEntries(final T[] changedEntries) {
        updateEntries(Arrays.asList(changedEntries));
    }

    @Override
    protected synchronized void updateEntries(final List<T> changedEntries) {
        Map<Boolean, List<T>> collect = changedEntries.stream()
                .collect(Collectors.partitioningBy(removeCondition));
        removeMatches(collect.get(true), keyMapper);
        if (!collect.get(false).isEmpty()) {
            super.updateEntries(collect.get(false));
        }
    }

}
