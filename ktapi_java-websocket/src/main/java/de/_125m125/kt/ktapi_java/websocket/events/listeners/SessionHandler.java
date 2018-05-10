package de._125m125.kt.ktapi_java.websocket.events.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de._125m125.kt.ktapi_java.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi_java.websocket.MessageSendException;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi_java.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi_java.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi_java.websocket.requests.SessionRequestData;
import de._125m125.kt.ktapi_java.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi_java.websocket.responses.SessionResponse;

public class SessionHandler {

    private KtWebsocketManager       manager;
    private ScheduledExecutorService service;

    private String                   sessionId;

    public SessionHandler() {

    }

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException("each session handler can only be used for a single WebsocketManager");
        }
        this.manager = e.getManager();
        this.service = Executors.newScheduledThreadPool(0, r -> {
            final Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
        this.service.scheduleAtFixedRate(this::pingSession, 1, 1, TimeUnit.HOURS);
    }

    @WebsocketEventListening
    public synchronized void onWebsocketConnected(final WebsocketConnectedEvent e) {
        resumeSession();
    }

    private synchronized boolean resumeSession() {
        if (this.sessionId == null) {
            return false;
        }
        final Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("session", SessionRequestData.createResumtionRequest(this.sessionId));
        try {
            final ResponseMessage responseMessage = sendAndAwait(requestMap);
            if (!(responseMessage instanceof SessionResponse)) {
                responseMessage.getError().filter("unknownSessionId"::equals).ifPresent(msg -> this.sessionId = null);
                return false;
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void pingSession() {
        try {
            this.manager.sendMessage(new RequestMessage.RequestMessageBuilder()
                    .addContent(SessionRequestData.createStatusRequest()).build());
        } catch (final MessageSendException e) {
            this.service.schedule(this::pingSession, 1, TimeUnit.MINUTES);
        }
    }

}
