package de._125m125.kt.ktapi_java.retrofitRequester.builderModifier;

import java.io.IOException;

import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;

public class HeaderAdder implements ClientModifier {

    public static interface HeaderProducer {
        public String apply(Request r) throws IOException;
    }

    private final String         name;
    private final HeaderProducer valueProducer;

    public HeaderAdder(final String name, final String value) {
        this(name, r -> value);
    }

    public HeaderAdder(final String name, final HeaderProducer valueProducer) {
        this.name = name;
        this.valueProducer = valueProducer;
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addInterceptor(chain -> {
            final String value = HeaderAdder.this.valueProducer.apply(chain.request());
            Request request = chain.request();
            if (value != null) {
                request = chain.request().newBuilder().addHeader(HeaderAdder.this.name, value).build();
            }
            return chain.proceed(request);
        });
    }

}
