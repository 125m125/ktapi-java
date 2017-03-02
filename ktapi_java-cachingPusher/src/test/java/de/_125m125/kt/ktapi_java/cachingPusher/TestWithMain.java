package de._125m125.kt.ktapi_java.cachingPusher;

import java.util.List;

import de._125m125.kt.ktapi_java.simple.Kt.BUY_SELL;
import de._125m125.kt.ktapi_java.simple.Result;
import de._125m125.kt.ktapi_java.simple.objects.Trade;
import de._125m125.kt.ktapi_java.simple.objects.User;

public class TestWithMain {

    public static void main(final String[] args) {
        final CachingPusherKt kt = new CachingPusherKt(new User("1", "1", "1"));

        try {
            Thread.sleep(5000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        final List<Trade> trades = kt.getTrades();
        System.out.println(trades);
        System.out.println(kt.hasUpdated(trades));
        System.out.println(kt.getTrades() == trades);
        final Result<Trade> createTrade = kt.createTrade(BUY_SELL.BUY, "4", 1, 0.04);
        kt.cancelTrade(createTrade.getObject());

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
        kt.subscribeToTrades((e) -> {
            System.out.println("updated: " + kt.hasUpdated(trades));
            System.out.println("equal: " + (kt.getTrades() == trades));
            System.out.println(kt.getTrades());
        }, true);
        kt.takeoutFromTrade(createTrade.getObject());

        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
