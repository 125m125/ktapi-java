package de._125m125.kt.ktapi_java.simple;

import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.KtRequestUtil;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.Result;
import de._125m125.kt.ktapi_java.core.objects.Trade;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.simple.authenticator.BasicAuthAuthenticator;

public class TestWithMain {
    public static void main(final String[] args) {
        final User user = new User("1", "1", "1");
        // final KtRequester requester = new KtRequesterImpl(new
        // URLParamAuthenticator(user));
        final KtRequester requester = new KtRequesterImpl(new BasicAuthAuthenticator(user));
        final KtRequestUtil kt = new Kt(requester);

        System.out.println(kt.getPermissions());

        System.out.println(kt.getItems());

        System.out.println(kt.getTrades());

        System.out.println(kt.getMessages());

        System.out.println(kt.getPayouts());

        System.out.println(kt.getStatistics("4", 30));

        System.out.println(kt.getOrderBook("4", 30, true));

        final Result<Trade> createTrade = kt.createTrade(BUY_SELL.BUY, "4", 100, 92.67);
        System.out.println(createTrade);

        System.out.println(kt.cancelTrade(createTrade.getObject().getId()));

        System.out.println(kt.takeoutFromTrade(createTrade.getObject().getId()));
    }
}
