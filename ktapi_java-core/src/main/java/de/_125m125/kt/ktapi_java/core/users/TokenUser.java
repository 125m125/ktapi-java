package de._125m125.kt.ktapi_java.core.users;

public class TokenUser extends User<TokenUser> {

    private final String tokenId;
    private final String token;

    public TokenUser(final String userId, final String tokenId, final String token) {
        super(userId);
        this.tokenId = tokenId;
        this.token = token;
    }

    @Override
    public TokenUserKey getKey() {
        return new TokenUserKey(getUserId(), this.tokenId);
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public String getToken() {
        return this.token;
    }

}
