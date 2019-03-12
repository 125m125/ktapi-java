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
package de._125m125.kt.ktapi.core.entities;

public enum Entity {
    HISTORY_ENTRY("history", HistoryEntry.class),
    ITEM("items", Item.class),
    MESSAGE("messages", Message.class),
    ORDERBOOK_ENTRY("orderbook", OrderBookEntry.class),
    PAYOUT("payouts", Payout.class),
    TRADE("trades", Trade.class),;

    public static Entity forInstanceClass(final Class<?> clazz) {
        for (final Entity e : Entity.values()) {
            if (e.getInstanceClass() == clazz) {
                return e;
            }
        }
        throw new IllegalArgumentException("there is no entity with instance class " + clazz);
    }

    public static Entity forUpdateChannel(final String channel) {
        for (final Entity e : Entity.values()) {
            if (e.getUpdateChannel().equals(channel)) {
                return e;
            }
        }
        throw new IllegalArgumentException("there is no entity with channel " + channel);
    }

    private final String   updateChannel;
    private final Class<?> clazz;

    private Entity(final String updateChannel, final Class<?> clazz) {
        this.updateChannel = updateChannel;
        this.clazz = clazz;

    }

    public String getUpdateChannel() {
        return this.updateChannel;
    }

    public Class<?> getInstanceClass() {
        return this.clazz;
    }

}
