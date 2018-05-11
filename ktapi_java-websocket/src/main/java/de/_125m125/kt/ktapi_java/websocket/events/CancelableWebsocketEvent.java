package de._125m125.kt.ktapi_java.websocket.events;

public class CancelableWebsocketEvent extends WebsocketEvent {

    public static enum CancelState {
        NONE,
        SOFT,
        HARD;
    }

    private CancelState cancelState;
    private Throwable   cancelReason;

    public CancelableWebsocketEvent(final WebsocketStatus websocketStatus) {
        super(websocketStatus);
        this.cancelState = CancelState.NONE;
    }

    public void setCancelState(final CancelState cancelState) {
        this.cancelState = cancelState;
    }

    public void cancel() {
        setCancelState(CancelState.HARD);
    }

    public void cancel(final Throwable t) {
        this.cancelReason = t;
        setCancelState(CancelState.HARD);
    }

    public void softCancel() {
        setCancelState(CancelState.SOFT);
    }

    public CancelState getCancelState() {
        return this.cancelState;
    }

    public boolean isCancelled() {
        return this.cancelState != CancelState.NONE;
    }

    public Throwable getCancelReason() {
        return this.cancelReason;
    }

}
