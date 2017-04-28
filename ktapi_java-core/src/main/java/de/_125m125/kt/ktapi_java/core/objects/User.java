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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.tid == null) ? 0 : this.tid.hashCode());
        result = prime * result + ((this.tkn == null) ? 0 : this.tkn.hashCode());
        result = prime * result + ((this.uid == null) ? 0 : this.uid.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.tid == null) {
            if (other.tid != null) {
                return false;
            }
        } else if (!this.tid.equals(other.tid)) {
            return false;
        }
        if (this.tkn == null) {
            if (other.tkn != null) {
                return false;
            }
        } else if (!this.tkn.equals(other.tkn)) {
            return false;
        }
        if (this.uid == null) {
            if (other.uid != null) {
                return false;
            }
        } else if (!this.uid.equals(other.uid)) {
            return false;
        }
        return true;
    }
}
