package de._125m125.kt.ktapi.core.users;

public class IdUser extends User<IdUser> {
    public IdUser(final String userId) {
        super(userId);
    }

    @Override
    public UserKey<IdUser> getKey() {
        return new IdUserKey(getUserId());
    }

}
