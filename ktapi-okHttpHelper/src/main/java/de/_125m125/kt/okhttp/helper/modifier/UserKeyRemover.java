package de._125m125.kt.okhttp.helper.modifier;

import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;

public class UserKeyRemover implements ClientModifier {
    @Override
    public Builder modify(final Builder clientBuilder) {
        return clientBuilder.addInterceptor(chain -> {
            final Request r = chain.request().newBuilder().removeHeader("userKey").build();
            return chain.proceed(r);
        });
    }
}