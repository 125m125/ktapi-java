package de._125m125.kt.ktapi_java.core.users;

import java.io.File;

public class CertificateUserKey extends UserKey<CertificateUser> {
    private final File file;

    public CertificateUserKey(final String userId, final String path) {
        super(userId);
        this.file = new File(path);
    }

    public CertificateUserKey(final String userId, final File file) {
        super(userId);
        this.file = file;
    }

    @Override
    public String getSubIdentifier() {
        return this.file.getAbsolutePath();
    }
}
