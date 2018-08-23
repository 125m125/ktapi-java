package de._125m125.kt.ktapi_java.core.users;

public class IdUserKey extends UserKey<IdUser> {
    public IdUserKey(final String userId) {
        super(userId);
    }

    @Override
    public String getSubIdentifier() {
        return "";
    }

}
