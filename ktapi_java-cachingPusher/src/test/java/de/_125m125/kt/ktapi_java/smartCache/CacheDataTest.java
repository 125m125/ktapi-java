package de._125m125.kt.ktapi_java.smartCache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi_java.smartCache.objects.TimestampedList;

public class CacheDataTest {
    private final class ClockExtension extends Clock {
        private long current = 1000;

        @Override
        public ZoneId getZone() {
            return ZoneId.of("Z");
        }

        @Override
        public Clock withZone(final ZoneId zone) {
            throw new RuntimeException("This clock does not support Clock#whithZone()");
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(this.current);
        }

        public void progress() {
            this.current += 1000;
        }
    }

    private CacheData<String> uut;
    private ClockExtension    testClock;

    @Before
    public void beforeCacheDataTest() {
        this.testClock = new ClockExtension();
        this.uut = new CacheData<>(this.testClock);
        this.testClock.progress();
    }

    @Test
    public void testSetAndGet_0To2() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0, 2);

        final Optional<TimestampedList<String>> actual = this.uut.get(0, 2);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("a", "b"), 1000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_2To4() throws Exception {
        this.uut.set(Arrays.asList("c", "d"), 2, 4);

        final Optional<TimestampedList<String>> actual = this.uut.get(2, 4);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("c", "d"), 1000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_6To7() throws Exception {
        this.uut.set(Arrays.asList("g"), 6, 7);

        final Optional<TimestampedList<String>> actual = this.uut.get(6, 7);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("g"), 1000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_4To5_5To6() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);
        this.uut.set(Arrays.asList("b"), 5, 6);

        final Optional<TimestampedList<String>> actual = this.uut.get(4, 6);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("a", "b"), 1000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_missingBefore() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        assertEquals(Optional.empty(), this.uut.get(3, 4));
    }

    @Test
    public void testSetAndGet_missingPartBefore() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        assertEquals(Optional.empty(), this.uut.get(3, 5));
    }

    @Test
    public void testSetAndGet_missingAfter() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        assertEquals(Optional.empty(), this.uut.get(5, 6));
    }

    @Test
    public void testSetAndGet_missingPartAfter() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        assertEquals(Optional.empty(), this.uut.get(4, 6));
    }

    @Test
    public void testGet_empty() throws Exception {
        assertEquals(Optional.empty(), this.uut.get(0, 2));
    }

    @Test
    public void testSetAndGet_single_6() throws Exception {
        this.uut.set(Arrays.asList("g"), 6, 7);

        assertEquals(Optional.of("g"), this.uut.get(6));
    }

    @Test
    public void testSetAndGet_single_missingBefore() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        assertEquals(Optional.empty(), this.uut.get(3));
    }

    @Test
    public void testSetAndGet_single_missingAfter() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        assertEquals(Optional.empty(), this.uut.get(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSet_failure_listTooShort() throws Exception {
        this.uut.set(Arrays.asList("c", "d"), 2, 5);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSet_failure_negativeIndex() throws Exception {
        this.uut.set(Arrays.asList("c", "d"), -1, 1);
    }

    @Test
    public void testInvalidate_empty() throws Exception {
        this.uut.invalidate();
    }

    @Test
    public void testInvalidate_invalidatesHit() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        this.uut.invalidate();

        assertEquals(Optional.empty(), this.uut.get(4, 5));
    }

    @Test
    public void testInvalidate_changesTime() throws Exception {
        this.uut.invalidate();

        this.testClock.progress();
        assertEquals(2000L, this.uut.getLastInvalidationTime());

        this.uut.invalidate();

        assertEquals(3000L, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testGetAll() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0, 2);
        this.uut.set(Arrays.asList("c"), 2, 3);

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("a", "b", "c"), 1000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testGetAll_empty() throws Exception {
        final Optional<TimestampedList<String>> actual = this.uut.getAll();

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void testInvalidate_invalidatesGetAll() throws Exception {
        this.uut.set(Arrays.asList("a"), 4, 5);

        this.uut.invalidate();

        assertEquals(Optional.empty(), this.uut.getAll());
    }

    @Test
    public void testGetAny_empty() throws Exception {
        final Optional<String> actual = this.uut.getAny("c"::equals);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void testGetAny_hit() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0, 2);
        this.uut.set(Arrays.asList("c"), 2, 3);

        final Optional<String> actual = this.uut.getAny("c"::equals);

        assertEquals(Optional.of("c"), actual);
    }

    @Test
    public void testGetAny_multiple() throws Exception {
        this.uut.set(Arrays.asList("c", "c"), 0, 2);
        this.uut.set(Arrays.asList("c"), 2, 3);

        final Optional<String> actual = this.uut.getAny("c"::equals);

        assertEquals(Optional.of("c"), actual);
    }

    @Test
    public void testGetAny_miss() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0, 2);
        this.uut.set(Arrays.asList("c"), 2, 3);

        final Optional<String> actual = this.uut.getAny("d"::equals);

        assertEquals(Optional.empty(), actual);
    }
}
