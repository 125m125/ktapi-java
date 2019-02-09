package de._125m125.kt.ktapi.core.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Message {
    private long   timestamp;
    private String message;

    protected Message() {
        super();
    }

    public Message(final long timestamp, final String message) {
        super();
        this.timestamp = timestamp;
        this.message = message != null ? message : "";
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Message [timestamp=");
        builder.append(this.timestamp);
        builder.append(", message=");
        builder.append(this.message);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Message other = (Message) obj;
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (timestamp != other.timestamp) {
            return false;
        }
        return true;
    }

}
