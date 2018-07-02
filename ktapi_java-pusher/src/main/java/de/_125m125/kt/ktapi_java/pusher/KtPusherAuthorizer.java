package de._125m125.kt.ktapi_java.pusher;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;

import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.UserKey;
import de._125m125.kt.ktapi_java.core.results.Result;

public class KtPusherAuthorizer<T extends UserKey> implements Authorizer {

    private final T                      user;
    private final KtRequester<? super T> requester;

    public KtPusherAuthorizer(final T user, final KtRequester<? super T> requester) {
        this.user = user;
        this.requester = requester;
    }

    @Override
    public final String authorize(final String channelName, final String socketId)
            throws AuthorizationFailureException {
        final Result<PusherResult> pusherAuthResult = this.requester.authorizePusher(this.user, channelName, socketId);
        try {
            if (pusherAuthResult.isSuccessful()) {
                return pusherAuthResult.getContent().getAuthdata();
            } else {
                throw new AuthorizationFailureException(pusherAuthResult.getErrorMessage());
            }
        } catch (final Exception e) {
            throw new AuthorizationFailureException(e);
        }
    }
}