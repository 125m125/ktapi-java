/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.websocket.events.listeners;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.BeforeMessageSendEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketConnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketDisconnectedEvent;
import de._125m125.kt.ktapi.websocket.events.WebsocketEventListening;
import de._125m125.kt.ktapi.websocket.events.WebsocketManagerCreatedEvent;
import de._125m125.kt.ktapi.websocket.exceptions.MessageSendException;
import de._125m125.kt.ktapi.websocket.requests.RequestMessage;
import de._125m125.kt.ktapi.websocket.requests.SessionRequestData;
import de._125m125.kt.ktapi.websocket.responses.ResponseMessage;
import de._125m125.kt.ktapi.websocket.responses.SessionResponse;

public class SessionHandler {
    private static final Logger      logger        = LoggerFactory.getLogger(SessionHandler.class);

    private KtWebsocketManager       manager;
    private ScheduledExecutorService service;

    private String                   sessionId;
    private boolean                  sessionActive = false;

    public SessionHandler() {

    }

    @WebsocketEventListening
    public synchronized void onWebsocketManagerCreated(final WebsocketManagerCreatedEvent e) {
        if (this.manager != null) {
            throw new IllegalStateException(
                    "each session handler can only be used for a single WebsocketManager");
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
        SessionHandler.logger.trace("Checking session status...");
        if (this.sessionActive) {
            SessionHandler.logger.debug("Session already active.");
            return;
        }
        final RequestMessage requestMessage = RequestMessage.builder()
                .addContent(SessionRequestData.createStartRequest()).build();
        this.manager.sendRequest(requestMessage);
        try {
            final ResponseMessage responseMessage = requestMessage.getResult().get(5,
                    TimeUnit.SECONDS);
            if (!(responseMessage instanceof SessionResponse)) {
                SessionHandler.logger.warn("Failed to aquire session.");
                return;
            }
            final String newSessionId = ((SessionResponse) responseMessage).getSessionDetails()
                    .getId();
            SessionHandler.logger.info("Aquired session with id {}. Previous: {}", newSessionId,
                    this.sessionId);
            this.sessionId = newSessionId;
            this.sessionActive = true;
        } catch (InterruptedException | TimeoutException e) {
            SessionHandler.logger.warn("Failed to aquire session.");
            return;
        }
    }

    private synchronized boolean resumeSession() {
        if (this.sessionId == null || this.sessionActive) {
            return true;
        }
        SessionHandler.logger.info("resuming previously started session {}", this.sessionId);
        final RequestMessage requestMessage = RequestMessage.builder()
                .addContent(SessionRequestData.createResumtionRequest(this.sessionId)).build();
        this.manager.sendRequest(requestMessage);
        try {
            final ResponseMessage responseMessage = requestMessage.getResult().get(30,
                    TimeUnit.SECONDS);
            final boolean error = responseMessage.getError().filter("unknownSessionId"::equals)
                    .isPresent();
            if (error) {
                SessionHandler.logger.warn("Could not resume session.");
                this.sessionId = null;
                return false;
            } else {
                SessionHandler.logger.info("Session resumed successfully.");
                this.sessionActive = true;
                return true;
            }
        } catch (InterruptedException | TimeoutException e) {
            SessionHandler.logger.warn("Could not resume session.");
            return false;
        }
    }

    public synchronized void pingSession() {
        if (!this.sessionActive) {
            return;
        }
        SessionHandler.logger.debug("Sending ping message to keep session alive.");
        try {
            this.manager.sendMessage(new RequestMessage.RequestMessageBuilder()
                    .addContent(SessionRequestData.createStatusRequest()).build());
        } catch (final MessageSendException e) {
            this.service.schedule(this::pingSession, 1, TimeUnit.MINUTES);
        }
    }

}
