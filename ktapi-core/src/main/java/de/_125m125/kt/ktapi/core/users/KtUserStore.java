package de._125m125.kt.ktapi.core.users;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KtUserStore {
    private final Map<String, User<?>> users;

    public KtUserStore(final User<?>... initialUsers) {
        this.users = new ConcurrentHashMap<>();
        for (final User<?> user : initialUsers) {
            add(user);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends User<T>> T add(final User<T> user) {
        return (T) this.users.put(user.getKey().getIdentifier(), user);
    }

    @SuppressWarnings("unchecked")
    public <T extends User<T>> T get(final UserKey<T> key) {
        return (T) this.users.get(key.getIdentifier());
    }

    @SuppressWarnings("unchecked")
    public <T extends User<T>> T get(final UserKey<?> key, final Class<? extends UserKey<T>> type) {
        if (type.isAssignableFrom(key.getClass())) {
            final User<?> user = this.users.get(key.getIdentifier());
            if (user != null) {
                return (T) user;
            }
        }
        if (key instanceof IdUserKey) {
            final String userId = key.getUserId();
            return getFromUserId(userId, type);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends User<T>> T get(final String key, final Class<? extends UserKey<T>> type) {
        if (UserKey.getUserType(key).map(type::isAssignableFrom).orElse(false)) {
            final User<?> user = this.users.get(key);
            if (user != null) {
                return (T) user;
            }
        }
        if (key.startsWith(IdUserKey.class.getTypeName())) {
            final String userId = key.substring(IdUserKey.class.getTypeName().length() + 1, key.length() - 1);
            return getFromUserId(userId, type);
        }
        return null;
    }

    public <T extends User<T>> T get(final IdUser idUser, final Class<? extends UserKey<T>> type) {
        return getFromUserId(idUser.getUserId(), type);
    }

    @SuppressWarnings("unchecked")
    public <T extends User<T>> T getFromUserId(final String userId, final Class<? extends UserKey<T>> type) {
        return (T) this.users.values().stream().filter(u -> userId.equals(u.getUserId()))
                .filter(u -> type.isAssignableFrom(u.getKey().getClass())).findAny().orElse(null);
    }
}
