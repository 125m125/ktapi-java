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
