package de._125m125.kt.ktapi_java.retrofitRequester.builderModifier;

import java.util.ArrayList;
import java.util.List;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient.Builder;

public class CertificatePinnerAdder implements ClientModifier {

    //@formatter:off
    public static CertificatePinnerAdderCertificate[] DEFAULT_CERTIFICATES = {
            new CertificatePinnerAdderCertificate("kt.125m125.de",
                    "sha256/tKgj8//UI0ceNEqmwGJ9WnSaE/z/fYs/c+CcX80mzzc=", 1531148895000L), // Jul 09 2018
            new CertificatePinnerAdderCertificate("kt.125m125.de",
                    "sha256/ZnHPNFeZ/okzKy3UttEwn4O9V8T/qEvByGaE1FLBdq8=", 1536451200000L), // Sep 09 2018
            new CertificatePinnerAdderCertificate("kt.125m125.de",
                    "sha256/vJdW/+vnegRN4ZwzWfu4EymOnj672ev7S7EWh9p6xHU=", 1536451200000L), // Sep 09 2018
    };
    //@formatter:on

    public static class CertificatePinnerAdderBuilder {
        private final List<CertificatePinnerAdderCertificate> certificatesToAdd = new ArrayList<>();
        private final boolean                                 ignoreExpired;

        public CertificatePinnerAdderBuilder(final boolean ignoreExpired) {
            this.ignoreExpired = ignoreExpired;
        }

        public CertificatePinnerAdder build() {
            return new CertificatePinnerAdder(this.certificatesToAdd
                    .toArray(new CertificatePinnerAdderCertificate[this.certificatesToAdd.size()]));
        }

        public CertificatePinnerAdderBuilder add(final CertificatePinnerAdderCertificate cert) {
            if (this.ignoreExpired || cert.useUntilMillies > System.currentTimeMillis()) {
                this.certificatesToAdd.add(cert);
            }
            return this;
        }

        public CertificatePinnerAdderBuilder addAll(final CertificatePinnerAdderCertificate... certs) {
            for (final CertificatePinnerAdderCertificate cert : certs) {
                add(cert);
            }
            return this;
        }
    }

    public static CertificatePinnerAdderBuilder builder(final boolean ignoreExpired) {
        return new CertificatePinnerAdderBuilder(ignoreExpired);
    }

    private CertificatePinner certificatePinner;

    public CertificatePinnerAdder(final CertificatePinnerAdderCertificate... certificates) {
        if (certificates.length == 0) {
            this.certificatePinner = null;
            return;
        }
        final okhttp3.CertificatePinner.Builder builder = new CertificatePinner.Builder();
        for (final CertificatePinnerAdderCertificate certificate : certificates) {
            builder.add(certificate.hostname, certificate.value);
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

    public static class CertificatePinnerAdderCertificate {
        private final String hostname;
        private final String value;
        private final long   useUntilMillies;

        public CertificatePinnerAdderCertificate(final String hostname, final String value,
                final long useUntilMillies) {
            super();
            this.hostname = hostname;
            this.value = value;
            this.useUntilMillies = useUntilMillies;
        }

        public String getHostname() {
            return this.hostname;
        }

        public String getValue() {
            return this.value;
        }

        public long getUseUntilMillies() {
            return this.useUntilMillies;
        }
    }
}
