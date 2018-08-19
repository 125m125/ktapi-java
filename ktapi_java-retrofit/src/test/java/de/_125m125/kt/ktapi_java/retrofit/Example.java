package de._125m125.kt.ktapi_java.retrofit;

import java.util.List;

import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.results.Callback;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.core.users.KtUserStore;
import de._125m125.kt.ktapi_java.core.users.TokenUser;
import de._125m125.kt.ktapi_java.core.users.TokenUserKey;
import de._125m125.kt.ktapi_java.retrofitRequester.KtRetrofitRequester;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final TokenUser user = new TokenUser("1", "1", "1");
        final KtRetrofitRequester<TokenUserKey> requester = KtRetrofit.createDefaultRequester(new KtUserStore(user));
        // final CertificateUser user = new CertificateUser("1", new
        // File("certificate.p12"), new char[] { 'a' });
        // final SingleUserKtRequester<CertificateUserKey> requester =
        // KtRetrofit
        // .createClientCertificateRequester(new KtUserStore(user),
        // user.getKey(), null);
        final Result<List<Message>> history = requester.getMessages(user.getKey());
        try {
            if (history.isSuccessful()) {
                System.out.println(history.getContent());
            } else {
                System.out.println(history.getStatus() + ": " + history.getErrorMessage());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final Result<Permissions> permissions = requester.getPermissions(user.getKey());
        permissions.addCallback(new Callback<Permissions>() {

            @Override
            public void onSuccess(final int status, final Permissions result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(final int status, final String message, final String humanReadableMessage) {
                System.out.println("Request failed with status " + status + ": " + message);
            }

            @Override
            public void onError(final Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
