package de._125m125.kt.ktapi.retrofit.requester.modifier;

import de._125m125.kt.okhttp.helper.modifier.HeaderAdder;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit.Builder;

public class ConverterFactoryAdder extends HeaderAdder implements HybridModifier {
    private final Factory factory;

    public ConverterFactoryAdder(final String header, final Factory factory) {
        super("Accept", r -> {
            final String currentHeader = r.header("Accept");
            return currentHeader == null ? header : currentHeader + "," + header;
        });
        this.factory = factory;
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addConverterFactory(this.factory);
    }
}
