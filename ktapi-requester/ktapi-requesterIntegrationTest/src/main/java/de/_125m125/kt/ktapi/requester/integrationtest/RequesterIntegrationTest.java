package de._125m125.kt.ktapi.requester.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import de._125m125.kt.ktapi.core.BuySell;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.core.results.WriteResult;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.TokenUser;

public abstract class RequesterIntegrationTest {
    @ClassRule
    public static final WireMockClassRule wireMockRule =
            new WireMockClassRule(new WireMockConfiguration()
                    .usingFilesUnderClasspath("de/_125m125/kt/ktapi/requester").dynamicPort());

    @Rule
    public final WireMockClassRule        instanceRule = RequesterIntegrationTest.wireMockRule;

    private KtRequester                   uut;
    private final TokenUser               user         = new TokenUser("123", "234", "345");

    @Before
    public void initializeRequester() {
        this.uut = Objects.requireNonNull(
                createRequester(this.instanceRule.baseUrl(), new KtUserStore(this.user)));
    }

    @After
    public void closeRequester() {
        if (this.uut != null) {
            try {
                this.uut.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract KtRequester createRequester(String baseUrl, KtUserStore userStore);

    @Test
    public void testGetHistory() throws Exception {
        if (this.uut == null) {
            throw new IllegalStateException(
                    "initializeRequester has to be called before executing tests.");
        }
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
        if (this.uut == null) {
            throw new IllegalStateException(
                    "initializeRequester has to be called before executing tests.");
        }
        final Result<HistoryEntry> history = this.uut.getLatestHistory("4");

        final HistoryEntry expected =
                new HistoryEntry("2019-02-01", 0.782616, 0.648797, null, 1.0, 2, 1.75);

        assertNull(history.getErrorMessage());
        assertTrue(history.isSuccessful());
        assertEquals(expected, history.getContent());
    }

    @Test
    public void testCreateOrder_success() throws Exception {
        if (this.uut == null) {
            throw new IllegalStateException(
                    "initializeRequester has to be called before executing tests.");
        }
        final Result<WriteResult<Trade>> createTrade =
                this.uut.createTrade(this.user.getKey(), BuySell.BUY, "4", 1, ".1");

        final WriteResult<Trade> expected =
                new WriteResult<>(true, "creationsuccess", new Trade(2282356126412355415L, true,
                        "4", "Cobblestone(4)", 1, 0.1, 1, -200, 1, false));

        System.out.println(createTrade);
        assertNull(createTrade.getErrorMessage());
        assertTrue(createTrade.isSuccessful());
        assertEquals(expected, createTrade.getContent());
    }

}
