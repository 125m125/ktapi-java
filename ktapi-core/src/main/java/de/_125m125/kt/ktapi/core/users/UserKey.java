package de._125m125.kt.ktapi.core.users;

import java.util.Optional;

public abstract class UserKey {
    private final String userId;
    private final String typeName;

    public UserKey(final String userid, final Class<? extends User> userType) {
        this.userId = userid;
        this.typeName = userType.getTypeName() + ":" + getUserId();
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
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        return ((UserKey) o).getIdentifier().equals(getIdentifier());
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    public String getUserId() {
        return this.userId;
    }
}
