package de._125m125.kt.ktapi_java.simple;

import com.univocity.parsers.annotations.Parsed;

public class Result<T> {
    @Parsed
    private boolean success;
    @Parsed
    private String  message;
    @Parsed
    private T       result;

    public Result() {
        super();
    }

    public Result(final boolean success, final String message) {
        super();
        this.success = success;
        this.message = message;
        this.result = null;
    }

    public Result(final boolean success, final String message, final T object) {
        super();
        this.success = success;
        this.message = message;
        this.result = object;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public T getObject() {
        return this.result;
    }

    public boolean hasObject() {
        return this.result != null;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Result [success=");
        builder.append(this.success);
        builder.append(", message=");
        builder.append(this.message);
        builder.append(", result=");
        builder.append(this.result);
        builder.append("]");
        return builder.toString();
    }
}
