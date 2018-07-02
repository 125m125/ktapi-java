package de._125m125.kt.ktapi_java.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de._125m125.kt.ktapi_java.core.entities.User;
import de._125m125.kt.ktapi_java.core.entities.UserKey;

public class KtUserStore<T extends User> {
    private final Map<UserKey, T> users;

    @SafeVarargs
    public KtUserStore(final T... initialUsers) {
        this.users = new ConcurrentHashMap<>();
        for (final T user : initialUsers) {
            add(user);
        }
    }

    public T add(final T user) {
        return this.users.put(UserKey.of(user), user);
    }

    public T get(final UserKey key) {
        return this.users.get(UserKey.of(key));
    }

    public boolean contains(final UserKey key) {
        return this.users.containsKey(UserKey.of(key));
    }
}
