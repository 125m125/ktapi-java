package de._125m125.kt.ktapi_java.websocket.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.gson.Gson;

import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;

public class RequestMessage {
    public static RequestMessageBuilder builder() {
        return new RequestMessageBuilder();
    }

    public static class RequestMessageBuilder {

        private static final AtomicInteger nextRequestId = new AtomicInteger(0);

        private final Map<String, Object>  content;

        private final WebsocketResult      result;

        public RequestMessageBuilder(final RequestMessage r) {
            this.content = new HashMap<>(r.content);
            this.result = r.result;
        }

        public RequestMessageBuilder() {
            this.content = new HashMap<>();
            this.result = new WebsocketResult();
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

        public RequestMessageBuilder addResponseCallback(final Consumer<ResponseMessage> responseReceiver) {
            expectResponse();
            this.result.addCallback(responseReceiver);
            return this;
        }

        public RequestMessageBuilder expectResponse() {
            this.content.computeIfAbsent("rid", k -> RequestMessageBuilder.nextRequestId.getAndIncrement());
            return this;
        }

        public RequestMessage build() {
            return new RequestMessage(this.content, this.result);
        }
    }

    private final Map<String, Object> content;
    private final WebsocketResult     result;

    protected RequestMessage(final Map<String, Object> content, final WebsocketResult result) {
        this.content = content;
        this.result = result;
    }

    public String getMessage() {
        return new Gson().toJson(this.content);
    }

    public Optional<Integer> getRequestId() {
        return Optional.ofNullable((Integer) this.content.get("rid"));
    }

    public WebsocketResult getResult() {
        return this.result;
    }

    public boolean hasContent(final String key) {
        return this.content.containsKey(key);
    }

}
