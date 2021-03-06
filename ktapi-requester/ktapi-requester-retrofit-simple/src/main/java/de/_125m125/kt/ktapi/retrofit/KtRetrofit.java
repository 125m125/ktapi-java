/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.retrofit;

import java.io.File;

import com.google.gson.Gson;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.results.ErrorResponse;
import de._125m125.kt.ktapi.core.users.CertificateUser;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.UserKey;
import de._125m125.kt.ktapi.requester.retrofit.KtRetrofitRequester;
import de._125m125.kt.ktapi.requester.retrofit.modifier.GsonConverterFactoryAdder;
import de._125m125.kt.ktapi.requester.retrofit.modifier.HybridModifier;
import de._125m125.kt.ktapi.requester.retrofit.modifier.RetrofitModifier;
import de._125m125.kt.ktapi.retrofit.tsvparser.univocity.UnivocityConverterFactoryAdder;
import de._125m125.kt.okhttp.helper.modifier.BasicAuthenticator;
import de._125m125.kt.okhttp.helper.modifier.ClientCertificateAdder;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;
import de._125m125.kt.okhttp.helper.modifier.ContentTypeAdder;
import de._125m125.kt.okhttp.helper.modifier.HeaderAdder;
import de._125m125.kt.okhttp.helper.modifier.UserKeyRemover;
import okhttp3.Cache;

public class KtRetrofit {
    private static final RetrofitModifier[] RETROFIT_MODIFIERS = new RetrofitModifier[] {};
    private static final HybridModifier[]   HYBRID_MODIFIERS   = new HybridModifier[] {
            new UnivocityConverterFactoryAdder(), new GsonConverterFactoryAdder(), };

    public static KtRetrofitRequester createDefaultRequester(final String appName,
            final KtUserStore userStore) {
        return createDefaultRequester(appName, userStore, null);
    }

    public static KtRetrofitRequester createDefaultRequester(final String appName,
            final KtUserStore userStore, final File cacheDirectory, final long maxCacheSize) {
        return createDefaultRequester(appName, userStore, new Cache(cacheDirectory, maxCacheSize));
    }

    public static KtRetrofitRequester createDefaultRequester(final String appName,
            final KtUserStore userStore, final Cache cache) {
        return new KtRetrofitRequester(appName, KtRequester.DEFAULT_BASE_URL,
                getClientModifiers(userStore, cache), KtRetrofit.HYBRID_MODIFIERS,
                KtRetrofit.RETROFIT_MODIFIERS,
                value -> new Gson().fromJson(value.charStream(), ErrorResponse.class));
    }

    public static KtRetrofitRequester createClientCertificateRequester(final String appName,
            final KtUserStore userStore, final UserKey userKey, final Cache cache) {
        final CertificateUser user = userStore.get(userKey, CertificateUser.class);
        return new KtRetrofitRequester(appName, KtRequester.DEFAULT_BASE_URL,
                getClientModifiers(userStore, cache,
                        ClientCertificateAdder.createUnchecked(user.getFile(), user.getPassword())),
                KtRetrofit.HYBRID_MODIFIERS, KtRetrofit.RETROFIT_MODIFIERS,
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
