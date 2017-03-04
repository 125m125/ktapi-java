package de._125m125.kt.ktapi_java.core.objects;

public class Payout {
    private long   id;
    private String material;
    private String materialName;
    private double amount;
    private String state;
    private String payoutType;
    private String date;
    private String message;

    public Payout() {
        super();
    }

    public Payout(final long id, final String material, final String materialName, final double amount,
            final String state, final String payoutType, final String date, final String message) {
        super();
        this.id = id;
        this.material = material;
        this.materialName = materialName;
        this.amount = amount;
        this.state = state;
        this.payoutType = payoutType;
        this.date = date;
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
        return this.message;
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

}
