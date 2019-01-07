package de._125m125.kt.ktapi.websocket.requests.subscription;

import de._125m125.kt.ktapi.core.users.TokenUser;
import de._125m125.kt.ktapi.core.users.User;

/**
 * A factory for creating SubscriptionRequestData objects. New Types of users can be added by
 * overriding {@link SubscriptionRequestDataFactory#create(String, User, boolean)} and creating
 * special SubscriptionRequestData instances for these user types.
 */
public class SubscriptionRequestDataFactory {

    /**
     * Creates a new instance of SubscriptionRequestData.
     *
     * @param channel
     *            the channel
     * @param user
     *            the user
     * @param selfCreated
     *            the self created
     * @return the subscription request data
     */
    public SubscriptionRequestData createSubscriptionRequestData(final String channel,
            final User user, final boolean selfCreated) {
        if (user instanceof TokenUser) {
            return new TokenSubscriptionRequestData(channel, (TokenUser) user, selfCreated);
        }
        return new SubscriptionRequestData(channel, user, selfCreated);
    }
}
