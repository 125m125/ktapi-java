package de._125m125.kt.ktapi.requester.jersey.interceptors;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class HeaderAdderFilter extends SelfRegistrator implements ClientRequestFilter {

    private final String header;
    private final String content;

    public HeaderAdderFilter(final String header, final String content) {
        this.header = header;
        this.content = content;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(this.header, this.content);
    }

}
