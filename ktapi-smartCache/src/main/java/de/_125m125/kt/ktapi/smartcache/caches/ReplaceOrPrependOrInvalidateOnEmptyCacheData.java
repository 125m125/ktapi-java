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
