package de._125m125.kt.ktapi_java.core.objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HistoryEntry {
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String                   date;
    private double                   open;
    private double                   close;
    private Double                   low;
    private Double                   high;
    private double                   unit_volume;
    private double                   dollar_volume;

    public HistoryEntry() {
        super();
    }

    public HistoryEntry(final String date, final double open, final double close, final double low, final double high,
            final double unit_value, final double dollar_value) {
        super();
        this.date = date;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.unit_volume = unit_value;
        this.dollar_volume = dollar_value;
    }

    public String getDatestring() {
        return this.date;
    }

    public LocalDate getDate() {
        return LocalDate.parse(this.date, HistoryEntry.FORMATTER);
    }

    public double getOpen() {
        return this.open;
    }

    public double getClose() {
        return this.close;
    }

    public Double getLow() {
        return this.low;
    }

    public Double getHigh() {
        return this.high;
    }

    public double getUnit_value() {
        return this.unit_volume;
    }

    public double getDollar_value() {
        return this.dollar_volume;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HistoryEntry [date=");
        builder.append(this.date);
        builder.append(", open=");
        builder.append(this.open);
        builder.append(", close=");
        builder.append(this.close);
        builder.append(", low=");
        builder.append(this.low);
        builder.append(", high=");
        builder.append(this.high);
        builder.append(", unit_volume=");
        builder.append(this.unit_volume);
        builder.append(", dollar_volume=");
        builder.append(this.dollar_volume);
        builder.append("]");
        return builder.toString();
    }

}
