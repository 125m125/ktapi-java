package de._125m125.kt.ktapi_java.websocket.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.gson.Gson;

import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;

public class RequestMessage {
    public static class RequestMessageBuilder {

        private static final AtomicInteger nextRequestId = new AtomicInteger(0);

        private final Map<String, Object>  content;

        private Consumer<ResponseMessage>  responseReceiver;

        public RequestMessageBuilder(final RequestMessage r) {
            this.content = new HashMap<>(r.content);
        }

        public RequestMessageBuilder() {
            this.content = new HashMap<>();
        }

        public RequestMessageBuilder addContent(final String key, final Object value) {
            if ("rid".equals(key) && !(value instanceof Integer)) {
                throw new IllegalArgumentException("The request id has to be an integer");
            }
            this.content.put(key, value);
            return this;
        }

        public RequestMessageBuilder addContent(final SessionRequestData sessionRequest) {
            addContent("session", sessionRequest);
            return this;
        }

        public RequestMessageBuilder addContent(final SubscriptionRequestData subscriptionRequest) {
            addContent("subscribe", subscriptionRequest);
            return this;
        }

        public RequestMessageBuilder addPing() {
            addContent("ping", "ping");
            return this;
        }

        public RequestMessageBuilder setResponseReceiver(final Consumer<ResponseMessage> responseReceiver) {
            this.responseReceiver = responseReceiver;
            this.content.computeIfAbsent("rid", k -> RequestMessageBuilder.nextRequestId.getAndIncrement());
            return this;
        }

        public RequestMessage build() {
            return new RequestMessage(this.content, Optional.of(this.responseReceiver));
        }
    }

    private final Map<String, Object>                 content;
    private final Optional<Consumer<ResponseMessage>> responseConsumer;

    protected RequestMessage(final Map<String, Object> content,
            final Optional<Consumer<ResponseMessage>> responseConsumer) {
        this.content = content;
        this.responseConsumer = responseConsumer;
    }

    public String getMessage() {
        return new Gson().toJson(this.content);
    }

    public Optional<Integer> getRequestId() {
        return this.responseConsumer.map(v -> (Integer) this.content.get("rid"));
    }

    public Optional<Consumer<ResponseMessage>> getResponseConsumer() {
        return this.responseConsumer;
    }

}
