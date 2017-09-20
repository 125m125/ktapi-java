package de._125m125.kt.ktapi_java.core.entities;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.amount;
        long temp;
        temp = Double.doubleToLongBits(this.price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrderBookEntry other = (OrderBookEntry) obj;
        if (this.amount != other.amount) {
            return false;
        }
        if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
