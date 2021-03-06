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

public class Payout {
    private long   id;
    private String material;
    private String materialName;
    private double amount;
    private String state;
    private String payoutType;
    private String date;
    private String message;

    protected Payout() {
        super();
    }

    public Payout(final long id, final String material, final String materialName,
            final double amount, final String state, final String payoutType, final String date,
            final String message) {
        super();
        this.id = id;
        this.material = Objects.requireNonNull(material);
        this.materialName = materialName != null ? materialName : material;
        this.amount = amount;
        this.state = Objects.requireNonNull(state);
        this.payoutType = Objects.requireNonNull(payoutType);
        this.date = Objects.requireNonNull(date);
        this.message = message;
    }

    public long getId() {
        return this.id;
    }

    public String getMaterial() {
        return this.material;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getState() {
        return this.state;
    }

    public String getPayoutType() {
        return this.payoutType;
    }

    public String getDate() {
        return this.date;
    }

    public String getMessage() {
        return this.message != null ? message : null;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Payout [id=");
        builder.append(this.id);
        builder.append(", material=");
        builder.append(this.material);
        builder.append(", materialName=");
        builder.append(this.materialName);
        builder.append(", amount=");
        builder.append(this.amount);
        builder.append(", state=");
        builder.append(this.state);
        builder.append(", payoutType=");
        builder.append(this.payoutType);
        builder.append(", date=");
        builder.append(this.date);
        builder.append(", message=");
        builder.append(this.message);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.amount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.date == null) ? 0 : this.date.hashCode());
        result = prime * result + (int) (this.id ^ (this.id >>> 32));
        result = prime * result + ((this.material == null) ? 0 : this.material.hashCode());
        result = prime * result + ((this.materialName == null) ? 0 : this.materialName.hashCode());
        result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
        result = prime * result + ((this.payoutType == null) ? 0 : this.payoutType.hashCode());
        result = prime * result + ((this.state == null) ? 0 : this.state.hashCode());
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
        final Payout other = (Payout) obj;
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        if (this.date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!this.date.equals(other.date)) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        if (this.material == null) {
            if (other.material != null) {
                return false;
            }
        } else if (!this.material.equals(other.material)) {
            return false;
        }
        if (this.materialName == null) {
            if (other.materialName != null) {
                return false;
            }
        } else if (!this.materialName.equals(other.materialName)) {
            return false;
        }
        if (this.message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!this.message.equals(other.message)) {
            return false;
        }
        if (this.payoutType == null) {
            if (other.payoutType != null) {
                return false;
            }
        } else if (!this.payoutType.equals(other.payoutType)) {
            return false;
        }
        if (this.state == null) {
            if (other.state != null) {
                return false;
            }
        } else if (!this.state.equals(other.state)) {
            return false;
        }
        return true;
    }

}
