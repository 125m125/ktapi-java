package de._125m125.kt.ktapi.smartcache;

import de._125m125.kt.ktapi.core.results.Callback;

public class InvalidationCallback<T> implements Callback<T> {

    private final String                key;
    private final KtSmartCache requester;

    public InvalidationCallback(final KtSmartCache requester, final String key) {
        this.requester = requester;
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
        this.requester.<T>invalidate(this.key, (T[]) new Object[] { result });
    }

}
