package de._125m125.kt.ktapi.requester.jersey.interceptors;

import java.util.function.Function;

import javax.ws.rs.client.ClientBuilder;

public abstract class SelfRegistrator implements Function<ClientBuilder, ClientBuilder> {

    public SelfRegistrator() {
        super();
    }

    @Override
    public ClientBuilder apply(final ClientBuilder t) {
        return t.register(this);
    }

}