package de._125m125.kt.ktapi.smartCache.caches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;

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
