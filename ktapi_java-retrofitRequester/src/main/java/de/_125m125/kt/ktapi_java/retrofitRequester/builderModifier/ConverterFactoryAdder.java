package de._125m125.kt.ktapi_java.retrofitRequester.builderModifier;

import retrofit2.Converter.Factory;
import retrofit2.Retrofit.Builder;

public class ConverterFactoryAdder implements RetrofitModifier {
    private final Factory factory;

    public ConverterFactoryAdder(final Factory factory) {
        this.factory = factory;
    }

    @Override
    public Builder modify(final Builder builder) {
        return builder.addConverterFactory(this.factory);
    }
}
