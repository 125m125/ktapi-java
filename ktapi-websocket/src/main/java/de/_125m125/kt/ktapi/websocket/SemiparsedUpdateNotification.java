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
package de._125m125.kt.ktapi.websocket;

import java.lang.reflect.Array;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import de._125m125.kt.ktapi.core.entities.Entity;
import de._125m125.kt.ktapi.core.entities.UpdateNotification;

public class SemiparsedUpdateNotification<T> extends UpdateNotification<T> {

    private final JsonArray contents;

    public SemiparsedUpdateNotification(final boolean selfCreated, final long uid,
            final String base32Uid, final Map<String, String> details, final JsonArray contents) {
        super(selfCreated, uid, base32Uid, details, null);
        this.contents = contents;
    }

    @Override
    public boolean hasChangedEntries() {
        return this.contents != null && this.contents.size() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getChangedEntries() {
        if (this.contents == null) {
            return (T[]) new Object[0];
        }
        if (!super.hasChangedEntries()) {
            super.changedEntries = (T[]) new Gson().fromJson(this.contents,
                    Array.newInstance(Entity.forUpdateChannel(getSource()).getInstanceClass(), 0)
                            .getClass());
        }
        return super.getChangedEntries();
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
