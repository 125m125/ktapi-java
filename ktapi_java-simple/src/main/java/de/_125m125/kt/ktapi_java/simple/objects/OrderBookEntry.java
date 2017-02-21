package de._125m125.kt.ktapi_java.simple.objects;

import com.univocity.parsers.annotations.Parsed;

import de._125m125.kt.ktapi_java.simple.Kt.BUY_SELL;

public class OrderBookEntry {
    @Parsed
    private String type;
    @Parsed
    private double price;
    @Parsed
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
