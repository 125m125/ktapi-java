package de._125m125.kt.ktapi.core.users;

public class TokenUserKey extends UserKey {

    protected final String tid;

    public TokenUserKey(final String userid, final String tid) {
        this(userid, tid, TokenUser.class);
    }

    protected TokenUserKey(final String userid, final String tid,
            final Class<? extends TokenUser> clazz) {
        super(userid, clazz);
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