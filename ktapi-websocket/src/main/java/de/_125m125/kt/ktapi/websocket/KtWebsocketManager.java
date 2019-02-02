package de._125m125.kt.ktapi.websocket;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import de._125m125.kt.ktapi.websocket.events.AfterMessageSendEvent;
import de._125m125.kt.ktapi.websocket.events.BeforeMessageSendEvent;
import de._125m125.kt.ktapi.websocket.events.CancelableWebsocketEvent;
import de._125m125.kt.ktapi.websocket.events.CancelableWebsocketEvent.CancelState;
import de._125m125.kt.ktapi.websocket.events.MessageDeliveryFailedEvent;
import de._125m125.kt.ktapi.websocket.events.MessageReceivedEvent;
import de._125m125.kt.ktapi.websocket.events.UnparsableMessageEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketDisconnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketStartedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketStatus;
import de._125m125.kt.ktapi.websocket.events.WebsocketStoppedEvent;
import de._125m125.kt.ktapi.websocket.exceptions.MessageCancelException;
import de._125m125.kt.ktapi.websocket.exceptions.MessageSendException;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.requests.WebsocketResult;
import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi.websocket.responses.parsers.NotificationParser;
import de._125m125.kt.ktapi.websocket.responses.parsers.ResponseMessageParser;
import de._125m125.kt.ktapi.websocket.responses.parsers.SessionMessageParser;
import de._125m125.kt.ktapi.websocket.responses.parsers.WebsocketMessageParser;

public class KtWebsocketManager implements Closeable {

    private static final Logger logger       = LoggerFactory.getLogger(KtWebsocketManager.class);
    /**
     * This marker marks log messages, where the token of a user could be
     * contained in a json string.
     */
    public static final Marker  TOKEN_MARKER = MarkerFactory
            .getMarker("de.125m125.kt.ktapi.websocket.JSON-TOKEN");

    public static Builder builder(final KtWebsocket websocket) {
        return new Builder(websocket);
    }

    public static class Builder {
        private final KtWebsocket                                                  websocket;
        private final Map<Class<? extends WebsocketEvent>, List<Consumer<Object>>> listeners;
        private final List<WebsocketMessageParser<?>>                              parsers;

        public Builder(final KtWebsocket websocket) {
            this.websocket = websocket;
            listeners = new HashMap<>();
            parsers = new ArrayList<>();
        }

