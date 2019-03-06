package de._125m125.kt.ktapi.retrofit;

import java.io.File;

import com.google.gson.Gson;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.users.CertificateUser;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.retrofit.requester.KtRetrofitRequester;
import de._125m125.kt.ktapi.retrofit.requester.modifier.ConverterFactoryAdder;
import de._125m125.kt.ktapi.retrofit.requester.modifier.RetrofitModifier;
import de._125m125.kt.ktapi.retrofit.tsvparser.univocity.UnivocityConverterFactory;
import de._125m125.kt.okhttp.helper.modifier.BasicAuthenticator;
import de._125m125.kt.okhttp.helper.modifier.ClientCertificateAdder;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import de._125m125.kt.okhttp.helper.modifier.ContentTypeAdder;
import de._125m125.kt.okhttp.helper.modifier.HeaderAdder;
import de._125m125.kt.okhttp.helper.modifier.UserKeyRemover;
import okhttp3.Cache;
import retrofit2.converter.gson.GsonConverterFactory;

public class KtRetrofit {
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
        return new KtRetrofitRequester(KtRequester.DEFAULT_BASE_URL,
                getClientModifiers(userStore, cache), KtRetrofit.RETROFIT_MODIFIERS,
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
    }

    public static KtRetrofitRequester createClientCertificateRequester(final KtUserStore userStore,
            final UserKey userKey, final Cache cache) {
        final CertificateUser user = userStore.get(userKey, CertificateUser.class);
        return new KtRetrofitRequester(KtRequester.DEFAULT_BASE_URL,
                getClientModifiers(userStore, cache,
                        ClientCertificateAdder.createUnchecked(user.getFile(), user.getPassword())),
                KtRetrofit.RETROFIT_MODIFIERS,
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
    }

    private static ClientModifier[] getClientModifiers(final KtUserStore store, final Cache cache,
            final ClientModifier... others) {
        final ClientModifier[] modifiers = new ClientModifier[4 + (cache == null ? 0 : 1)
                + others.length];
        int i = 0;
        System.arraycopy(others, 0, modifiers, i, others.length);
        i += others.length;
        modifiers[i++] = new BasicAuthenticator(store);
        modifiers[i++] = new HeaderAdder("Accept", "text/tsv,application/json");
        modifiers[i++] = new UserKeyRemover();
        modifiers[i++] = new ContentTypeAdder();
        if (cache != null) {
            modifiers[i++] = client -> client.cache(cache);
        }
        return modifiers;
    }
}
