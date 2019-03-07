/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.smartcache;

import de._125m125.kt.ktapi.core.results.Callback;

public class InvalidationCallback<T> implements Callback<T> {

    private final String       key;
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
        if (status >= 500) {
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
        this.requester.<T>invalidate(this.key,
                result != null ? (T[]) new Object[] { result } : null);
    }

}
