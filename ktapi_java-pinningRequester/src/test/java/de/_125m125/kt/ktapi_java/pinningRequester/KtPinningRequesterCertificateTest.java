package de._125m125.kt.ktapi_java.pinningRequester;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class KtPinningRequesterCertificateTest {
    private static X509Certificate primaryCertificate;

    @BeforeClass
    public static void beforeKtPinningRequesterCertificateTest() throws Exception {
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream is = KtPinningRequester.class
                .getResourceAsStream(KtPinningRequester.DEFAULT_PRIMARY_CERTIFICATE_NAME)) {
            KtPinningRequesterCertificateTest.primaryCertificate = (X509Certificate) cf.generateCertificate(is);
        }
    }

    @Test
    public void testDefaultPrimaryCertificateNotExpired() {
        assertTrue(new Date().before(KtPinningRequesterCertificateTest.primaryCertificate.getNotAfter()));
    }

}
