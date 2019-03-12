package de._125m125.kt.ktapi.retrofit.requester;

import com.google.gson.Gson;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.requester.integrationtest.RequesterIntegrationTest;
import de._125m125.kt.ktapi.retrofit.requester.modifier.GsonConverterFactoryAdder;
import de._125m125.kt.ktapi.retrofit.requester.modifier.HybridModifier;
import de._125m125.kt.ktapi.retrofit.requester.modifier.RetrofitModifier;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;

public class KtRetrofitRequesterIt extends RequesterIntegrationTest {

    @Override
    public KtRequester createRequester(final String baseUrl, final KtUserStore userStore) {
        return new KtRetrofitRequester(baseUrl, new ClientModifier[] {},
                new HybridModifier[] { new GsonConverterFactoryAdder() }, new RetrofitModifier[] {},
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
    }

}
