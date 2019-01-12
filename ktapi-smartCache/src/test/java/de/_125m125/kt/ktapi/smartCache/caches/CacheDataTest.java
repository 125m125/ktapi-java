package de._125m125.kt.ktapi.smartCache.caches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi.smartCache.objects.TimestampedList;

public class CacheDataTest {
    private CacheData<String> uut;
    private ClockExtension    testClock;

    @Before
    public void beforeCacheDataTest() {
        this.testClock = new ClockExtension();
        this.uut = new CacheData<String>(String.class, this.testClock) {

            @Override
            protected void updateEntries(final String[] changedEntries) {
                // TODO Auto-generated method stub

            }

        };
        this.testClock.progress();
    }

    @Test
    public void testSetAndGet_0To2() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0);

        final Optional<TimestampedList<String>> actual = this.uut.get(0, 2);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("a", "b"), 1000, true)),
                actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_2To4() throws Exception {
        this.uut.set(Arrays.asList("c", "d"), 2);

        final Optional<TimestampedList<String>> actual = this.uut.get(2, 4);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("c", "d"), 1000, true)),
                actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_6To7() throws Exception {
        this.uut.set(Arrays.asList("g"), 6);

        final Optional<TimestampedList<String>> actual = this.uut.get(6, 7);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("g"), 1000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_4To5_5To6() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);
        this.uut.set(Arrays.asList("b"), 5);

        final Optional<TimestampedList<String>> actual = this.uut.get(4, 6);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("a", "b"), 1000, true)),
                actual);
        assertTrue(actual.get().wasCacheHit());
        assertEquals(1000, actual.get().getTimestamp());
    }

    @Test
    public void testSetAndGet_missingBefore() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        assertEquals(Optional.empty(), this.uut.get(3, 4));
    }

    @Test
    public void testSetAndGet_missingPartBefore() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        assertEquals(Optional.empty(), this.uut.get(3, 5));
    }

    @Test
    public void testSetAndGet_missingAfter() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        assertEquals(Optional.empty(), this.uut.get(5, 6));
    }

    @Test
    public void testSetAndGet_missingPartAfter() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        assertEquals(Optional.empty(), this.uut.get(4, 6));
    }

    @Test
    public void testGet_empty() throws Exception {
        assertEquals(Optional.empty(), this.uut.get(0, 2));
    }

    @Test
    public void testSetAndGet_single_6() throws Exception {
        this.uut.set(Arrays.asList("g"), 6);

        assertEquals(Optional.of("g"), this.uut.get(6));
    }

    @Test
    public void testSetAndGet_single_missingBefore() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        assertEquals(Optional.empty(), this.uut.get(3));
    }

    @Test
    public void testSetAndGet_single_missingAfter() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        assertEquals(Optional.empty(), this.uut.get(5));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSet_failure_negativeIndex() throws Exception {
        this.uut.set(Arrays.asList("c", "d"), -1);
    }

    @Test
    public void testInvalidate_empty() throws Exception {
        this.uut.invalidate(null);
    }

    @Test
    public void testInvalidate_invalidatesHit() throws Exception {
        this.uut.set(Arrays.asList("a"), 4);

        this.uut.invalidate(null);

        assertEquals(Optional.empty(), this.uut.get(4, 5));
    }

    @Test
    public void testInvalidate_changesTime() throws Exception {
        this.uut.invalidate(null);

        this.testClock.progress();
        assertEquals(2000L, this.uut.getLastInvalidationTime());

        this.uut.invalidate(null);

        assertEquals(3000L, this.uut.getLastInvalidationTime());
    }

    @Test
    public void testGetAll() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0);
        this.uut.set(Arrays.asList("c"), 2);

        final Optional<TimestampedList<String>> actual = this.uut.getAll();
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("a", "b", "c"), 1000, true)),
                actual);
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
        this.uut.set(Arrays.asList("a"), 4);

        this.uut.invalidate(null);

        assertEquals(Optional.empty(), this.uut.getAll());
    }

    @Test
    public void testGetAny_empty() throws Exception {
        final Optional<String> actual = this.uut.getAny("c"::equals);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void testGetAny_hit() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0);
        this.uut.set(Arrays.asList("c"), 2);

        final Optional<String> actual = this.uut.getAny("c"::equals);

        assertEquals(Optional.of("c"), actual);
    }

    @Test
    public void testGetAny_multiple() throws Exception {
        this.uut.set(Arrays.asList("c", "c"), 0);
        this.uut.set(Arrays.asList("c"), 2);

        final Optional<String> actual = this.uut.getAny("c"::equals);

        assertEquals(Optional.of("c"), actual);
    }

    @Test
    public void testGetAny_miss() throws Exception {
        this.uut.set(Arrays.asList("a", "b"), 0);
        this.uut.set(Arrays.asList("c"), 2);

        final Optional<String> actual = this.uut.getAny("d"::equals);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void testAdd_invalidatesTimer() throws Exception {
        this.uut.add(Arrays.asList("1"), 1);

        final Optional<TimestampedList<String>> actual = this.uut.get(1, 2);
        assertEquals(2000, actual.get().getTimestamp());
    }

    @Test
    public void testAdd_empty() throws Exception {
        this.uut.add(Arrays.asList("1"), 1);

        final Optional<TimestampedList<String>> actual = this.uut.get(1, 2);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("1"), 2000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
    }

    @Test
    public void testAdd_twice() throws Exception {
        this.uut.add(Arrays.asList("1"), 1);
        this.uut.add(Arrays.asList("2"), 1);

        final Optional<TimestampedList<String>> actual = this.uut.get(1, 3);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("2", "1"), 2000, true)),
                actual);
        assertTrue(actual.get().wasCacheHit());
    }

    @Test
    public void testAdd_twiceWithGap() throws Exception {
        this.uut.add(Arrays.asList("1"), 3);
        this.uut.add(Arrays.asList("2"), 1);

        Optional<TimestampedList<String>> actual = this.uut.get(1, 2);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("2"), 2000, true)), actual);
        assertTrue(actual.get().wasCacheHit());

        actual = this.uut.get(4, 5);
        assertEquals(Optional.of(new TimestampedList<>(Arrays.asList("1"), 2000, true)), actual);
        assertTrue(actual.get().wasCacheHit());
    }

    @Test
    public void testAdd_multiple() throws Exception {
        this.uut.add(Arrays.asList("1", "2"), 1);
        this.uut.add(Arrays.asList("3", "4"), 1);

        final Optional<TimestampedList<String>> actual = this.uut.get(1, 5);
        assertEquals(
                Optional.of(new TimestampedList<>(Arrays.asList("3", "4", "1", "2"), 2000, true)),
                actual);
        assertTrue(actual.get().wasCacheHit());
    }
}
