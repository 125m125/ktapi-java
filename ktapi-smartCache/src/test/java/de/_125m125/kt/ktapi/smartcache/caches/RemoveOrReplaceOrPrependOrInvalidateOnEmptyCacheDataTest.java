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

import de._125m125.kt.ktapi.smartcache.objects.TimestampedList;

public class RemoveOrReplaceOrPrependOrInvalidateOnEmptyCacheDataTest {

    RemoveOrReplaceOrPrependOrInvalidateOnEmptyCacheData<String> uut;
    ClockExtension                                               clock;

    @Before
    public void beforePrependCacheDataTest() throws Exception {
        this.clock = new ClockExtension();
        this.uut = new RemoveOrReplaceOrPrependOrInvalidateOnEmptyCacheData<>(String.class,
                String::hashCode, s -> s.equals("CB"), this.clock);
        this.clock.progress();
    }

    private void fillUut() {
        this.uut.set(Arrays.asList("Aa", "Ba", "Ca"), 0);
    }

    @Test
    public void testRemovesMatches() {
        fillUut();
        this.uut.updateEntries(new String[] { "CB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("Aa", "Ca"), actual.get());
    }

    @Test
    public void testRemovesAndReplacesMatches() {
        fillUut();
        this.uut.updateEntries(new String[] { "BB", "CB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("BB", "Ca"), actual.get());
    }

    @Test
    public void testUpdateEntriesInvalidatesIfEmptyList() throws Exception {
        this.uut.updateEntries(Arrays.asList("a"));

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
        assertEquals(2000L, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testUpdateEntriesInvalidatesIfEmptyArray() throws Exception {
        this.uut.updateEntries(new String[] { "a", "b" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
        assertEquals(2000L, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testUpdateReplacesHit() throws Exception {
        fillUut();

        this.uut.updateEntries(new String[] { "BB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("BB", "Ba", "Ca"), actual.get());
    }

    @Test
    public void testUpdatePrependsMissing() throws Exception {
        fillUut();

        this.uut.updateEntries(new String[] { "Da" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("Da", "Aa", "Ba", "Ca"), actual.get());
    }
}
