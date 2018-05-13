package de._125m125.kt.ktapi_java.websocket.events;

import de._125m125.kt.ktapi_java.websocket.KtWebsocketManager;

public class WebsocketManagerCreatedEvent extends WebsocketEvent {
    private final KtWebsocketManager manager;

    public WebsocketManagerCreatedEvent(final KtWebsocketManager manager) {
        super(new WebsocketStatus(false, false));
        this.manager = manager;
    }

    public KtWebsocketManager getManager() {
        return manager;
    }
}
