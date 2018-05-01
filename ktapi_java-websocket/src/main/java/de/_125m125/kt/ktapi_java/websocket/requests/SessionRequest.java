package de._125m125.kt.ktapi_java.websocket.requests;

public class SessionRequest {

    public static SessionRequest createStartRequest() {
        return new SessionRequest(RequestType.start, null);
    }

    public static SessionRequest createStatusRequest() {
        return new SessionRequest(RequestType.status, null);
    }

    public static SessionRequest createResumtionRequest(final String sessionId) {
        return new SessionRequest(RequestType.resume, sessionId);
    }

    public enum RequestType {
        start,
        status,
        resume;
    }

    private final RequestType request;
    private final String      id;

    private SessionRequest(final RequestType request, final String id) {
        this.request = request;
        this.id = id;
    }

    public RequestType getRequest() {
        return this.request;
    }

    public String getId() {
        return this.id;
    }
}
