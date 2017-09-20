package de._125m125.kt.ktapi_java.core.entities;

public class PusherResult {
    private final String authdata;
    private final String channelname;

    public PusherResult(final String authdata, final String channelname) {
        this.authdata = authdata;
        this.channelname = channelname;
    }

    public String getAuthdata() {
        return this.authdata;
    }

    public String getChannelname() {
        return this.channelname;
    }
}
