package de._125m125.kt.ktapi_java.retrofit;

import java.util.List;

import de._125m125.kt.ktapi_java.core.KtUserStore;
import de._125m125.kt.ktapi_java.core.entities.Message;
import de._125m125.kt.ktapi_java.core.entities.Permissions;
import de._125m125.kt.ktapi_java.core.entities.User;
import de._125m125.kt.ktapi_java.core.results.Callback;
import de._125m125.kt.ktapi_java.core.results.Result;
import de._125m125.kt.ktapi_java.retrofitRequester.KtRetrofitRequester;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final User user = new User("1", "1", "1");
        final KtRetrofitRequester requester = KtRetrofit.createDefaultRequester(new KtUserStore<>(user));
        final Result<List<Message>> history = requester.getMessages(user);
        try {
            if (history.isSuccessful()) {
                System.out.println(history.getContent());
            } else {
                System.out.println(history.getStatus() + ": " + history.getErrorMessage());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        final Result<Permissions> permissions = requester.getPermissions(user);
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
