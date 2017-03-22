package de._125m125.kt.ktapi_java.core;

/**
 * The Enum BUY_SELL.
 */
public enum BUY_SELL {
    BUY,
    SELL;

    /** The opposite. */
    private BUY_SELL opposite;

    /**
     * Gets the opposite.
     *
     * @return the opposite
     */
    public BUY_SELL getOpposite() {
        return this.opposite;
    }

    static {
        BUY.opposite = SELL;
        SELL.opposite = BUY;
    }
}