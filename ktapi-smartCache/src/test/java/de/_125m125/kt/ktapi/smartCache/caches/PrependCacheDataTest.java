package de._125m125.kt.ktapi.smartCache.caches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;

public class PrependCacheDataTest {

    PrependCacheData<String> uut;
    ClockExtension           clock;

    @Before
    public void beforePrependCacheDataTest() throws Exception {
        this.clock = new ClockExtension();
        this.uut = new PrependCacheData<>(String.class, this.clock);
        this.uut.set(Arrays.asList("a", "b", "c"), 0);
        this.clock.progress();
    }

    @Test
    public void testUpdateSingle() {
        this.uut.updateEntries(new String[] { "d" });

        final Optional<TimestampedList<String>> actual = this.uut.get(0, 4);
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("d", "a", "b", "c"), actual.get());
    }

    @Test
    public void testUpdateMultiple() {
        this.uut.updateEntries(new String[] { "d", "e" });

        final Optional<TimestampedList<String>> actual = this.uut.get(0, 5);
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("d", "e", "a", "b", "c"), actual.get());
    }

    @Test
    public void testUpdateInvalidates() {
        this.uut.updateEntries(Arrays.asList("d"));
        assertEquals(2000L, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testUpdateEmptyList() {
        this.uut.invalidate(null);

        this.uut.updateEntries(new String[] { "d" });

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertTrue(actual.isPresent());
        assertEquals(Arrays.asList("d"), actual.get());
    }

}
