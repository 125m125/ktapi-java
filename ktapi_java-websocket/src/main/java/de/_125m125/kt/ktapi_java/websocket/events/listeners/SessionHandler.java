package de._125m125.kt.ktapi_java.websocket.events.listeners;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de._125m125.kt.ktapi_java.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi_java.websocket.events.BeforeMessageSendEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketDisconnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi_java.websocket.exceptions.MessageSendException;
import de._125m125.kt.ktapi_java.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi_java.websocket.requests.SessionRequestData;
import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi_java.websocket.responses.SessionResponse;

public class SessionHandler {

    private KtWebsocketManager       manager;
    private ScheduledExecutorService service;

    private String                   sessionId;
    private boolean                  sessionActive = false;

    public SessionHandler() {

    }

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException("each session handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
        this.service = Executors.newScheduledThreadPool(1, r -> {
            final Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
        this.service.scheduleAtFixedRate(this::pingSession, 1, 1, TimeUnit.HOURS);
    }

    @WebsocketEventListening
    public void onWebsocketConnected(final WebsocketConnectedEvent e) {
        resumeSession();
    }

    @WebsocketEventListening
    public synchronized void onWebsocketDisconnected(final WebsocketDisconnectedEvent e) {
        this.sessionActive = false;
    }

    @WebsocketEventListening
    public void onBeforeMessageSend(final BeforeMessageSendEvent e) {
        if (e.getMessage().hasContent("subscribe")) {
            checkSession();
        }
    }

    private synchronized void checkSession() {
        if (this.sessionActive) {
            return;
        }
        final RequestMessage requestMessage = RequestMessage.builder()
                .addContent(SessionRequestData.createStartRequest()).build();
        this.manager.sendRequest(requestMessage);
        try {
            final ResponseMessage responseMessage = requestMessage.getResult().get(5, TimeUnit.SECONDS);
            if (!(responseMessage instanceof SessionResponse)) {
                return;
            }
            this.sessionId = ((SessionResponse) responseMessage).getSessionDetails().getId();
            this.sessionActive = true;
        } catch (InterruptedException | TimeoutException e) {
            return;
        }
    }

    private synchronized boolean resumeSession() {
        if (this.sessionId == null || this.sessionActive) {
            return true;
        }
        final RequestMessage requestMessage = RequestMessage.builder()
                .addContent(SessionRequestData.createResumtionRequest(this.sessionId)).build();
        this.manager.sendRequest(requestMessage);
        try {
            final ResponseMessage responseMessage = requestMessage.getResult().get(30, TimeUnit.SECONDS);
            final boolean error = responseMessage.getError().filter("unknownSessionId"::equals).isPresent();
            if (error) {
                this.sessionId = null;
                return false;
            } else {
                this.sessionActive = true;
                return true;
            }
        } catch (InterruptedException | TimeoutException e) {
            return false;
        }
    }

    public synchronized void pingSession() {
        if (!this.sessionActive) {
            return;
        }
        try {
            this.manager.sendMessage(new RequestMessage.RequestMessageBuilder()
                    .addContent(SessionRequestData.createStatusRequest()).build());
        } catch (final MessageSendException e) {
            this.service.schedule(this::pingSession, 1, TimeUnit.MINUTES);
        }
    }

}
