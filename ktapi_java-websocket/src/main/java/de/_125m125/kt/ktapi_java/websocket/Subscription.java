package de._125m125.kt.ktapi_java.websocket;

import java.util.Objects;
import java.util.Optional;

public class Subscription {
    private final String           source;
    private final Optional<String> key;

    public Subscription(final String source, final Optional<String> key) {
        super();
        this.source = Objects.requireNonNull(source);
        this.key = key;
    }

    public String getSource() {
        return this.source;
    }

    public Optional<String> getKey() {
        return this.key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
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
        final Subscription other = (Subscription) obj;
        if (this.key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!this.key.equals(other.key)) {
            return false;
        }
        if (this.source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!this.source.equals(other.source)) {
            return false;
        }
        return true;
    }

}
