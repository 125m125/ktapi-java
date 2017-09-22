package de._125m125.kt.ktapi_java.retrofitUnivocityTsvparser;

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

import de._125m125.kt.ktapi_java.core.entities.Trade;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitUnivosityTsvparserIntegrationTest {

    @Test
    public void testParse() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("trades.tsv"); Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getTradeListType(), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<Trade> convert = (List<Trade>) responseBodyConverter.convert(body);

        final List<Trade> expected = Arrays.asList(
                new Trade(5608204481722859487L, false, "JuniCrate3", "JuniCrate3(JuniCrate3)", 3456, 1000.05, 3456,
                        932723928279L, 0, false),
                new Trade(8906063564992935262L, false, "1", "Stone(1)", 3456, 0.33, 1985, 654984495, 0, false),
                new Trade(1128838699383035894L, true, "264", "Diamond(264)", 2, 0.10, 2, 200000, 0, true));
        assertEquals(expected, convert);
    }

    @Test
    public void testParseEmptys() throws Exception {
        String content;
        try (InputStream resourceAsStream = RetrofitUnivosityTsvparserIntegrationTest.class
                .getResourceAsStream("trades_empty.tsv"); Scanner scanner = new java.util.Scanner(resourceAsStream)) {
            content = scanner.useDelimiter("\\A").next();
        }
        final Converter<ResponseBody, ?> responseBodyConverter = new UnivocityConverterFactory()
                .responseBodyConverter(getTradeListType(), new Annotation[0], null);
        final ResponseBody body = ResponseBody.create(MediaType.parse("text/tsv"), content);

        @SuppressWarnings("unchecked")
        final List<Trade> convert = (List<Trade>) responseBodyConverter.convert(body);

        final List<Trade> expected = Arrays.asList();
        assertEquals(expected, convert);
    }

    private Type getTradeListType() throws Exception {
        System.out.println(Arrays.toString(RetrofitUnivosityTsvparserIntegrationTest.class.getMethods()));
        return RetrofitUnivosityTsvparserIntegrationTest.class.getMethod("typeHelper").getGenericReturnType();
    }

    public static List<Trade> typeHelper() {
        return null;
    }
}
