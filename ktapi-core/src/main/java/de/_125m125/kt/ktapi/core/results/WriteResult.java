package de._125m125.kt.ktapi.core.results;

public class WriteResult<T> {
    private boolean success;
    private String  message;
    private T       data;

    public WriteResult() {
        super();
    }

    public WriteResult(final boolean success, final String message) {
        super();
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public WriteResult(final boolean success, final String message, final T object) {
        super();
        this.success = success;
        this.message = message;
        this.data = object;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public T getObject() {
        return this.data;
    }

    public boolean hasObject() {
        return this.data != null;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Result [success=");
        builder.append(this.success);
        builder.append(", message=");
        builder.append(this.message);
        builder.append(", result=");
        builder.append(this.data);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
        result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
        result = prime * result + (this.success ? 1231 : 1237);
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
        final WriteResult other = (WriteResult) obj;
        if (this.data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!this.data.equals(other.data)) {
            return false;
        }
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!this.message.equals(other.message)) {
            return false;
        }
        if (this.success != other.success) {
            return false;
        }
        return true;
    }
}
