package de._125m125.kt.ktapi_java.core.results;

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
}
