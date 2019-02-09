package de._125m125.kt.ktapi.smartcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import de._125m125.kt.ktapi.core.KtNotificationManager;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Message;
import de._125m125.kt.ktapi.core.entities.Notification;
import de._125m125.kt.ktapi.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi.core.results.Callback;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.users.TokenUserKey;
import de._125m125.kt.ktapi.smartcache.caches.CacheData;
import de._125m125.kt.ktapi.smartcache.objects.TimestampedHistoryEntry;
import de._125m125.kt.ktapi.smartcache.objects.TimestampedList;

@RunWith(MockitoJUnitRunner.class)
public class KtSmartCacheTest {

    @Mock
    private KtRequester               requester;
    @Mock
    private KtNotificationManager<?>  notificationmanager;

    @InjectMocks
    private KtSmartCache              uut;

    private Map<String, CacheData<?>> cache;

    @Before
    @SuppressWarnings("unchecked")
    public void beforeTest() {
        this.cache = Whitebox.getInternalState(this.uut, Map.class);
    }

    @Test
    public void testinvalidateHistory() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history-1", cacheData);

        this.uut.invalidateHistory("-1");

        verify(cacheData).invalidate(null);
    }

    @Test
    public void testinvalidateHistory_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history1", cacheData);

        this.uut.invalidateHistory("-1");

        verify(cacheData, times(0)).invalidate(null);
    }

    @Test
    public void testinvalidateHistory_empty() throws Exception {
        this.uut.invalidateHistory("-1");
    }

    @Test
    public void testIsValidOrderBook_validHit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<OrderBookEntry> cacheData = mock(CacheData.class);
        this.cache.put("orderbook1", cacheData);
        when(cacheData.getLastInvalidationTime()).thenReturn(10000L);

        final List<OrderBookEntry> orderbook = new TimestampedList<>(
                Arrays.asList(new OrderBookEntry("buy", 1.0, 1)), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertTrue(result);
    }

    @Test
    public void testIsValidOrderBook_invalidHit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<OrderBookEntry> cacheData = mock(CacheData.class);
        this.cache.put("orderbook1", cacheData);
        when(cacheData.getLastInvalidationTime()).thenReturn(20000L);

        final List<OrderBookEntry> orderbook = new TimestampedList<>(
                Arrays.asList(new OrderBookEntry("buy", 1.0, 1)), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertFalse(result);
    }

    @Test
    public void testIsValidOrderBook_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<OrderBookEntry> cacheData = mock(CacheData.class);
        this.cache.put("orderbook2", cacheData);
        when(cacheData.getLastInvalidationTime()).thenReturn(10000L);

        final List<OrderBookEntry> orderbook = new TimestampedList<>(
                Arrays.asList(new OrderBookEntry("buy", 1.0, 1)), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertFalse(result);
    }

    @Test
    public void testIsValidOrderBook_empty() throws Exception {
        final List<OrderBookEntry> orderbook = new TimestampedList<>(
                Arrays.asList(new OrderBookEntry("buy", 1.0, 1)), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertFalse(result);
    }

    @Test
    public void testUpdate_invalidates() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<Message> cacheData = mock(CacheData.class);
        this.cache.put("messages3", cacheData);

        final Notification n = mock(Notification.class);
        final Map<String, String> details = new HashMap<>();
        details.put("source", "messages");
        details.put("key", "3");
        when(n.getDetails()).thenReturn(details);
        when(n.getBase32Uid()).thenReturn("3");
        when(n.getUid()).thenReturn(3L);
        when(n.getType()).thenReturn("type");
        when(n.isSelfCreated()).thenReturn(false);

        this.uut.update(n);

        verify(cacheData).invalidate(null);
    }

    @Test
    public void testGetHistory_hit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history1", cacheData);
        final Optional<TimestampedList<HistoryEntry>> expected = Optional.of(new TimestampedList<>(
                Arrays.asList(new HistoryEntry("a", 1d, 2d, 1d, 2d, 1, 2d)), 1000L, true));
        when(cacheData.get(5, 6)).thenReturn(expected);

        final Result<List<HistoryEntry>> history = this.uut.getHistory("1", 1, 5);

        assertEquals(expected.get(), history.getContent());
        assertEquals(KtSmartCache.CACHE_HIT_STATUS, history.getStatus());
        verify(this.requester, times(0)).getHistory(any(), anyInt(), anyInt());
    }

    @Test
    public void testGetHistory_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history1", cacheData);
        when(cacheData.get(5, 6)).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        final Result<List<HistoryEntry>> result = mock(Result.class);
        final List<HistoryEntry> asList = Arrays
                .asList(new HistoryEntry("a", 1d, 2d, 1d, 2d, 1, 2d));
        when(this.requester.getHistory("1", 1, 5)).thenReturn(result);
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            final Callback<List<HistoryEntry>> argumentAt = invocation.getArgumentAt(0,
                    Callback.class);
            argumentAt.onSuccess(200, asList);
            return null;
        }).when(result).addCallback(any());
        when(cacheData.set(asList, 5, false)).thenReturn(new TimestampedList<>(asList, 1000));

        final Result<List<HistoryEntry>> history = this.uut.getHistory("1", 1, 5);

        assertEquals(asList, history.getContent());
        assertEquals(200, history.getStatus());
        assertTrue(history.getContent() instanceof TimestampedList);
    }

    @Test
    public void testGetLatestHistory_hit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history1", cacheData);
        final Optional<HistoryEntry> expected = Optional.of(new TimestampedHistoryEntry(
                new HistoryEntry("a", 1d, 2d, 1d, 2d, 1, 2d), 1000L, true));
        when(cacheData.get(0)).thenReturn(expected);

        final Result<HistoryEntry> history = this.uut.getLatestHistory("1");

        assertEquals(new TimestampedHistoryEntry(expected.get(), 1000, true), history.getContent());
        assertEquals(KtSmartCache.CACHE_HIT_STATUS, history.getStatus());
        assertTrue(history.getContent() instanceof Timestamped);
        assertEquals(true, ((TimestampedHistoryEntry) history.getContent()).wasCacheHit());
        verify(this.requester, times(0)).getLatestHistory(any());
    }

    @Test
    public void testGetLatestHistory_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history1", cacheData);
        when(cacheData.get(0)).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        final Result<HistoryEntry> result = mock(Result.class);
        final HistoryEntry asList = new HistoryEntry("a", 1d, 2d, 1d, 2d, 1, 2d);
        when(this.requester.getLatestHistory("1")).thenReturn(result);
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            final Callback<HistoryEntry> argumentAt = invocation.getArgumentAt(0, Callback.class);
            argumentAt.onSuccess(200, asList);
            return null;
        }).when(result).addCallback(any());

        final Result<HistoryEntry> history = this.uut.getLatestHistory("1");

        assertEquals(new TimestampedHistoryEntry(asList, 1000, false), history.getContent());
        assertEquals(200, history.getStatus());
        assertTrue(history.getContent() instanceof Timestamped);
        assertEquals(false, ((TimestampedHistoryEntry) history.getContent()).wasCacheHit());
    }

    @Test
    public void testGetMessages_requestReturnsLessEntriesThanExpected() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<Message> cacheData = mock(CacheData.class);
        this.cache.put("message3", cacheData);
        when(cacheData.get(5, 7)).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        final Result<List<Message>> result = mock(Result.class);
        final List<Message> asList = Arrays.asList(new Message(1546300800, "hello"));
        when(this.requester.getMessages(any(), eq(2), eq(5))).thenReturn(result);
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            final Callback<List<Message>> argumentAt = invocation.getArgumentAt(0, Callback.class);
            argumentAt.onSuccess(200, asList);
            return null;
        }).when(result).addCallback(any());
        when(cacheData.set(asList, 5, true)).thenReturn(new TimestampedList<>(asList, 1000));

        final Result<List<Message>> messages = this.uut.getMessages(new TokenUserKey("1", "2"), 2,
                5);

        assertEquals(asList, messages.getContent());
        assertEquals(200, messages.getStatus());
        assertTrue(messages.getContent() instanceof TimestampedList);
    }
}
