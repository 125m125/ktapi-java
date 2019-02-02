package de._125m125.kt.ktapi.smartcache.caches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi.smartcache.caches.ReplaceOrPrependCacheData;
import de._125m125.kt.ktapi.smartcache.objects.TimestampedList;

public class ReplaceOrPrependCacheDataTest {

    ReplaceOrPrependCacheData<String> uut;
    ClockExtension                    clock;

    @Before
    public void beforePrependCacheDataTest() throws Exception {
        this.clock = new ClockExtension();
        this.uut = new ReplaceOrPrependCacheData<>(String.class, String::hashCode, this.clock);
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
    public void testUpdatePrependsMissing() {
        this.uut.updateEntries(Arrays.asList("EB")); // hash EB == hash Da

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("EB", "Aa", "Ba", "Ca"), actual.get());
    }

    @Test
    public void testUpdatePrependsToEmptyList() {
        this.uut.invalidate(null);

        this.uut.updateEntries(Arrays.asList("EB")); // hash EB == hash Da

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("EB"), actual.get());
    }

    @Test
    public void testUpdateReplacesMatchAndPrependsMissing() {
        this.uut.updateEntries(new String[] { "CB", "EB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("EB", "Aa", "CB", "Ca"), actual.get());
    }

    @Test
    public void testUpdateHandlesNullInList() {
        this.uut.set(Arrays.asList("a"), 4);
        this.uut.updateEntries(new String[] { "CB", "EB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("EB", "Aa", "CB", "Ca", null, "a"), actual.get());
    }

    @Test
    public void testUpdateInvalidatesTimeOnReplace() {
        this.uut.updateEntries(new String[] { "CB" });

        assertEquals(2000, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testUpdateInvalidatesTimeOnPrepend() {
        this.uut.updateEntries(new String[] { "EB" });

        assertEquals(2000, this.uut.getLastInvalidationTime());
    }

}
