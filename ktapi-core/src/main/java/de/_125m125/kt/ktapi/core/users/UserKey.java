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

import java.util.Objects;
import java.util.Optional;

public abstract class UserKey {
    private final String userId;
    private final String typeName;

    public UserKey(final String userid, final Class<? extends User> userType) {
        this.userId = Objects.requireNonNull(userid);
        this.typeName = userType.getTypeName() + ":" + this.userId;
    }

    @SuppressWarnings("unchecked")
    public static Optional<Class<? extends User>> getUserType(final String identifier) {
        final String className = identifier.substring(0, identifier.indexOf(':'));
        try {
            final Class<?> clazz = Class.forName(className);
            if (User.class.isAssignableFrom(clazz)) {
                return Optional.of((Class<? extends User>) clazz);
            }
            return Optional.empty();
        } catch (final ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public final String getIdentifier() {
        return this.typeName + ":" + getSubIdentifier();
    }

    public abstract String getSubIdentifier();

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserKey other = (UserKey) obj;
        return getIdentifier().equals(other.getIdentifier());
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    public String getUserId() {
        return this.userId;
    }
}
