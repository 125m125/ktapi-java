package de._125m125.kt.ktapi.smartCache;

import java.util.Map;

import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.smartCache.caches.CacheData;

public class InvalidationCallback<T> implements Callback<T> {

    private final Map<String, CacheData<?>> cache;
    private final String                    key;

    public InvalidationCallback(final Map<String, CacheData<?>> cache, final String key) {
        this.cache = cache;
        this.key = key;
    }

    @Override
    public void onSuccess(final int status, final T result) {
        invalidate(result);
    }

    @Override
    public void onFailure(final int status, final String message,
            final String humanReadableMessage) {
        if (status > 500) {
            // data could have changed
            invalidate(null);
        }
    }

    @Override
    public void onError(final Throwable t) {
        // data could have changed
        invalidate(null);
    }

    @SuppressWarnings("unchecked")
    private void invalidate(final T result) {
        final CacheData<T> cacheData = (CacheData<T>) this.cache.get(this.key);
        if (cacheData != null) {
            cacheData.invalidate((T[]) new Object[] { result });
        }
    }

}
