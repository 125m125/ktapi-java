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
