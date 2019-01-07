package de._125m125.kt.ktapi.core.users;

public class IdUserKey extends UserKey {
    public IdUserKey(final String userId) {
        super(userId, IdUser.class);
    }

    @Override
    public String getSubIdentifier() {
        return "";
    }

}
