/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
 * <b>To use this class, the dependecy com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider is
 * required!</b> <br>
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