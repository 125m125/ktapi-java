package de._125m125.kt.ktapi_java.core.users;

public class TokenUserKey extends UserKey<TokenUser> {
    private final String tid;

    public TokenUserKey(final String uid, final String tid) {
        super(uid);
        this.tid = tid;
    }

    public String getTid() {
        return this.tid;
    }

    @Override
    public String getSubIdentifier() {
        return this.tid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.tid == null) ? 0 : this.tid.hashCode());
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
        final TokenUserKey other = (TokenUserKey) obj;
        if (this.tid == null) {
            if (other.tid != null) {
                return false;
            }
        } else if (!this.tid.equals(other.tid)) {
            return false;
        }
        return true;
    }

}