package de._125m125.kt.ktapi_java.core.objects;

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

}
