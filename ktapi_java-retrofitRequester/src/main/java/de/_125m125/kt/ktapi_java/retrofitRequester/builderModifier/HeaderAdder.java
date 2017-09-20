package de._125m125.kt.ktapi_java.retrofitRequester.builderModifier;

import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;

public class HeaderAdder implements ClientModifier {

    private final String name;
    private final String value;

    public HeaderAdder(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addInterceptor(chain -> {
            final Request request = chain.request().newBuilder()
                    .addHeader(HeaderAdder.this.name, HeaderAdder.this.value).build();
            return chain.proceed(request);
        });
    }

}
