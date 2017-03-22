package de._125m125.kt.ktapi_java.pinningRequester;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.simple.KtRequesterImpl;

public class KtPinningRequester extends KtRequesterImpl implements KtRequester {

    private final SSLContext sslContext;

    public KtPinningRequester(final User user, final InputStream certificateStream) {
        super(user);
        try {
            this.sslContext = createSSLContext(certificateStream);
        } catch (final Exception e) {
            throw new CertificateLoadingException(e);
        }
    }

    public KtPinningRequester(final User user) {
        super(user);
        try (InputStream resourceAsStream = KtPinningRequester.class
                .getResourceAsStream("kt.125m125.de-certificate.crt")) {
            this.sslContext = createSSLContext(resourceAsStream);
        } catch (final Exception e) {
            throw new CertificateLoadingException(e);
        }
    }

    private SSLContext createSSLContext(final InputStream is) throws NoSuchAlgorithmException, CertificateException,
            KeyStoreException, IOException, KeyManagementException {
        // source:
        // https://www.owasp.org/index.php/Certificate_and_Public_Key_Pinning#Android

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        ca = cf.generateCertificate(is);

        // Create a KeyStore containing our trusted CAs
        final String keyStoreType = KeyStore.getDefaultType();
        final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        final String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        // SSLContext context = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }

    @Override
    public HttpsURLConnection createConnection(final String urlString) throws MalformedURLException, IOException {
        final URL url = new URL(urlString);
        final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(this.sslContext.getSocketFactory());
        return urlConnection;
    }

}
