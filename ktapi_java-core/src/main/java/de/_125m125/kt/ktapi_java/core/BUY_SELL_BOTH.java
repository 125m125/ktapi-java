package de._125m125.kt.ktapi_java.core;

/**
 * The Enum BUY_SELL.
 */
public enum BUY_SELL_BOTH {
    BUY,
    SELL,
    BOTH;

    /** The opposite. */
    private BUY_SELL_BOTH opposite;

    /**
     * Gets the opposite.
     *
     * @return the opposite
     */
    public BUY_SELL_BOTH getOpposite() {
        return this.opposite;
    }

    static {
        BUY.opposite = SELL;
        SELL.opposite = BUY;
        BOTH.opposite = BOTH;
    }
}