package de._125m125.kt.ktapi_java.pinningRequester;

public class CertificateLoadingException extends RuntimeException {

    public CertificateLoadingException() {
        super();
    }

    public CertificateLoadingException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CertificateLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CertificateLoadingException(final String message) {
        super(message);
    }

    public CertificateLoadingException(final Throwable cause) {
        super(cause);
    }

}
