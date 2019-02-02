package de._125m125.kt.ktapi.core;

/**
 * The Enum BUY_SELL.
 */
public enum BuySellBoth {
    BUY,
    SELL,
    BOTH;

    /** The opposite. */
    private BuySellBoth opposite;

    /**
     * Gets the opposite.
     *
     * @return the opposite
     */
    public BuySellBoth getOpposite() {
        return this.opposite;
    }

    static {
        BUY.opposite = SELL;
        SELL.opposite = BUY;
        BOTH.opposite = BOTH;
    }
}