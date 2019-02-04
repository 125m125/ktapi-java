package de._125m125.kt.ktapi.retrofit.tsvparser.univocity;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de._125m125.kt.ktapi.core.entities.HistoryEntry;
import de._125m125.kt.ktapi.core.entities.Item;
import de._125m125.kt.ktapi.core.entities.Payout;
import de._125m125.kt.ktapi.core.entities.Trade;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitUnivosityTsvparserIntegrationTest {

    @Test
    public void testParseTrade() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("trades.tsv");
                Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getListType("trade"), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<Trade> convert = (List<Trade>) responseBodyConverter.convert(body);

        final List<Trade> expected = Arrays.asList(
                new Trade(5608204481722859487L, false, "JuniCrate3", "JuniCrate3(JuniCrate3)", 3456,
                        1000.05, 3456, 932723928279L, 0, false),
                new Trade(8906063564992935262L, false, "1", "Stone(1)", 3456, 0.33, 1985, 654984495,
                        0, false),
                new Trade(1128838699383035894L, true, "264", "Diamond(264)", 2, 0.10, 2, 200000, 0,
                        true));
        assertEquals(expected, convert);
    }

    @Test
    public void testParseHistory() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("history.tsv");
                Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getListType("historyEntry"), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<HistoryEntry> convert = (List<HistoryEntry>) responseBodyConverter.convert(body);

        final List<HistoryEntry> expected = Arrays.asList(
                new HistoryEntry("2019-01-31", 0.648797, 0.648797, null, null, 0, 0),
                new HistoryEntry("2019-01-30", 0.782616, 0.648797, 1.500000, 2.000000, 4, 1.750000),
                new HistoryEntry("2019-01-29", 0.461682, 0.782616, 1.000000, 1.000000, 2,
                        2.000000));
        assertEquals(expected, convert);
    }

    @Test
    public void testParseItems() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("items.tsv");
                Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getListType("item"), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<Item> convert = (List<Item>) responseBodyConverter.convert(body);

        final List<Item> expected = Arrays.asList(new Item("-2", "Raivotar-Superlos(-2)", 0),
                new Item("-1", "Kadis(-1)", 125137.726744), new Item("1", "Stone(1)", 0),
                new Item("3", "Dirt(3)", 585));
        assertEquals(expected, convert);
    }

    @Test
    public void testParsePayouts() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("payouts.tsv");
                Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getListType("payout"), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<Payout> convert = (List<Payout>) responseBodyConverter.convert(body);

        final List<Payout> expected = Arrays.asList(
                new Payout(3282017611L, "3", "Dirt(3)", 12, "Offen", "bs1", "2019-02-04 23:10:56.0",
                        null),
                new Payout(635057382L, "3", "Dirt(3)", 10, "Erfolgreich", "lieferung",
                        "2019-02-01 21:32:22.0", "hello"),
                new Payout(3775312640L, "-1", "Kadis(-1)", 10.000000, "Unbekannt", "vkba",
                        "2018-12-16 19:32:06.0", null),
                new Payout(234083005L, "-1", "Kadis(-1)", 10.010000, "Abgebrochen", "kadcon",
                        "2018-12-16 19:29:53.0", null));
        assertEquals(expected, convert);
    }

    @Test
    public void testParseEmptys() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("trades_empty.tsv");
                Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getListType("trade"), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<Trade> convert = (List<Trade>) responseBodyConverter.convert(body);

        final List<Trade> expected = Arrays.asList();
        assertEquals(expected, convert);
    }

    private Type getListType(String type) throws Exception {
        return RetrofitUnivosityTsvparserIntegrationTest.class.getMethod(type)
                .getGenericReturnType();
    }

    public static List<Trade> trade() {
        return null;
    }

    public static List<HistoryEntry> historyEntry() {
        return null;
    }

    public static List<Item> item() {
        return null;
    }

    public static List<Payout> payout() {
        return null;
    }
}
