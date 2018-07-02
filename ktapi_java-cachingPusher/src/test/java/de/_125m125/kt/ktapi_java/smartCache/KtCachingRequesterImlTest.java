package de._125m125.kt.ktapi_java.smartCache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.entities.HistoryEntry;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.Notification;
import de._125m125.kt.ktapi_java.core.entities.OrderBookEntry;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.Callback;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.smartCache.objects.TimestampedHistoryEntry;
import de._125m125.kt.ktapi_java.smartCache.objects.TimestampedList;

@RunWith(MockitoJUnitRunner.class)
public class KtCachingRequesterImlTest {

    @Mock
    private KtRequester<UserKey>           requester;
    @Mock
    private KtNotificationManager<UserKey> notificationmanager;

    @InjectMocks
    private KtCachingRequesterIml<UserKey> uut;

    private Map<String, CacheData<?>>      cache;

    @Before
    @SuppressWarnings("unchecked")
    public void beforeTest() {
        this.cache = Whitebox.getInternalState(this.uut, Map.class);
    }

    @Test
    public void testinvalidateHistory() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history--1", cacheData);

        this.uut.invalidateHistory("-1");

        verify(cacheData).invalidate();
    }

    @Test
    public void testinvalidateHistory_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history-1", cacheData);

        this.uut.invalidateHistory("-1");

        verify(cacheData, times(0)).invalidate();
    }

    @Test
    public void testinvalidateHistory_empty() throws Exception {
        this.uut.invalidateHistory("-1");
    }

    @Test
    public void testIsValidOrderBook_validHit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<OrderBookEntry> cacheData = mock(CacheData.class);
        this.cache.put("orderbook-1", cacheData);
        when(cacheData.getLastInvalidationTime()).thenReturn(10000L);

        final List<OrderBookEntry> orderbook = new TimestampedList<>(Arrays.asList(new OrderBookEntry()), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertTrue(result);
    }

    @Test
    public void testIsValidOrderBook_invalidHit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<OrderBookEntry> cacheData = mock(CacheData.class);
        this.cache.put("orderbook-1", cacheData);
        when(cacheData.getLastInvalidationTime()).thenReturn(20000L);

        final List<OrderBookEntry> orderbook = new TimestampedList<>(Arrays.asList(new OrderBookEntry()), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertFalse(result);
    }

    @Test
    public void testIsValidOrderBook_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<OrderBookEntry> cacheData = mock(CacheData.class);
        this.cache.put("orderbook-2", cacheData);
        when(cacheData.getLastInvalidationTime()).thenReturn(10000L);

        final List<OrderBookEntry> orderbook = new TimestampedList<>(Arrays.asList(new OrderBookEntry()), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertFalse(result);
    }

    @Test
    public void testIsValidOrderBook_empty() throws Exception {
        final List<OrderBookEntry> orderbook = new TimestampedList<>(Arrays.asList(new OrderBookEntry()), 15000L);

        final boolean result = this.uut.isValidOrderBook("1", orderbook);

        assertFalse(result);
    }

    @Test
    public void testUpdate_invalidates() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<Message> cacheData = mock(CacheData.class);
        this.cache.put("messages-3", cacheData);

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

        verify(cacheData).invalidate();
    }

    @Test
    public void testGetHistory_hit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history-1", cacheData);
        final Optional<TimestampedList<HistoryEntry>> expected = Optional
                .of(new TimestampedList<>(Arrays.asList(new HistoryEntry("a", 1, 2, 1, 2, 1, 2)), 1000L, true));
        when(cacheData.get(5, 6)).thenReturn(expected);

        final Result<List<HistoryEntry>> history = this.uut.getHistory("1", 1, 5);

        assertEquals(expected.get(), history.getContent());
        assertEquals(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE, history.getStatus());
        verify(this.requester, times(0)).getHistory(any(), anyInt(), anyInt());
    }

    @Test
    public void testGetHistory_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history-1", cacheData);
        when(cacheData.get(5, 6)).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        final Result<List<HistoryEntry>> result = mock(Result.class);
        final List<HistoryEntry> asList = Arrays.asList(new HistoryEntry("a", 1, 2, 1, 2, 1, 2));
        when(this.requester.getHistory("1", 1, 5)).thenReturn(result);
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            final Callback<List<HistoryEntry>> argumentAt = invocation.getArgumentAt(0, Callback.class);
            argumentAt.onSuccess(200, asList);
            return null;
        }).when(result).addCallback(any());
        when(cacheData.set(asList, 5, 6)).thenReturn(new TimestampedList<>(asList, 1000));

        final Result<List<HistoryEntry>> history = this.uut.getHistory("1", 1, 5);

        assertEquals(asList, history.getContent());
        assertEquals(200, history.getStatus());
        assertTrue(history.getContent() instanceof TimestampedList);
    }

    @Test
    public void testGetLatestHistory_hit() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history-1", cacheData);
        final Optional<HistoryEntry> expected = Optional
                .of(new TimestampedHistoryEntry(new HistoryEntry("a", 1, 2, 1, 2, 1, 2), 1000L, true));
        when(cacheData.get(0)).thenReturn(expected);

        final Result<HistoryEntry> history = this.uut.getLatestHistory("1");

        assertEquals(new TimestampedHistoryEntry(expected.get(), 1000, true), history.getContent());
        assertEquals(KtCachingRequesterIml.CACHE_HIT_STATUS_CODE, history.getStatus());
        assertTrue(history.getContent() instanceof Timestamped);
        assertEquals(true, ((TimestampedHistoryEntry) history.getContent()).wasCacheHit());
        verify(this.requester, times(0)).getLatestHistory(any());
    }

    @Test
    public void testGetLatestHistory_miss() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheData<HistoryEntry> cacheData = mock(CacheData.class);
        this.cache.put("history-1", cacheData);
        when(cacheData.get(0)).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        final Result<HistoryEntry> result = mock(Result.class);
        final HistoryEntry asList = new HistoryEntry("a", 1, 2, 1, 2, 1, 2);
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
    @Ignore
    public void testGetItems() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    @Ignore
    public void testGetItem() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

}
