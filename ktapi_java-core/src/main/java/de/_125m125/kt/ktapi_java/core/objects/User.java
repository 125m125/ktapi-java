package de._125m125.kt.ktapi_java.core.objects;

public class User {
    private final String uid;
    private final String tid;
    private final String tkn;

    public User(final String userId, final String tokenId, final String token) {
        this.uid = userId;
        this.tid = tokenId;
        this.tkn = token;
    }

    public String getUID() {
        return this.uid;
    }

    public String getTID() {
        return this.tid;
    }

    public String getTKN() {
        return this.tkn;
    }
}
