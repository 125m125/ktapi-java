package de._125m125.kt.ktapi.core.entities;

import java.util.Objects;

public class PusherResult {
    private final String authdata;
    private final String channelname;

    public PusherResult(final String authdata, final String channelname) {
        this.authdata = Objects.requireNonNull(authdata);
        this.channelname = Objects.requireNonNull(channelname);
    }

    public String getAuthdata() {
        return this.authdata;
    }

    public String getChannelname() {
        return this.channelname;
    }
}
