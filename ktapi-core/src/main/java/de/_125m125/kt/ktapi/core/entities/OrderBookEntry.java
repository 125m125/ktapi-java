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

import java.util.Objects;

import de._125m125.kt.ktapi.core.BuySell;

public class OrderBookEntry {
    private String type;
    private double price;
    private int    amount;

    protected OrderBookEntry() {
        super();
    }

    public OrderBookEntry(final String type, final double price, final int amount) {
        super();
        this.type = Objects.requireNonNull(type);
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

    public BuySell getBuySell() {
        if (isBuying()) {
            return BuySell.BUY;
        } else if (isSelling()) {
            return BuySell.SELL;
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
