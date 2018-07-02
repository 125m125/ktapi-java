package de._125m125.kt.ktapi_java.core.entities;

public class User extends UserKey {
    private final String tkn;

    public User(final String userId, final String tokenId, final String token) {
        super(userId, tokenId);
        this.tkn = token;
    }

    public String getTkn() {
        return this.tkn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.tkn == null) ? 0 : this.tkn.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.tkn == null) {
            if (other.tkn != null) {
                return false;
            }
        } else if (!this.tkn.equals(other.tkn)) {
            return false;
        }
        return true;
    }

}
