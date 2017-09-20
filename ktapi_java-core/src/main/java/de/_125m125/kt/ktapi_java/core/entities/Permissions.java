package de._125m125.kt.ktapi_java.core.entities;

public class Permissions {
    private boolean rPayouts;
    private boolean wOrders;
    private boolean rMessages;
    private boolean wPayouts;
    private boolean rItems;
    private boolean rOrders;

    public Permissions() {

    }

    public Permissions(final boolean rPayouts, final boolean wOrders, final boolean rMessages, final boolean wPayouts,
            final boolean rItems, final boolean rOrders) {
        super();
        this.rPayouts = rPayouts;
        this.wOrders = wOrders;
        this.rMessages = rMessages;
        this.wPayouts = wPayouts;
        this.rItems = rItems;
        this.rOrders = rOrders;
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
