package de._125m125.kt.ktapi.core.users;

public final class TokenUser extends AbstractTokenUser<TokenUser> {

    public TokenUser(final String userId, final String tokenId, final String token) {
        super(userId, tokenId, token);
    }

    @Override
    public TokenUserKey getKey() {
        return new TokenUserKey(getUserId(), this.tokenId);
    }
}
