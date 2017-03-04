package de._125m125.kt.ktapi_java.core.objects;

import de._125m125.kt.ktapi_java.core.BUY_SELL;

public class OrderBookEntry {
    private String type;
    private double price;
    private int    amount;

    public OrderBookEntry() {
        super();
    }

    public OrderBookEntry(final String type, final double price, final int amount) {
        super();
        this.type = type;
        this.price = price;
        this.amount = amount;
    }

    public String getType() {
        return this.type;
    }

    public boolean isBuying() {
        return this.type.equals("buy");
    }

    public boolean isSelling() {
        return this.type.equals("sell");
    }

    public BUY_SELL getBuySell() {
        if (isBuying()) {
            return BUY_SELL.BUY;
        } else if (isSelling()) {
            return BUY_SELL.SELL;
        } else {
            throw new IllegalStateException();
        }
    }

    public double getPrice() {
        return this.price;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("OrderBookEntry [type=");
        builder.append(this.type);
        builder.append(", price=");
        builder.append(this.price);
        builder.append(", amount=");
        builder.append(this.amount);
        builder.append("]");
        return builder.toString();
    }

}
