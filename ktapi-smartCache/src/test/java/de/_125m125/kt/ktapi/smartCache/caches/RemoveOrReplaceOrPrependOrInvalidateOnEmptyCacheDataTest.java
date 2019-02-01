package de._125m125.kt.ktapi.smartCache.caches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;

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
        uut.updateEntries(new String[] { "CB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("Aa", "Ca"), actual.get());
    }

    @Test
    public void testRemovesAndReplacesMatches() {
        fillUut();
        uut.updateEntries(new String[] { "BB", "CB" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("BB", "Ca"), actual.get());
    }

    @Test
    public void testUpdateEntriesInvalidatesIfEmptyList() throws Exception {
        this.uut.updateEntries(Arrays.asList("a"));

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
        assertEquals(2000l, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testUpdateEntriesInvalidatesIfEmptyArray() throws Exception {
        this.uut.updateEntries(new String[] { "a", "b" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertFalse(actual.isPresent());
        assertEquals(2000l, this.uut.getLastInvalidationTime());
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
