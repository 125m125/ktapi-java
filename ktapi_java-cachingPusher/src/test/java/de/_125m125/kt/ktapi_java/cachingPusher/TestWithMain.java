package de._125m125.kt.ktapi_java.cachingPusher;

import java.util.List;

import de._125m125.kt.ktapi_java.core.BUY_SELL;
import de._125m125.kt.ktapi_java.core.KtNotificationManager;
import de._125m125.kt.ktapi_java.core.KtRequestUtil;
import de._125m125.kt.ktapi_java.core.KtRequester;
import de._125m125.kt.ktapi_java.core.Result;
import de._125m125.kt.ktapi_java.core.objects.Trade;
import de._125m125.kt.ktapi_java.core.objects.User;
import de._125m125.kt.ktapi_java.pusher.PusherKt;
import de._125m125.kt.ktapi_java.simple.Kt;
import de._125m125.kt.ktapi_java.simple.KtRequesterImpl;
import de._125m125.kt.ktapi_java.simple.parsers.SpecializedJsonParser;

public class TestWithMain {

    public static void main(final String[] args) {
        final User u = new User("1", "1", "1");
        final KtRequester r = new KtRequesterImpl(u);
        final KtNotificationManager nm = new PusherKt(u, new SpecializedJsonParser<>(), KtRequesterImpl.BASE_URL);
        final CachingPusherKt cp = new CachingPusherKt(u, r, nm);
        final KtRequestUtil kt = new Kt(cp);

        try {
            Thread.sleep(5000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        final List<Trade> trades = kt.getTrades();
        System.out.println(trades);
        System.out.println(cp.hasUpdated(trades));
        System.out.println(kt.getTrades() == trades);
        final Result<Trade> createTrade = kt.createTrade(BUY_SELL.BUY, "4", 1, 0.04);
        kt.cancelTrade(createTrade.getObject());

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
        nm.subscribeToTrades((e) -> {
            System.out.println("updated: " + cp.hasUpdated(trades));
            System.out.println("equal: " + (kt.getTrades() == trades));
            System.out.println(kt.getTrades());
        }, u, true);
        kt.takeoutFromTrade(createTrade.getObject());

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
