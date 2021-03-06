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

public class Trade {
    private long    id;
    private boolean buy;
    private String  materialId;
    private String  materialName;
    private int     amount;
    private double  price;
    private int     sold;
    private double  toTakeM;
    private int     toTakeI;
    private boolean cancelled;

    protected Trade() {
        super();
    }

    public Trade(final long id, final boolean buySell, final String materialId,
            final String materialName, final int amount, final double price, final int sold,
            final double toTakeM, final int toTakeI, final boolean cancelled) {
        super();
        this.id = id;
        this.buy = buySell;
        this.materialId = Objects.requireNonNull(materialId);
        this.materialName = materialName != null ? materialName : materialId;
        this.amount = amount;
        this.price = price;
        this.sold = sold;
        this.toTakeM = toTakeM;
        this.toTakeI = toTakeI;
        this.cancelled = cancelled;
    }

    public long getId() {
        return this.id;
    }

    public BuySell getBuySell() {
        return this.buy ? BuySell.BUY : BuySell.SELL;
    }

    public boolean isBuySell() {
        return this.buy;
    }

    public boolean isBuy() {
        return this.buy;
    }

    public boolean isSell() {
        return !this.buy;
    }

    public String getMaterialId() {
        return this.materialId;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public int getAmount() {
        return this.amount;
    }

    public double getPrice() {
        return this.price;
    }

    public int getSold() {
        return this.sold;
    }

    public double getToTakeMoney() {
        return this.toTakeM;
    }

    public int getToTakeItems() {
        return this.toTakeI;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Trade [id=");
        builder.append(this.id);
        builder.append(", buySell=");
        builder.append(this.buy);
        builder.append(", materialId=");
        builder.append(this.materialId);
        builder.append(", materialName=");
        builder.append(this.materialName);
        builder.append(", amount=");
        builder.append(this.amount);
        builder.append(", price=");
        builder.append(this.price);
        builder.append(", sold=");
        builder.append(this.sold);
        builder.append(", toTakeM=");
        builder.append(this.toTakeM);
        builder.append(", toTakeI=");
        builder.append(this.toTakeI);
        builder.append(", cancelled=");
        builder.append(this.cancelled);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.amount;
        result = prime * result + (this.buy ? 1231 : 1237);
        result = prime * result + (this.cancelled ? 1231 : 1237);
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        result = prime * result + ((this.materialId == null) ? 0 : this.materialId.hashCode());
        result = prime * result + ((this.materialName == null) ? 0 : this.materialName.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + this.sold;
        result = prime * result + this.toTakeI;
        temp = Double.doubleToLongBits(this.toTakeM);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        final Trade other = (Trade) obj;
        if (this.amount != other.amount) {
            return false;
        }
        if (this.buy != other.buy) {
            return false;
        }
        if (this.cancelled != other.cancelled) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        if (this.materialId == null) {
            if (other.materialId != null) {
                return false;
            }
        } else if (!this.materialId.equals(other.materialId)) {
            return false;
        }
        if (this.materialName == null) {
            if (other.materialName != null) {
                return false;
            }
        } else if (!this.materialName.equals(other.materialName)) {
            return false;
        }
        if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (this.sold != other.sold) {
            return false;
        }
        if (this.toTakeI != other.toTakeI) {
            return false;
        }
        if (Double.doubleToLongBits(this.toTakeM) != Double.doubleToLongBits(other.toTakeM)) {
            return false;
        }
        return true;
    }

}
