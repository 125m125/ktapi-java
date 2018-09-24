package de._125m125.kt.ktapi.retrofitRequester.builderModifier;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient.Builder;

public class CertificatePinnerAdder implements ClientModifier {

    public static CertificatePinnerAdderCertificate[] DEFAULT_CERTIFICATES = { new CertificatePinnerAdderCertificate(
            "kt.125m125.de", "sha256/ZnHPNFeZ/okzKy3UttEwn4O9V8T/qEvByGaE1FLBdq8=",
            "sha256/tcgiMrn7yiqTpt7SDna6sU0faF8m4QUiq24aQVW3F9U=",
            "sha256/Ii0NcQVclBzikUGw+iAV5+UnEmTqQDIhLZifNGM4yHY="), };

    private final CertificatePinner                   certificatePinner;

    public CertificatePinnerAdder() {
        this(CertificatePinnerAdder.DEFAULT_CERTIFICATES);
    }

    public CertificatePinnerAdder(final CertificatePinnerAdderCertificate... certificates) {
        if (certificates.length == 0 || certificates == null) {
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
            return this.value;
        }
    }
}
