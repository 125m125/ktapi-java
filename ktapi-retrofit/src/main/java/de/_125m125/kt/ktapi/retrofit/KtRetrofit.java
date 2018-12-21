package de._125m125.kt.ktapi.retrofit;

import java.io.File;

import com.google.gson.Gson;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.SingleUserKtRequester;
import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.users.CertificateUser;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.retrofitRequester.KtRetrofitRequester;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.BasicAuthenticator;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.CertificatePinnerAdder;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.ClientCertificateAdder;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.ClientModifier;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.ConverterFactoryAdder;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.HeaderAdder;
import de._125m125.kt.ktapi.retrofitRequester.builderModifier.RetrofitModifier;
import de._125m125.kt.ktapi.retrofitUnivocityTsvparser.UnivocityConverterFactory;
import okhttp3.Cache;
import retrofit2.converter.gson.GsonConverterFactory;

public class KtRetrofit {
    public static final String              DEFAULT_BASE_URL   = "https://kt.125m125.de/api/v2.0/";
    private static final RetrofitModifier[] RETROFIT_MODIFIERS = new RetrofitModifier[] {
            new ConverterFactoryAdder(new UnivocityConverterFactory()),
            new ConverterFactoryAdder(GsonConverterFactory.create()) };

    public static KtRetrofitRequester createDefaultRequester(final KtUserStore userStore) {
        return createDefaultRequester(userStore, null);
    }

    public static KtRetrofitRequester createDefaultRequester(final KtUserStore userStore,
            final File cacheDirectory, final long maxCacheSize) {
        return createDefaultRequester(userStore, new Cache(cacheDirectory, maxCacheSize));
    }

    public static KtRetrofitRequester createDefaultRequester(final KtUserStore userStore,
            final Cache cache) {
        return new KtRetrofitRequester(KtRetrofit.DEFAULT_BASE_URL,
                getClientModifiers(new BasicAuthenticator(userStore), cache),
                KtRetrofit.RETROFIT_MODIFIERS,
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
    }

    public static SingleUserKtRequester createClientCertificateRequester(
            final KtUserStore userStore, final UserKey userKey, final Cache cache) {
        final CertificateUser user = userStore.get(userKey, CertificateUser.class);
        final KtRequester baseRequester = new KtRetrofitRequester(KtRetrofit.DEFAULT_BASE_URL,
                getClientModifiers(
                        ClientCertificateAdder.createUnchecked(user.getFile(), user.getPassword()),
                        cache),
                KtRetrofit.RETROFIT_MODIFIERS,
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
        return new SingleUserKtRequester(userKey, baseRequester);
    }

    private static ClientModifier[] getClientModifiers(final ClientModifier authenticator,
            final Cache cache) {
        return new ClientModifier[] { authenticator,
                new HeaderAdder("Accept", "text/tsv,application/json"), client -> {
                    if (cache != null) {
                        client.cache(cache);
                    }
                    return client;
                }, new CertificatePinnerAdder() };
    }
}
