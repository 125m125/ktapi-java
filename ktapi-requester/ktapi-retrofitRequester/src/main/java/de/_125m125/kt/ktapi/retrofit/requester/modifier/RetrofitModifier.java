package de._125m125.kt.ktapi.retrofit.requester.modifier;

import retrofit2.Retrofit;

public interface RetrofitModifier {
    public Retrofit.Builder modify(Retrofit.Builder builder);
}
