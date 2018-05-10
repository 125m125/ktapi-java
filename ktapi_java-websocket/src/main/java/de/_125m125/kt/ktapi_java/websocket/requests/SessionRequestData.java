package de._125m125.kt.ktapi_java.websocket.requests;

public class SessionRequestData {

    public static SessionRequestData createStartRequest() {
        return new SessionRequestData(RequestType.start, null);
    }

    public static SessionRequestData createStatusRequest() {
        return new SessionRequestData(RequestType.status, null);
    }

    public static SessionRequestData createResumtionRequest(final String sessionId) {
        return new SessionRequestData(RequestType.resume, sessionId);
    }

    public enum RequestType {
        start,
        status,
        resume;
    }

    private final RequestType request;
    private final String      id;

    private SessionRequestData(final RequestType request, final String id) {
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
