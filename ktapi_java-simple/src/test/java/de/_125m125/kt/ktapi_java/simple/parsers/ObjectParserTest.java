package de._125m125.kt.ktapi_java.simple.parsers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import com.univocity.parsers.common.ParsingContext;

public class ObjectParserTest {

    @Test
    public void testObjectParser() throws Exception {
        final ObjectParser<TestObject> uut = new ObjectParser<>(TestObject.class, new TypeConverterFactory());
        final ParsingContext context = mock(ParsingContext.class);
        when(context.headers()).thenReturn(new String[] { "b", "d", "i", "l", "s" });

        uut.processStarted(context);

        uut.rowProcessed(new String[] { "true", "1.23", "4", "5", "asd" }, context);

        uut.processEnded(context);

        final List<TestObject> result = uut.getResult();
        assertEquals(1, result.size());

        final TestObject testObject = result.get(0);
        assertEquals(testObject.getI(), 4);
        assertEquals(testObject.getD(), 1.23, 0.001);
        assertEquals(testObject.getS(), "asd");
        assertEquals(testObject.getL(), 5L);
        assertEquals(testObject.isB(), true);
    }

    @Test
    public void testObjectParser_unknownField() throws Exception {
        final ObjectParser<TestObject> uut = new ObjectParser<>(TestObject.class, new TypeConverterFactory());
        final ParsingContext context = mock(ParsingContext.class);
        when(context.headers()).thenReturn(new String[] { "unknown" });

        uut.processStarted(context);

        uut.rowProcessed(new String[] { "1" }, context);

        uut.processEnded(context);

        final List<TestObject> result = uut.getResult();
        assertEquals(1, result.size());

        final TestObject testObject = result.get(0);
        assertEquals(testObject.getI(), 0);
        assertEquals(testObject.getD(), 0.0, 0.001);
        assertEquals(testObject.getS(), null);
        assertEquals(testObject.getL(), 0);
        assertEquals(testObject.isB(), false);
    }

    @Test
    public void testObjectParser_missingParser() throws Exception {
        final ObjectParser<TestObject> uut = new ObjectParser<>(TestObject.class, new TypeConverterFactory());
        final ParsingContext context = mock(ParsingContext.class);
        when(context.headers()).thenReturn(new String[] { "t" });

        uut.processStarted(context);

        uut.rowProcessed(new String[] { "1" }, context);

        uut.processEnded(context);

        final List<TestObject> result = uut.getResult();
        assertEquals(1, result.size());

        final TestObject testObject = result.get(0);
        assertEquals(testObject.getT(), null);
    }
}