        public <T extends WebsocketEvent> Builder addListener(final Class<T> clazz,
                final Consumer<? super T> consumer) {
            this.listeners.computeIfAbsent(clazz, c -> new ArrayList<>())
                    .add(o -> consumer.accept(clazz.cast(o)));
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder addListener(final Object listener) {
            KtWebsocketManager.logger.info("Adding listener {}.", listener);
            final Method[] methods = listener.getClass().getMethods();
            for (final Method m : methods) {
                if (m.getAnnotation(WebsocketEventListening.class) == null) {
                    continue;
                }
                KtWebsocketManager.logger.debug("Found listening method {}.", m);
                final Parameter[] parameters = m.getParameters();
                if (parameters.length != 1) {
                    KtWebsocketManager.logger.warn("Method {} does not have exactly one argument.",
                            m);
                    throw new IllegalArgumentException(
                            "Method " + m.getName() + " should have exactly one argument");
                }
                final Parameter p = parameters[0];
                if (!WebsocketEvent.class.isAssignableFrom(p.getType())) {
                    KtWebsocketManager.logger
                            .warn("Method {} does not accept a WebsocketEvent as argument.", m);
                    throw new IllegalArgumentException(
                            "The argument for " + listener.getClass().getName() + "#" + m.getName()
                                    + " does not extend WebsocketEvent");
                }
                KtWebsocketManager.logger.debug("Adding method {} as listener for {}.", m,
                        p.getType());
                addListener((Class<? extends WebsocketEvent>) p.getType(), t -> {
                    try {
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
            KtWebsocketManager.logger.info("Adding message parser {}.", parser);
            this.parsers.add(parser);
            return this;
        }

        public KtWebsocketManager build() {
            final KtWebsocketManager manager = new KtWebsocketManager(this.listeners, this.parsers,
                    this.websocket);
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

    protected volatile boolean                                                 active;
    protected volatile boolean                                                 connected;

    private final Map<Integer, RequestMessage>                                 awaitedResponses;

    public KtWebsocketManager(
            final Map<Class<? extends WebsocketEvent>, List<Consumer<Object>>> listeners,
            final List<WebsocketMessageParser<?>> parsers, final KtWebsocket websocket) {
        super();
        this.listeners = listeners;
        this.parsers = parsers;
        this.websocket = websocket;
        active = false;
        connected = false;
        awaitedResponses = new ConcurrentHashMap<>();
    }

    public <T extends WebsocketEvent> void fireEvent(final T e) {
        final List<Consumer<Object>> consumers = this.listeners.get(e.getClass());
        KtWebsocketManager.logger.debug("publishing event {} to {}", e, consumers);
        if (consumers == null) {
            return;
        }
        for (final Consumer<Object> consumer : consumers) {
            if (e instanceof CancelableWebsocketEvent
                    && ((CancelableWebsocketEvent) e).isCancelled()) {
                KtWebsocketManager.logger.trace("publishing of event {} was cancelled.", e);
                break;
            }
            KtWebsocketManager.logger.trace("pushing event {} to {}", e, consumer);
            consumer.accept(e);
        }
    }

    public void sendMessage(final RequestMessage requestMessage) throws MessageSendException {
        final String message = requestMessage.getMessage();
        KtWebsocketManager.logger.trace(KtWebsocketManager.TOKEN_MARKER,
                "preparing to send message [{}]", message);
        // notify observers about attempted message sending
        final BeforeMessageSendEvent bmse = new BeforeMessageSendEvent(generateStatus(),
                requestMessage);
        fireEvent(bmse);
        if (bmse.isCancelled()) {
            KtWebsocketManager.logger.trace(KtWebsocketManager.TOKEN_MARKER,
                    "sending of message [{}] was cancelled", message);
            if (bmse.getCancelState() == CancelState.HARD) {
                throw new MessageCancelException(bmse.getCancelReason());
            }
            return;
        }
        // remember id and consumer, if message expects a response
        requestMessage.getRequestId()
                .ifPresent(rid -> this.awaitedResponses.put(rid, requestMessage));
        try {
            KtWebsocketManager.logger.debug(KtWebsocketManager.TOKEN_MARKER,
                    "Sending message [{}].", message);
            this.websocket.sendMessage(message);
        } catch (final IOException e) {
            // notify observers about failed message sending
            final MessageDeliveryFailedEvent mdfe = new MessageDeliveryFailedEvent(generateStatus(),
                    requestMessage, e);
            fireEvent(mdfe);
            // if a listener handled the exception, we don't have to forward it
            if (mdfe.isCancelled()) {
                return;
            }
            // notify caller or callback about failure
            KtWebsocketManager.logger.warn(KtWebsocketManager.TOKEN_MARKER,
                    "message delivery failed for [{}] without handler", message, e);
            requestMessage.getResult()
                    .setResponse(new ResponseMessage("message delivery failed", e));
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
            sendMessage(new RequestMessage.RequestMessageBuilder(requestMessage).expectResponse()
                    .build());
        }
    }

    public void receiveMessage(final String rawMessage) {
        KtWebsocketManager.logger.debug("Received message [{}].", rawMessage);
        final Optional<JsonObject> json = tryParse(rawMessage);
        final Optional<WebsocketMessageParser<?>> parser = this.parsers.stream()
                .filter(p -> p.parses(rawMessage, json)).findFirst();
        if (parser.isPresent()) {
            final Object parsedResponse = parser.get().parse(rawMessage, json);
            if (parsedResponse instanceof ResponseMessage) {
                final ResponseMessage responseMessage = (ResponseMessage) parsedResponse;
                responseMessage.getRequestId().map(this.awaitedResponses::remove)
                        .map(RequestMessage::getResult).filter(r -> !r.isDone())
                        .ifPresent(r -> r.setResponse(responseMessage));
            }
            fireEvent(new MessageReceivedEvent(generateStatus(), parsedResponse));
        } else {
            KtWebsocketManager.logger.warn("No parser found for message [{}].", rawMessage);
            fireEvent(new UnparsableMessageEvent(generateStatus(), rawMessage, json));
        }
    }

    public void websocketDisconnected() {
        KtWebsocketManager.logger.info("Websocket was disconnected.");
        this.connected = false;
        cancelAwaitedResponses();
        fireEvent(new WebsocketDisconnectedEvent(generateStatus()));
    }

    public void websocketConnected() {
        KtWebsocketManager.logger.info("Websocket is connected.");
        this.connected = true;
        fireEvent(new WebsocketConnectedEvent(generateStatus()));
    }

    @Override
    public void close() {
        stop();
    }

    public void stop() {
        KtWebsocketManager.logger.debug("Stopping websocket...");
        this.connected = false;
        this.active = false;
        this.websocket.close();
        fireEvent(new WebsocketStoppedEvent(generateStatus()));
        KtWebsocketManager.logger.info("Websocket stopped.");
    }

    public void open() {
        if (this.active) {
            return;
        }
        KtWebsocketManager.logger.info("Starting websocket.");
        this.active = true;
        fireEvent(new WebsocketStartedEvent(generateStatus()));
        connect();
    }

    public void connect() {
        KtWebsocketManager.logger.info("Connecting websocket.");
        if (!this.active) {
            throw new IllegalStateException("cannot connect websocket while inactive");
        }
        if (this.connected) {
            throw new IllegalStateException("websocket is already connected");
        }
        this.websocket.connect();
    }

    private void cancelAwaitedResponses() {
        KtWebsocketManager.logger.debug("Cancelling waiting responses {}", this.awaitedResponses);
        final Iterator<Entry<Integer, RequestMessage>> iterator = this.awaitedResponses.entrySet()
                .iterator();
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
            KtWebsocketManager.logger.warn("Received message could not be parsed as json: {}",
                    rawMessage);
            return Optional.empty();
        }
    }

    private WebsocketStatus generateStatus() {
        return new WebsocketStatus(this.active, this.connected);
    }

}
