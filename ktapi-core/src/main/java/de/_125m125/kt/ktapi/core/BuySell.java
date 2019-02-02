package de._125m125.kt.ktapi.core;

/**
 * The Enum BUY_SELL.
 */
public enum BuySell {
    BUY,
    SELL;

    /** The opposite. */
    private BuySell opposite;

    /**
     * Gets the opposite.
     *
     * @return the opposite
     */
    public BuySell getOpposite() {
        return this.opposite;
    }

    static {
        BUY.opposite = SELL;
        SELL.opposite = BUY;
    }
}