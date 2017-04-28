package de._125m125.kt.ktapi_java.simple.authenticator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Before;
import org.junit.Test;

import de._125m125.kt.ktapi_java.core.objects.User;

public class BasicAuthAuthenticatorTest {

    private BasicAuthAuthenticator uut;

    @Before
    public void beforeURLParamAuthenticatorTest() {
        this.uut = new BasicAuthAuthenticator(new User("1", "2", "4"));
    }

    @Test
    public void testAfterConnectionCreation() throws Exception {
        final HttpsURLConnection con = mock(HttpsURLConnection.class);

        this.uut.afterConnectionCreation(con);

        verify(con).setRequestProperty("Authorization", "Basic Mjo0");
    }

    @Test
    public void testBeforeUrlBuilding() throws Exception {
        final Map<String, String> map = new HashMap<>();

        this.uut.beforeUrlBuilding("GET", "test", map);

        assertEquals("1", map.get("uid"));
    }

}
