package de._125m125.kt.ktapi.core.users;

import java.io.File;

public class CertificateUserKey extends UserKey {
    private final File file;

    public CertificateUserKey(final String userId, final String path) {
        this(userId, new File(path));
    }

    public CertificateUserKey(final String userId, final File file) {
        this(userId, file, CertificateUser.class);
    }

    protected CertificateUserKey(final String userId, final File file,
            final Class<? extends CertificateUser> clazz) {
        super(userId, clazz);
        this.file = file;
    }

    @Override
    public String getSubIdentifier() {
        return this.file.getAbsolutePath();
    }
}
