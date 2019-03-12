package de._125m125.kt.ktapi.requester.jersey;

import de._125m125.kt.ktapi.core.KtRequester;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.requester.integrationtest.RequesterIntegrationTest;
import de._125m125.kt.ktapi.requester.jersey.parsers.JacksonJsonProviderRegistrator;

public class KtJerseyRequesterIt extends RequesterIntegrationTest {

    @Override
    public KtRequester createRequester(final String baseUrl, final KtUserStore userStore) {
        return new KtJerseyRequester(baseUrl, null, JacksonJsonProviderRegistrator.INSTANCE);
    }

}
