package de._125m125.kt.ktapi.retrofitRequester.builderModifier;

import retrofit2.Retrofit;

public interface RetrofitModifier {
    public Retrofit.Builder modify(Retrofit.Builder builder);
}
