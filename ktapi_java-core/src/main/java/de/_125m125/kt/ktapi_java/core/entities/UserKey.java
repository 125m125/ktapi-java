package de._125m125.kt.ktapi_java.core.entities;

public class UserKey {
    public static UserKey of(final UserKey userKey) {
        if (userKey.getClass() == UserKey.class) {
            return userKey;
        }
        return new UserKey(userKey.getUid(), userKey.getTid());
    }

    public static String toString(final UserKey userKey) {
        return userKey.getUid() + "#" + userKey.getTid();
    }

    public static UserKey fromString(final String s) {
        final String[] split = s.split("#");
        if (split.length != 2) {
            throw new IllegalArgumentException("unparsable string representation");
        }
        return new UserKey(split[0], split[1]);
    }

    private final String uid;
    private final String tid;

    public UserKey(final String uid, final String tid) {
        this.uid = uid;
        this.tid = tid;
    }

    public UserKey(final UserKey userKey) {
        this(userKey.getUid(), userKey.getTid());
    }

    public String getUid() {
        return this.uid;
    }

    public String getTid() {
        return this.tid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.tid == null) ? 0 : this.tid.hashCode());
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
        final UserKey other = (UserKey) obj;
        if (this.tid == null) {
            if (other.tid != null) {
                return false;
            }
        } else if (!this.tid.equals(other.tid)) {
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