package de._125m125.kt.ktapi.core;

public enum PAYOUT_TYPE {
    // money
    KADCON("kadcon"),
    // items
    DELIVERY("lieferung"),
    PAYOUT_BOX_S1("bs1"),
    PAYOUT_BOX_S2("bs2"),
    PAYOUT_BOX_S3("bs3"),

    ;

    private final String comName;

    private PAYOUT_TYPE(final String comName) {
        this.comName = comName;
    }

    public String getComName() {
        return comName;
    }
}
