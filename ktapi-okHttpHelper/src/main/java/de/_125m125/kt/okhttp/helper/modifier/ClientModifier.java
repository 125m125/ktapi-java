package de._125m125.kt.okhttp.helper.modifier;

import okhttp3.OkHttpClient;

public interface ClientModifier {
    public OkHttpClient.Builder modify(OkHttpClient.Builder builder);
}
