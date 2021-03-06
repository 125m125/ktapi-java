/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.okhttp.helper.modifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Optional;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient.Builder;

public class ClientCertificateAdder implements ClientModifier {

    public static ClientModifier createUnchecked(final File pkcs12File, final char[] filePassword) {
        try {
            return new ClientCertificateAdder(pkcs12File, filePassword);
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (IOException | UnrecoverableKeyException | CertificateException e) {
            throw new IllegalArgumentException("Invalid Certificate");
        }
    }

    public static ClientModifier createUnchecked(final File pkcs12File, final char[] filePassword,
            final X509TrustManager trustManager) {
        try {
            return new ClientCertificateAdder(pkcs12File, filePassword, trustManager);
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (IOException | UnrecoverableKeyException | CertificateException e) {
            throw new IllegalArgumentException("Invalid Certificate");
        }
    }

    private static final Optional<X509TrustManager> DEFAULT_TRUST_MANAGER;

    private final SSLSocketFactory                  socketFactory;
    private final X509TrustManager                  trustManager;

    static {
        Optional<X509TrustManager> tmp = Optional.empty();
        try {
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            tmp = Arrays.stream(trustManagerFactory.getTrustManagers())
                    .filter(X509TrustManager.class::isInstance).map(X509TrustManager.class::cast)
                    .findAny();
        } catch (final NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        DEFAULT_TRUST_MANAGER = tmp;
    }

    public ClientCertificateAdder(final File pkcs12File, final char[] password)
            throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, IOException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {
        if (!ClientCertificateAdder.DEFAULT_TRUST_MANAGER.isPresent()) {
            throw new IllegalStateException("no default trust manager is present.");
        }
        this.trustManager = ClientCertificateAdder.DEFAULT_TRUST_MANAGER.get();
        this.socketFactory = createSocketFactory(pkcs12File, password);
    }

    public ClientCertificateAdder(final File pkcs12File, final char[] password,
            final X509TrustManager trustmanager) throws NoSuchAlgorithmException, KeyStoreException,
            IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        this.trustManager = trustmanager;
        this.socketFactory = createSocketFactory(pkcs12File, password);
    }

    private SSLSocketFactory createSocketFactory(final File pkcs12File, final char[] password)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream keyInput = new FileInputStream(pkcs12File)) {
            keyStore.load(keyInput, password);
        }

        keyManagerFactory.init(keyStore, password);

        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { this.trustManager },
                new SecureRandom());
        return context.getSocketFactory();
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.sslSocketFactory(this.socketFactory, this.trustManager);
    }

    @Override
    public boolean conflictsWith(final ClientModifier modifier) {
        return modifier instanceof ClientCertificateAdder;
    }
}
