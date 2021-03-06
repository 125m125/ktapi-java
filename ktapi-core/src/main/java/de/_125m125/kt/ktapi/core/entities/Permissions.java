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

public class Permissions {
    public static final Permissions NO_PERMISSIONS = new Permissions(false, false, false, false,
            false, false);
    // @CHECKSTYLE:OFF naming required to allow parsing/encoding of messages
    private boolean                 rPayouts;
    private boolean                 wOrders;
    private boolean                 rMessages;
    private boolean                 wPayouts;
    private boolean                 rItems;
    private boolean                 rOrders;
    // @CHECKSTYLE:ON

    protected Permissions() {

    }

    public Permissions(final boolean readPayouts, final boolean writeOrders,
            final boolean readMessages, final boolean writePayouts, final boolean readItems,
            final boolean readOrders) {
        super();
        this.rPayouts = readPayouts;
        this.wOrders = writeOrders;
        this.rMessages = readMessages;
        this.wPayouts = writePayouts;
        this.rItems = readItems;
        this.rOrders = readOrders;
    }

    public boolean mayReadPayouts() {
        return this.rPayouts;
    }

    public boolean mayWriteOrders() {
        return this.wOrders;
    }

    public boolean mayReadMessages() {
        return this.rMessages;
    }

    public boolean mayWritePayouts() {
        return this.wPayouts;
    }

    public boolean mayReadItems() {
        return this.rItems;
    }

    public boolean mayReadOrders() {
        return this.rOrders;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Permissions [rPayouts=");
        builder.append(this.rPayouts);
        builder.append(", wOrders=");
        builder.append(this.wOrders);
        builder.append(", rMessages=");
        builder.append(this.rMessages);
        builder.append(", wPayouts=");
        builder.append(this.wPayouts);
        builder.append(", rItems=");
        builder.append(this.rItems);
        builder.append(", rOrders=");
        builder.append(this.rOrders);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.rItems ? 1231 : 1237);
        result = prime * result + (this.rMessages ? 1231 : 1237);
        result = prime * result + (this.rOrders ? 1231 : 1237);
        result = prime * result + (this.rPayouts ? 1231 : 1237);
        result = prime * result + (this.wOrders ? 1231 : 1237);
        result = prime * result + (this.wPayouts ? 1231 : 1237);
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
        final Permissions other = (Permissions) obj;
        if (this.rItems != other.rItems) {
            return false;
        }
        if (this.rMessages != other.rMessages) {
            return false;
        }
        if (this.rOrders != other.rOrders) {
            return false;
        }
        if (this.rPayouts != other.rPayouts) {
            return false;
        }
        if (this.wOrders != other.wOrders) {
            return false;
        }
        if (this.wPayouts != other.wPayouts) {
            return false;
        }
        return true;
    }

}
