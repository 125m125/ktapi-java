package de._125m125.kt.ktapi_java.websocket;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import de._125m125.kt.ktapi_java.websocket.events.AfterMessageSendEvent;
import de._125m125.kt.ktapi_java.websocket.events.BeforeMessageSendEvent;
import de._125m125.kt.ktapi_java.websocket.events.CancelableWebsocketEvent;
import de._125m125.kt.ktapi_java.websocket.events.CancelableWebsocketEvent.CancelState;
import de._125m125.kt.ktapi_java.websocket.events.MessageDeliveryFailedEvent;
import de._125m125.kt.ktapi_java.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi_java.websocket.events.UnparsableMessageEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketDisconnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketStartedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketStatus;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketStoppedEvent;
import de._125m125.kt.ktapi_java.websocket.exceptions.MessageCancelException;
import de._125m125.kt.ktapi_java.websocket.exceptions.MessageSendException;
import de._125m125.kt.ktapi_java.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi_java.websocket.requests.WebsocketResult;
import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi_java.websocket.responses.parsers.NotificationParser;
import de._125m125.kt.ktapi_java.websocket.responses.parsers.ResponseMessageParser;
import de._125m125.kt.ktapi_java.websocket.responses.parsers.SessionMessageParser;
import de._125m125.kt.ktapi_java.websocket.responses.parsers.WebsocketMessageParser;

public class KtWebsocketManager implements Closeable {
    public static Builder builder(final KtWebsocket websocket) {
        return new Builder(websocket);
    }

    public static class Builder {
        private final KtWebsocket                                                  websocket;
        private final Map<Class<? extends WebsocketEvent>, List<Consumer<Object>>> listeners = new HashMap<>();
        private final List<WebsocketMessageParser<?>>                              parsers   = new ArrayList<>();

        public Builder(final KtWebsocket websocket) {
            this.websocket = websocket;
        }

