package de._125m125.kt.ktapi_java.retrofitUnivocityTsvparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TypeConverterFactoryTest {

    @InjectMocks
    private TypeConverterFactory uut;

    @Test
    public void testGetConverterFor_unknown() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(TypeConverterFactoryTest.class);
        assertNull(converter);
    }

    @Test
    public void testGetConverterFor_String() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(String.class);
        assertNotNull(converter);
        assertEquals("hello", converter.apply("hello"));
    }

    @Test
    public void testGetConverterFor_Boolean() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(Boolean.class);
        assertNotNull(converter);
        assertEquals(Boolean.TRUE, converter.apply("true"));
        assertEquals(Boolean.FALSE, converter.apply("false"));
    }

    @Test
    public void testGetConverterFor_boolean() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(boolean.class);
        assertNotNull(converter);
        assertEquals(true, converter.apply("true"));
        assertEquals(false, converter.apply("false"));
    }

    @Test
    public void testGetConverterFor_Integer() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(Integer.class);
        assertNotNull(converter);
        assertEquals(new Integer(10), converter.apply("10"));
        assertEquals(new Integer(-123456789), converter.apply("-123456789"));
    }

    @Test
    public void testGetConverterFor_int() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(int.class);
        assertNotNull(converter);
        assertEquals(10, converter.apply("10"));
        assertEquals(-123456789, converter.apply("-123456789"));
    }

    @Test
    public void testGetConverterFor_Long() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(Long.class);
        assertNotNull(converter);
        assertEquals(new Long(10L), converter.apply("10"));
        assertEquals(new Long(-1234567890123456789L), converter.apply("-1234567890123456789"));
    }

    @Test
    public void testGetConverterFor_long() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(long.class);
        assertNotNull(converter);
        assertEquals(10L, converter.apply("10"));
        assertEquals(-1234567890123456789L, converter.apply("-1234567890123456789"));
    }

    @Test
    public void testGetConverterFor_Double() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(Double.class);
        assertNotNull(converter);
        assertEquals(new Double(10D), converter.apply("10"));
        assertEquals(new Double(-1234567890123456789.12345D), converter.apply("-1234567890123456789.12345"));
    }

    @Test
    public void testGetConverterFor_double() throws Exception {
        final Function<String, Object> converter = this.uut.getConverterFor(double.class);
        assertNotNull(converter);
        assertEquals(10D, converter.apply("10"));
        assertEquals(-1234567890123456789.12345D, converter.apply("-1234567890123456789.12345"));
    }

}
