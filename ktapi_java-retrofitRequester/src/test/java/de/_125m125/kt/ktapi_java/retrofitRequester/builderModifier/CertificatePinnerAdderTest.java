package de._125m125.kt.ktapi_java.retrofitRequester.builderModifier;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CertificatePinnerAdderTest {
    @Test
    public void testPrimaryCertificateExpiration() {
        assertTrue("primary certificate expired",
                CertificatePinnerAdder.DEFAULT_CERTIFICATES[0].getUseUntilMillies() > System.currentTimeMillis());
    }

    @Test
    public void testFallbackCertificateExpiration() {
        assertTrue("fallback certificate expired",
                CertificatePinnerAdder.DEFAULT_CERTIFICATES[1].getUseUntilMillies() > System.currentTimeMillis());
    }
}
