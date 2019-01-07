package de._125m125.kt.ktapi.core.users;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KtUserStore {
    private final Map<String, User> users;

    public KtUserStore(final User... initialUsers) {
        this.users = new ConcurrentHashMap<>();
        for (final User user : initialUsers) {
            add(user);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends User> T add(final T user) {
        return (T) this.users.put(user.getKey().getIdentifier(), user);
    }

    public User get(final UserKey key) {
        if (key == null) {
            return null;
        }
        return this.users.get(key.getIdentifier());
    }

    @SuppressWarnings("unchecked")
    public <T extends User> T get(final UserKey key, final Class<T> type) {
        final User user = this.users.get(key.getIdentifier());
        if (user != null && type.isAssignableFrom(user.getClass())) {
            return (T) user;
        }
        if (key instanceof IdUserKey) {
            final String userId = key.getUserId();
            return getFromUserId(userId, type);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends User> T get(final String key, final Class<T> type) {
        final User user = this.users.get(key);
        if (user != null && type.isAssignableFrom(user.getClass())) {
            return (T) user;
        }
        if (key.startsWith(IdUser.class.getTypeName())) {
            final String userId = key.substring(IdUser.class.getTypeName().length() + 1,
                    key.length() - 1);
            return getFromUserId(userId, type);
        }
        return null;
    }

    public <T extends User> T get(final IdUser idUser, final Class<T> type) {
        return getFromUserId(idUser.getUserId(), type);
    }

    @SuppressWarnings("unchecked")
    public <T extends User> T getFromUserId(final String userId, final Class<T> type) {
        return (T) this.users.values().stream().filter(u -> userId.equals(u.getUserId()))
                .filter(u -> type.isAssignableFrom(u.getClass())).findAny().orElse(null);
    }
}
