package de._125m125.kt.ktapi.core.users;

public abstract class User<T extends User<T>> {
    private final String userId;

    public User(final String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public abstract UserKey<T> getKey();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
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
        @SuppressWarnings("rawtypes")
        final User other = (User) obj;
        if (this.userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else if (!this.userId.equals(other.userId)) {
            return false;
        }
        return true;
    }
}
