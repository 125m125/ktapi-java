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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi.smartcache.caches.ReplaceOrInvalidateCacheData;
import de._125m125.kt.ktapi.smartcache.objects.TimestampedList;

public class ReplaceOrInvalidateCacheDataTest {

    private ReplaceOrInvalidateCacheData<String> uut;
    private ClockExtension                       clock;

    @Before
    public void beforePrependCacheDataTest() throws Exception {
        this.clock = new ClockExtension();
        this.uut = new ReplaceOrInvalidateCacheData<>(String.class, String::hashCode, this.clock);
        this.uut.set(Arrays.asList("Aa", "Ba", "Ca"), 0);
        this.clock.progress();
    }

    @Test
    public void testUpdateReplacesMatch() {
        this.uut.updateEntries(new String[] { "BB" }); // hash Aa == hash BB

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("BB", "Ba", "Ca"), actual.get());
    }

    @Test
    public void testUpdateInvalidatesOnMissing() {
        this.uut.updateEntries(new String[] { "EB" }); // hash EB == hash Da

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testUpdateInvalidatesEmptyList() {
        this.uut.invalidate(null);

        this.uut.updateEntries(new String[] { "EB" }); // hash EB == hash Da

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testUpdateInvalidatesOnPartialMatch() {
        this.uut.updateEntries(new String[] { "CB", "EB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
    }

    @Test
    public void testUpdateHandlesNullInList() {
        this.uut.set(Arrays.asList("a"), 4);
        this.uut.updateEntries(new String[] { "CB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("Aa", "CB", "Ca", null, "a"), actual.get());
    }

    @Test
    public void testUpdateInvalidatesTimeOnReplace() {
        this.uut.updateEntries(new String[] { "CB" });

        assertEquals(2000, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testUpdateInvalidatesTimeOnInvalidate() {
        this.uut.updateEntries(new String[] { "EB" });

        assertEquals(2000, this.uut.getLastInvalidationTime());
    }
}
