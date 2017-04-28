package de._125m125.kt.ktapi_java.core.objects;

import de._125m125.kt.ktapi_java.core.BUY_SELL;

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

    public Trade() {
        super();
    }

    public Trade(final long id, final boolean buySell, final String materialId, final String materialName,
            final int amount, final double price, final int sold, final double toTakeM, final int toTakeI,
            final boolean cancelled) {
        super();
        this.id = id;
        this.buy = buySell;
        this.materialId = materialId;
        this.materialName = materialName;
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

    public BUY_SELL getBuySell() {
        return this.buy ? BUY_SELL.BUY : BUY_SELL.SELL;
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

}
