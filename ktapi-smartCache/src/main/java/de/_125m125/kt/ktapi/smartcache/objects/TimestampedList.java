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
package de._125m125.kt.ktapi.smartcache.objects;

import java.util.AbstractList;
import java.util.List;

import de._125m125.kt.ktapi.smartcache.Timestamped;

public class TimestampedList<T> extends AbstractList<T> implements Timestamped {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -7176069598333249778L;
    private final long        timestamp;
    private final boolean     cacheHit;
    private final List<T>     messages;

    public TimestampedList(final List<T> messages, final long timestamp, final boolean cacheHit) {
        this.messages = messages;
        this.timestamp = timestamp;
        this.cacheHit = cacheHit;
    }

    public TimestampedList(final List<T> messages, final long timestamp) {
        this(messages, timestamp, false);
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public T get(final int index) {
        return this.messages.get(index);
    }

    @Override
    public int size() {
        return this.messages.size();
    }

    @Override
    public boolean wasCacheHit() {
        return this.cacheHit;
    }

}
