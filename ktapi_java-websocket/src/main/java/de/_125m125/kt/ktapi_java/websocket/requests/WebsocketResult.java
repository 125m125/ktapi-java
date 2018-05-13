package de._125m125.kt.ktapi_java.websocket.requests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;

public class WebsocketResult {
    private final CompletableFuture<ResponseMessage> result = new CompletableFuture<>();

    public WebsocketResult() {

    }

    public ResponseMessage get() throws InterruptedException {
        try {
            return this.result.get();
        } catch (final ExecutionException e) {
            return new ResponseMessage("retrieval of result failed", e);
        }
    }

    public ResponseMessage get(final long maxWait, final TimeUnit unit) throws InterruptedException, TimeoutException {
        try {
            return this.result.get(maxWait, unit);
        } catch (final ExecutionException e) {
            return new ResponseMessage("retrieval of result failed", e);
        }
    }

    public synchronized void addCallback(final Consumer<ResponseMessage> consumer) {
        if (this.result.isDone()) {
            try {
                consumer.accept(this.result.get());
            } catch (InterruptedException | ExecutionException e) {
                consumer.accept(new ResponseMessage("retrieval of result failed", e));
            }
        }
        new Thread(() -> this.result.thenAccept(consumer::accept));
    }

    public synchronized void setResponse(final ResponseMessage responseMessage) {
        this.result.complete(responseMessage);
    }

    public synchronized boolean isDone() {
        return this.result.isDone();
    }
}
