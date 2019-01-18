package de._125m125.kt.ktapi.core.users;

import java.io.File;
import java.util.Arrays;

public class CertificateUser extends User {
    private final File   file;
    private final char[] password;

    public CertificateUser(final String userId, final String path, final char[] password) {
        this(userId, new File(path), password);
    }

    public CertificateUser(final String userId, final File file, final char[] password) {
        super(userId);
        this.file = file;
        this.password = Arrays.copyOf(password, password.length);
    }

    public String getPath() {
        return this.file.getAbsolutePath();
    }

    public File getFile() {
        return this.file;
    }

    public char[] getPassword() {
        return Arrays.copyOf(this.password, this.password.length);
    }

    @Override
    public CertificateUserKey getKey() {
        return new CertificateUserKey(getUserId(), this.file.getAbsolutePath());
    }

}
