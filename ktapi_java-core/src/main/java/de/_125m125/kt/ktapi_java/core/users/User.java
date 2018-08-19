package de._125m125.kt.ktapi_java.core.users;

public abstract class User<T extends User<T>> {
    private final String userId;

    public User(final String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public abstract UserKey<T> getKey();
}
