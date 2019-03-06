package de._125m125.kt.ktapi.requester.jersey.parsers;

import java.util.function.Function;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import de._125m125.kt.ktapi.requester.jersey.interceptors.HeaderAdderFilter;

/**
 * <b>To use this class, the dependecy
 * com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider is required!</b>
 * <br>
 * This class registers the jackson JSON parser and provider to a ClientBuilder.
 */
public class JacksonJsonProviderRegistrator implements Function<ClientBuilder, ClientBuilder> {

    private static final ObjectMapper                  MAPPER;

    public static final JacksonJsonProviderRegistrator INSTANCE;

    static {
        MAPPER = new ObjectMapper();
        JacksonJsonProviderRegistrator.MAPPER
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonJsonProviderRegistrator.MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        JacksonJsonProviderRegistrator.MAPPER.setVisibility(PropertyAccessor.ALL,
                JsonAutoDetect.Visibility.ANY);
        JacksonJsonProviderRegistrator.MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        INSTANCE = new JacksonJsonProviderRegistrator();
    }

    private JacksonJsonProviderRegistrator() {
        // singleton
    }

    @Override
    public ClientBuilder apply(final ClientBuilder builder) {
        return builder
                .register(new JacksonJaxbJsonProvider(JacksonJsonProviderRegistrator.MAPPER,
                        JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS))
                .register(new HeaderAdderFilter("Accept", MediaType.APPLICATION_JSON));
    }

}