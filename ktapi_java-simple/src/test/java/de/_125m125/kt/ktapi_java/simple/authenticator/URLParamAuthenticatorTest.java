package de._125m125.kt.ktapi_java.simple.authenticator;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import de._125m125.kt.ktapi_java.core.objects.User;

@RunWith(PowerMockRunner.class)
@PrepareForTest(URLParamAuthenticator.class)
@PowerMockIgnore("javax.crypto.*")
public class URLParamAuthenticatorTest {

    private URLParamAuthenticator uut;
    private Clock                 clock;

    @Before
    public void beforeURLParamAuthenticatorTest() {
        this.clock = Clock.fixed(Instant.parse("2017-01-01T00:00:00.00Z"), ZoneId.of("Z"));
        this.uut = spy(new URLParamAuthenticator(new User("1", "2", "4"), this.clock));
    }

    @Test
    public void testBeforeUrlBuilding() throws Exception {
        final Map<String, String> map = new HashMap<>();
        PowerMockito.when(this.uut, "getCurrentTimestamp").thenReturn(10000000L);

        this.uut.beforeUrlBuilding("GET", "test", map);

        assertEquals("1", map.get("uid"));
        assertEquals("2", map.get("tid"));
        assertEquals("10000000", map.get("timestamp"));
    }

    @Test
    public void testAfterUrlBuilding() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder("uid=1&tid=2&");

        this.uut.afterUrlBuilding(stringBuilder);

        assertEquals("uid=1&tid=2&signature=cef572058d76ba02eb7e8d36ec5be1f2449852ca058f5d418e0b6ce57656385a&",
                stringBuilder.toString());
    }

    @Test
    public void testGetCurrentTimestamp_noSync() throws Exception {
        final long result = Whitebox.invokeMethod(this.uut, "getCurrentTimestamp");

        assertEquals(this.clock.millis(), result);
    }

    @Test
    public void testGetCurrentTimestamp_minValue() throws Exception {
        Whitebox.setInternalState(this.uut, "lastTime", Long.MIN_VALUE);

        final long result = Whitebox.invokeMethod(this.uut, "getCurrentTimestamp");

        assertEquals(this.clock.millis() + 4 * 60 * 1000, result);
    }

    @Test
    public void testGetCurrentTimestamp_inside() throws Exception {
        Whitebox.setInternalState(this.uut, "lastTime", this.clock.millis() - 10000);

        final long result = Whitebox.invokeMethod(this.uut, "getCurrentTimestamp");

        assertEquals(this.clock.millis() - 10000, result);
    }

    @Test
    public void testGetCurrentTimestamp_offset() throws Exception {
        Whitebox.setInternalState(this.uut, "lastTime", Long.MIN_VALUE);
        Whitebox.setInternalState(this.uut, "timeOffset", 10000);

        final long result = Whitebox.invokeMethod(this.uut, "getCurrentTimestamp");

        assertEquals(this.clock.millis() + 4 * 60 * 1000 + 10000, result);
    }

}