        public <T extends WebsocketEvent> Builder addListener(final Class<T> clazz,
                final Consumer<? super T> consumer) {
            this.listeners.computeIfAbsent(clazz, c -> new ArrayList<>()).add(o -> consumer.accept(clazz.cast(o)));
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder addListener(final Object listener) {
            final Method[] methods = listener.getClass().getMethods();
            for (final Method m : methods) {
                if (m.getAnnotation(WebsocketEventListening.class) == null) {
                    continue;
                }
                final Parameter[] parameters = m.getParameters();
                if (parameters.length != 1) {
                    throw new IllegalArgumentException("Method " + m.getName() + " should have exactly one argument");
                }
                final Parameter p = parameters[0];
                if (!WebsocketEvent.class.isAssignableFrom(p.getType())) {
                    throw new IllegalArgumentException("The argument for " + listener.getClass().getName() + "#"
                            + m.getName() + " does not extend WebsocketEvent");
                }
                addListener((Class<? extends WebsocketEvent>) p.getType(), t -> {
                    try {
                        // System.out.println(listener + "->" + t);
                        m.invoke(listener, t);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (final InvocationTargetException e) {
                        final Throwable cause = e.getCause();
                        if (e.getCause() instanceof RuntimeException) {
                            throw (RuntimeException) e.getCause();
                        }
                        throw new RuntimeException(cause);
                    }
                });
            }
            return this;
        }

        public Builder addDefaultParsers() {
            addParser(new NotificationParser());
            addParser(new SessionMessageParser());
            addParser(new ResponseMessageParser());
            return this;
        }

        public <T> Builder addParser(final WebsocketMessageParser<T> parser) {
            this.parsers.add(parser);
            return this;
        }

        public KtWebsocketManager build() {
            final KtWebsocketManager manager = new KtWebsocketManager(this.listeners, this.parsers, this.websocket);
            this.websocket.setManager(manager);
            manager.fireEvent(new WebsocketManagerCreatedEvent(manager));
            return manager;
        }

        public KtWebsocketManager buildAndOpen() {
            final KtWebsocketManager build = build();
            build.open();
            return build;
        }
    }

    private final Map<Class<? extends WebsocketEvent>, List<Consumer<Object>>> listeners;
    private final List<WebsocketMessageParser<?>>                              parsers;
    private final KtWebsocket                                                  websocket;

    protected volatile boolean                                                 active           = false;
    protected volatile boolean                                                 connected        = false;

    private final Map<Integer, RequestMessage>                                 awaitedResponses = new ConcurrentHashMap<>();

    public KtWebsocketManager(final Map<Class<? extends WebsocketEvent>, List<Consumer<Object>>> listeners,
            final List<WebsocketMessageParser<?>> parsers, final KtWebsocket websocket) {
        super();
        this.listeners = listeners;
        this.parsers = parsers;
        this.websocket = websocket;
    }

    public <T extends WebsocketEvent> void fireEvent(final T e) {
        final List<Consumer<Object>> consumers = this.listeners.get(e.getClass());
        if (consumers == null) {
            return;
        }
        for (final Consumer<Object> consumer : consumers) {
            if (e instanceof CancelableWebsocketEvent && ((CancelableWebsocketEvent) e).isCancelled()) {
                break;
            }
            consumer.accept(e);
        }
    }

    public void sendMessage(final RequestMessage requestMessage) throws MessageSendException {
        // notify observers about attempted message sending
        final BeforeMessageSendEvent bmse = new BeforeMessageSendEvent(generateStatus(), requestMessage);
        fireEvent(bmse);
        if (bmse.isCancelled()) {
            if (bmse.getCancelState() == CancelState.HARD) {
                throw new MessageCancelException(bmse.getCancelReason());
            }
            return;
        }
        // remember id and consumer, if message expects a response
        requestMessage.getRequestId().ifPresent(rid -> this.awaitedResponses.put(rid, requestMessage));
        try {
            this.websocket.sendMessage(requestMessage.getMessage());
        } catch (final IOException e) {
            // notify observers about failed message sending
            final MessageDeliveryFailedEvent mdfe = new MessageDeliveryFailedEvent(generateStatus(), requestMessage, e);
            fireEvent(mdfe);
            // if a listener handled the exception, we don't have to forward it
            if (mdfe.isCancelled()) {
                return;
            }
            // notify caller or callback about failure
            requestMessage.getResult().setResponse(new ResponseMessage("message delivery failed", e));
            requestMessage.getRequestId().ifPresent(this.awaitedResponses::remove);
            return;
        }
        // notify observers after message was sent successfully
        fireEvent(new AfterMessageSendEvent(generateStatus(), requestMessage));
    }

    public void sendRequest(final RequestMessage requestMessage) throws MessageSendException {
        if (requestMessage.getRequestId().isPresent()) {
            sendMessage(requestMessage);
        } else {
            sendMessage(new RequestMessage.RequestMessageBuilder(requestMessage).expectResponse().build());
        }
    }

    public void receiveMessage(final String rawMessage) {
        final Optional<JsonObject> json = tryParse(rawMessage);
        final Optional<WebsocketMessageParser<?>> parser = this.parsers.stream().filter(p -> p.parses(rawMessage, json))
                .findFirst();
        if (parser.isPresent()) {
            final Object parsedResponse = parser.get().parse(rawMessage, json);
            if (parsedResponse instanceof ResponseMessage) {
                final ResponseMessage responseMessage = (ResponseMessage) parsedResponse;
                responseMessage.getRequestId().map(this.awaitedResponses::remove).map(RequestMessage::getResult)
                        .filter(r -> !r.isDone()).ifPresent(r -> r.setResponse(responseMessage));
            }
            fireEvent(new MessageReceivedEvent(generateStatus(), parsedResponse));
        } else {
            fireEvent(new UnparsableMessageEvent(generateStatus(), rawMessage, json));
        }
    }

    public void websocketDisconnected() {
        this.connected = false;
        cancelAwaitedResponses();
        fireEvent(new WebsocketDisconnectedEvent(generateStatus()));
    }

    public void websocketConnected() {
        this.connected = true;
        fireEvent(new WebsocketConnectedEvent(generateStatus()));
    }

    @Override
    public void close() {
        stop();
    }

    public void stop() {
        this.connected = false;
        this.active = false;
        this.websocket.close();
        fireEvent(new WebsocketStoppedEvent(generateStatus()));
    }

    public void open() {
        if (this.active) {
            return;
        }
        this.active = true;
        fireEvent(new WebsocketStartedEvent(generateStatus()));
        connect();
    }

    public void connect() {
        if (!this.active) {
            throw new IllegalStateException("cannot connect websocket while inactive");
        }
        this.websocket.connect();
    }

    private void cancelAwaitedResponses() {
        final Iterator<Entry<Integer, RequestMessage>> iterator = this.awaitedResponses.entrySet().iterator();
        while (iterator.hasNext()) {
            final WebsocketResult result = iterator.next().getValue().getResult();
            if (!result.isDone()) {
                result.setResponse(new ResponseMessage("websocket closed", null));
            }
            iterator.remove();
        }
    }

    private Optional<JsonObject> tryParse(final String rawMessage) {
        try {
            final JsonElement parse = new JsonParser().parse(rawMessage);
            if (parse instanceof JsonObject) {
                return Optional.of((JsonObject) parse);
            } else {
                return Optional.empty();
            }
        } catch (final JsonParseException e) {
            return Optional.empty();
        }
    }

    private WebsocketStatus generateStatus() {
        return new WebsocketStatus(this.active, this.connected);
    }

}
