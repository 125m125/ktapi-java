package de._125m125.kt.ktapi.core.users;

import java.util.Optional;

public abstract class UserKey<T extends User<T>> {
    private final String userId;

    public UserKey(final String userid) {
        this.userId = userid;
    }

    public static Optional<Class<?>> getUserType(final String identifier) {
        final String className = identifier.substring(0, identifier.indexOf(':'));
        try {
            return Optional.of(Class.forName(className));
        } catch (final ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public final String getIdentifier() {
        return this.getClass().getTypeName() + ":" + this.getUserId() + ":" + getSubIdentifier();
    }

    public abstract String getSubIdentifier();

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        return ((UserKey<?>) o).getIdentifier().equals(this.getIdentifier());
    }

    @Override
    public int hashCode() {
        return this.getIdentifier().hashCode();
    }

    public String getUserId() {
        return this.userId;
    }
}
