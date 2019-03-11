package de._125m125.kt.ktapi.requester.jersey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de._125m125.kt.ktapi.core.BuySell;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.requester.jersey.parsers.JacksonJsonProviderRegistrator;

public class KtJerseyRequesterTest {
    @Rule
    public WireMockRule       wireMockRule = new WireMockRule(new WireMockConfiguration()
            .usingFilesUnderClasspath("de/_125m125/kt/ktapi/requester").dynamicPort());

    private KtJerseyRequester uut;
    private final TokenUser   user         = new TokenUser("123", "234", "345");

    @Before
    public void beforeKtJerseyRequesterTest() {
        this.uut = new KtJerseyRequester(this.wireMockRule.baseUrl(), null,
                JacksonJsonProviderRegistrator.INSTANCE);
    }

    @Test
    public void testGetHistory() throws Exception {
        final Result<List<HistoryEntry>> history = this.uut.getHistory("4", 10, 0);

        final List<HistoryEntry> expected =
                Arrays.asList(new HistoryEntry("2019-01-31", 0.648797, 0.648797, null, null, 0, 0),
                        new HistoryEntry("2019-01-30", 0.782616, 0.648797, 1.500000, 2.000000, 4,
                                1.750000),
                        new HistoryEntry("2019-01-29", 0.461682, 0.782616, 1.000000, 1.000000, 2,
                                2.000000));

        assertNull(history.getErrorMessage());
        assertTrue(history.isSuccessful());
        assertEquals(expected, history.getContent());
    }

    @Test
    public void testGetLatestHistory() throws Exception {
        final Result<HistoryEntry> history = this.uut.getLatestHistory("4");

        final HistoryEntry expected =
                new HistoryEntry("2019-02-01", 0.782616, 0.648797, null, 1.0, 2, 1.75);

        assertNull(history.getErrorMessage());
        assertTrue(history.isSuccessful());
        assertEquals(expected, history.getContent());
    }

    @Test
    public void testCreateOrder_success() throws Exception {
        final Result<WriteResult<Trade>> createTrade =
                this.uut.createTrade(this.user.getKey(), BuySell.BUY, "4", 1, ".1");

        final WriteResult<Trade> expected =
                new WriteResult<>(true, "creationsuccess", new Trade(2282356126412355415L, true,
                        "4", "Cobblestone(4)", 1, 0.1, 1, -200, 1, false));

        assertNull(createTrade.getErrorMessage());
        assertTrue(createTrade.isSuccessful());
        assertEquals(expected, createTrade.getContent());
    }

}
