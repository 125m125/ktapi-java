/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.NearMiss;

import de._125m125.kt.ktapi.core.BuySell;
import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Trade;
import de._125m125.kt.ktapi.core.results.ItemPayinResult;
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
    public final WireMockClassRule instanceRule = RequesterIntegrationTest.wireMockRule;

    private KtRequester     uut;
    private final TokenUser user = new TokenUser("123", "234", "345");

    @Before
    public void initializeRequester() {
        this.uut = Objects.requireNonNull(
                createRequester(this.instanceRule.baseUrl(), new KtUserStore(this.user)));
    }

    @After
    public void closeRequester() {
        final ServeEvent serveEvent =
                RequesterIntegrationTest.wireMockRule.getAllServeEvents().get(0);
        if (!serveEvent.getWasMatched()) {
            final List<NearMiss> findNearMissesFor = RequesterIntegrationTest.wireMockRule
                    .findNearMissesFor(serveEvent.getRequest());
            System.out.println(findNearMissesFor);
        }
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

    @Test
    public void testAdminAddItemsSuccess() throws Exception {
        if (this.uut == null) {
            throw new IllegalStateException(
                    "initializeRequester has to be called before executing tests.");
        }
        final Result<WriteResult<ItemPayinResult>> actual =
                this.uut.adminAddItems(this.user.getKey(), "test",
                        Arrays.asList(new Item("4", "Cobblestone", 64), new Item("1", "Stone", 10)),
                        "Test");

        final ItemPayinResult expected =
                new ItemPayinResult(Arrays.asList(new Item("4", 64), new Item("1", 10)), null);

        assertEquals(200, actual.getStatus());
        assertEquals(expected, actual.getContent().getObject());
    }

    @Test
    public void testAdminAddItemsPartialSuccess() throws Exception {
        if (this.uut == null) {
            throw new IllegalStateException(
                    "initializeRequester has to be called before executing tests.");
        }
        final Result<WriteResult<ItemPayinResult>> actual = this.uut.adminAddItems(
                this.user.getKey(), "test",
                Arrays.asList(new Item("4", "Cobblestone", 64), new Item("-5", "Unknown", 10)),
                "Test");

        final ItemPayinResult expected = new ItemPayinResult(Arrays.asList(new Item("4", 64)),
                Arrays.asList(new Item("-5", 0)));

        assertEquals(207, actual.getStatus());
        assertEquals("SomeUnknownItems", actual.getContent().getMessage());
        assertEquals(expected, actual.getContent().getObject());
    }
}
