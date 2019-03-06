package de._125m125.kt.ktapi.requester.jersey;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.results.Result;
import de._125m125.kt.ktapi.requester.jersey.parsers.JacksonJsonProviderRegistrator;

public class KtJerseyRequesterTest {
    @Rule
    public WireMockRule       wireMockRule =
            new WireMockRule(new WireMockConfiguration().dynamicPort());

    private KtJerseyRequester uut;

    @Before
    public void beforeKtJerseyRequesterTest() {
        this.uut = new KtJerseyRequester(this.wireMockRule.baseUrl(), null,
                JacksonJsonProviderRegistrator.INSTANCE);
    }

    @Test
    public void testGetHistory() throws Exception {
        this.wireMockRule.stubFor(get(urlEqualTo("/history/4?limit=10&offset=0"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody(getResourceString("history.json"))));

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
        this.wireMockRule.stubFor(get(urlEqualTo("/history/4/latest"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withBody("{\"close\":\"0.648797\",\"open\":\"0.782616\",\"high\":\"1.0\","
                                + "\"low\":\"\",\"unitVolume\":2,\"dollarVolume\":\"1.750000\","
                                + "\"date\":\"2019-02-01\"}")));

        final Result<HistoryEntry> history = this.uut.getLatestHistory("4");

        final HistoryEntry expected =
                new HistoryEntry("2019-02-01", 0.782616, 0.648797, null, 1.0, 2, 1.75);

        assertNull(history.getErrorMessage());
        assertTrue(history.isSuccessful());
        assertEquals(expected, history.getContent());
    }

    private String getResourceString(final String string) throws Exception {
        try (InputStream resourceAsStream =
                KtJerseyRequesterTest.class.getResourceAsStream("history.json");
                Scanner scanner = new Scanner(resourceAsStream)) {
            return scanner.useDelimiter("\\A").next();
        }
    }

}
