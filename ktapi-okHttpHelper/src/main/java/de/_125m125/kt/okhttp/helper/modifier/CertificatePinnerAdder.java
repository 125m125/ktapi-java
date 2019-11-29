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

import java.util.Arrays;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient.Builder;

public class CertificatePinnerAdder implements ClientModifier {

    private static final CertificatePinnerAdderCertificate[] DEFAULT_CERTIFICATES = {
            new CertificatePinnerAdderCertificate("kt.125m125.de",
                    "sha256/ZnHPNFeZ/okzKy3UttEwn4O9V8T/qEvByGaE1FLBdq8=",
                    "sha256/tcgiMrn7yiqTpt7SDna6sU0faF8m4QUiq24aQVW3F9U=",
                    "sha256/Ii0NcQVclBzikUGw+iAV5+UnEmTqQDIhLZifNGM4yHY=",
                    "sha256/BACuvvyhjQYzCbNMfqXZU8/XYBiSCQbcGqHXbrEmUKY=",
                    "sha256/47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU="), };

    private final CertificatePinner                          certificatePinner;

    public CertificatePinnerAdder() {
        this(CertificatePinnerAdder.DEFAULT_CERTIFICATES);
    }

    public CertificatePinnerAdder(final CertificatePinnerAdderCertificate... certificates) {
        if (certificates == null || certificates.length == 0) {
            throw new IllegalArgumentException(
                    "CertificatePinnerAdder requires at least contain one certificate to pin");
        }
        final okhttp3.CertificatePinner.Builder builder = new CertificatePinner.Builder();
        for (final CertificatePinnerAdderCertificate certificate : certificates) {
            builder.add(certificate.getHostname(), certificate.getValues());
        }
        this.certificatePinner = builder.build();
    }

    @Override
    public Builder modify(final Builder builder) {
        if (this.certificatePinner != null) {
            builder.certificatePinner(this.certificatePinner);
        }
        return builder;
    }

    @Override
    public boolean conflictsWith(final ClientModifier modifier) {
        return modifier instanceof CertificatePinnerAdder;
    }

    public static class CertificatePinnerAdderCertificate {
        private final String   hostname;
        private final String[] value;

        public CertificatePinnerAdderCertificate(final String hostname, final String... value) {
            super();
            this.hostname = hostname;
            this.value = value;
        }

        public String getHostname() {
            return this.hostname;
        }

        public String[] getValues() {
            return Arrays.copyOf(this.value, this.value.length);
        }
    }
}
