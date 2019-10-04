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
package de._125m125.kt.ktapi.core.results;

import java.util.List;

import de._125m125.kt.ktapi.core.entities.Item;

public class ItemPayinResult {
    private final EntryList<Item> succeeded;
    private final EntryList<Item> failed;

    public ItemPayinResult() {
        this(null, null);
    }

    public ItemPayinResult(final List<Item> succeeded, final List<Item> failed) {
        this.succeeded = EntryList.of(succeeded);
        this.failed = EntryList.of(failed);
    }

    public EntryList<Item> getSucceeded() {
        return this.succeeded;
    }

    public EntryList<Item> getFailed() {
        return this.failed;
    }

    public boolean hasFailures() {
        return this.failed != null && !this.failed.isEmpty();
    }

    public boolean hasSuccesses() {
        return this.succeeded != null && !this.succeeded.isEmpty();
    }

    @Override
    public String toString() {
        return "ItemPayinResult [succeeded=" + this.succeeded + ", failed=" + this.failed + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.failed == null) ? 0 : this.failed.hashCode());
        result = prime * result + ((this.succeeded == null) ? 0 : this.succeeded.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemPayinResult other = (ItemPayinResult) obj;
        if (this.failed == null) {
            if (other.failed != null) {
                return false;
            }
        } else if (!this.failed.equals(other.failed)) {
            return false;
        }
        if (this.succeeded == null) {
            if (other.succeeded != null) {
                return false;
            }
        } else if (!this.succeeded.equals(other.succeeded)) {
            return false;
        }
        return true;
    }
}
