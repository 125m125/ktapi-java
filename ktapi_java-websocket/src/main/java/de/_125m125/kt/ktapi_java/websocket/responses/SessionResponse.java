package de._125m125.kt.ktapi_java.websocket.responses;

import java.util.List;

public class SessionResponse extends ResponseMessage {

    private final SessionDetails session;

    public SessionResponse(final Integer rid, final Long pong, final String error, final Throwable errorCause,
            final SessionDetails sessionDetails) {
        super(rid, pong, error, errorCause);
        this.session = sessionDetails;
    }

    public SessionDetails getSessionDetails() {
        return this.session;
    }

    public class SessionDetails {
        private final String       id;
        private final List<String> channelSubscriptions;

        public SessionDetails(final String id, final List<String> channelSubscriptions) {
            this.id = id;
            this.channelSubscriptions = channelSubscriptions;
        }

        public String getId() {
            return this.id;
        }

        public List<String> getChannelSubscriptions() {
            return this.channelSubscriptions;
        }
    }
}
