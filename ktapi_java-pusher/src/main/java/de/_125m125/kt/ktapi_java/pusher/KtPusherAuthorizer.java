package de._125m125.kt.ktapi_java.pusher;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;

import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.entities.PusherResult;
import de._125m125.kt.ktapi_java.core.entities.User;
import de._125m125.kt.ktapi_java.core.results.Result;

public class KtPusherAuthorizer implements Authorizer {

    private final User        user;
    private final KtRequester requester;

    public KtPusherAuthorizer(final User user, final KtRequester requester) {
        this.user = user;
        this.requester = requester;
    }

    @Override
    public final String authorize(final String channelName, final String socketId)
            throws AuthorizationFailureException {
        final Result<PusherResult> pusherAuthResult = this.requester.authorizePusher(this.user.getUID(), channelName,
                socketId);
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