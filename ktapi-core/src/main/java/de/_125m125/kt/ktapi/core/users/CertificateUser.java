package de._125m125.kt.ktapi.core.users;

import java.io.File;

public class CertificateUser extends User {
    private final File   file;
    private final char[] password;

    public CertificateUser(final String userId, final String path, final char[] password) {
        super(userId);
        this.file = new File(path);
        this.password = password;
    }

    public CertificateUser(final String userId, final File file, final char[] password) {
        super(userId);
        this.file = file;
        this.password = password;
    }

    public String getPath() {
        return this.file.getAbsolutePath();
    }

    public File getFile() {
        return this.file;
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override
    public CertificateUserKey getKey() {
        return new CertificateUserKey(getUserId(), this.file.getAbsolutePath());
    }

}
