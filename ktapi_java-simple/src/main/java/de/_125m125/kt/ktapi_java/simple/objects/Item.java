package de._125m125.kt.ktapi_java.simple.objects;

import com.univocity.parsers.annotations.Parsed;

public class Item {
    @Parsed
    private String id;
    @Parsed
    private String name;
    @Parsed
    private double amount;

    public Item() {
        super();
    }

    public Item(final String id, final String name, final double amount) {
        super();
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Item [id=");
        builder.append(this.id);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", amount=");
        builder.append(this.amount);
        builder.append("]");
        return builder.toString();
    }

}
