package de._125m125.kt.ktapi_java.core.results;

public interface Callback<T> {
    public void onSuccess(int status, T result);

    public void onFailure(int status, String message, String humanReadableMessage);

    public void onError(Throwable t);
}