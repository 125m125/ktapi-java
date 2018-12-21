package de._125m125.kt.ktapi.core.users;

public class IdUser extends User {
    public IdUser(final String userId) {
        super(userId);
    }

    @Override
    public UserKey getKey() {
        return new IdUserKey(getUserId());
    }

}
