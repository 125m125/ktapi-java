package de._125m125.kt.ktapi_java.core.results;

import de._125m125.kt.ktapi_java.core.results.Result.ResultFetchException;

/* package */ class CallbackResult<T> {

    /* package */ CallbackResult(final Result<T> r, final Callback<T> c) {
        new Thread(() -> {
            try {
                if (r.isSuccessful()) {
                    c.onSuccess(r.getStatus(), r.getContent());
                } else {
                    c.onFailure(r.getStatus(), r.getErrorMessage(), r.getHumanReadableErrorMessage());
                }
            } catch (final Exception e) {
                if (e instanceof ResultFetchException) {
                    c.onError(e.getCause());
                } else {
                    c.onError(e);
                }
            }
        }).start();
    }
}
