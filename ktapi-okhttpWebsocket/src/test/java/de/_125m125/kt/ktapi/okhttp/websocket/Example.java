/**
 * The MIT License
 * Copyright © 2017 Kadcontrade
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de._125m125.kt.ktapi.okhttp.websocket;

import java.io.File;

import de._125m125.kt.ktapi.core.users.CertificateUser;
import de._125m125.kt.ktapi.core.users.KtUserStore;
import de._125m125.kt.ktapi.core.users.User;
import de._125m125.kt.ktapi.websocket.KtWebsocketManager;
import de._125m125.kt.ktapi.websocket.events.listeners.AbstractKtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.AutoReconnectionHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.KtWebsocketNotificationHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.OfflineMessageHandler;
import de._125m125.kt.ktapi.websocket.events.listeners.SessionHandler;
import de._125m125.kt.okhttp.helper.OkHttpClientBuilder;
import de._125m125.kt.okhttp.helper.modifier.ClientCertificateAdder;
import de._125m125.kt.okhttp.helper.modifier.ClientModifier;

public class Example {
    public static void main(final String[] args) throws InterruptedException {
        final OkHttpClientBuilder builder = new OkHttpClientBuilder();
        final ClientModifier createUnchecked = ClientCertificateAdder.createUnchecked(
                new File("../../certificateHelper/certificate2.p12"), "a".toCharArray());
        builder.addModifier(createUnchecked);

        final User user = new CertificateUser("1d0tat5", "../../certificateHelper/certificate2.p12",
                "a".toCharArray());
        final KtOkHttpWebsocket ws = new KtOkHttpWebsocket("wss://kt.125m125.de/api/websocket",
                builder);
        final AbstractKtWebsocketNotificationHandler<?> manager = 
                new KtWebsocketNotificationHandler(new KtUserStore(user));
        KtWebsocketManager.builder(ws).addDefaultParsers().addListener(new OfflineMessageHandler())
                .addListener(new SessionHandler()).addListener(manager)
                .addListener(new AutoReconnectionHandler()).buildAndOpen();

        Thread.sleep(100);
        System.out.println("subscribing...");
        manager.subscribeToItems(System.out::println, user.getKey(), false)
                .whenComplete((listener, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Subscription failed: " + throwable.getMessage());
                    } else {
                        System.out.println("Subscription succeeded");
                    }
                });
        manager.subscribeToAll(System.out::println);
        manager.subscribeToAll(System.out::println, user.getKey(), true);

        Thread.sleep(600_000);
        System.out.println("closing");
        manager.disconnect();
    }
}
