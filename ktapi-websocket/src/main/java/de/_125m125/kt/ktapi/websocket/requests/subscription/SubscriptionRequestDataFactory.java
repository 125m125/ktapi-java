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
    @SuppressWarnings("unchecked")
    public <T extends User<T>> SubscriptionRequestData<T> createSubscriptionRequestData(
            final String channel, final T user, final boolean selfCreated) {
        if (user instanceof TokenUser) {
            return (SubscriptionRequestData<T>) new TokenSubscriptionRequestData<>(channel,
                    ((Class<? extends TokenUser>) user.getClass()).cast(user), selfCreated);
        }
        return new SubscriptionRequestData<>(channel, user, selfCreated);
    }
}
