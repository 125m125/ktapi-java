package de._125m125.kt.ktapi.smartcache;

import de._125m125.kt.ktapi.core.results.Result;

public class ImmediateResult<T> extends Result<T> {
    public ImmediateResult(final int status, final T result) {
        super();
        super.setSuccessResult(status, result);
    }
}
