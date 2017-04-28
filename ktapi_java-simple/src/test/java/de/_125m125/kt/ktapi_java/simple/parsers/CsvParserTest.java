package de._125m125.kt.ktapi_java.simple.parsers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi_java.core.objects.Item;

public class CsvParserTest {

    private CsvParser uut;

    @Before
    public void beforeURLParamAuthenticatorTest() {
        this.uut = new CsvParser();
    }

    @Test
    public void testParseReader_itemlist() throws Exception {
        List<String[]> parse;
        try (final Reader is = new InputStreamReader(CsvParserTest.class.getResourceAsStream("itemlist.csv"))) {
            parse = this.uut.parse(is);
        }
        assertEquals(2, parse.size());

        assertArrayEquals(new String[] { "-1", "Kadis(-1)", "100164642341.350170" }, parse.get(0));
        assertArrayEquals(new String[] { "1", "Stone(1)", "99999999999993124" }, parse.get(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testParseReaderClass_itemlist() throws Exception {
        List<Item> parse;
        try (final Reader is = new InputStreamReader(CsvParserTest.class.getResourceAsStream("itemlist.csv"))) {
            parse = (List<Item>) this.uut.parse(is, Item.class);
        }
        assertEquals(2, parse.size());

        assertEquals(new Item("-1", "Kadis(-1)", 100164642341.350170), parse.get(0));
        assertEquals(new Item("1", "Stone(1)", 99999999999993124D), parse.get(1));
    }

}
