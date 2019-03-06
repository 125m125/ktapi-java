package de._125m125.kt.ktapi.core.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HistoryEntry {
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String                   date;
    private double                   open;
    private double                   close;
    private Double                   low;
    private Double                   high;
    private int                      unitVolume;
    private double                   dollarVolume;

    protected HistoryEntry() {
        super();
    }

    public HistoryEntry(final String date, final double open, final double close, final Double low,
            final Double high, final int unitVolume, final double dollarVolume) {
        super();
        this.date = Objects.requireNonNull(date);
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.unitVolume = unitVolume;
        this.dollarVolume = dollarVolume;
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

    public int getUnitVolume() {
        return this.unitVolume;
    }

    public double getDollarVolume() {
        return this.dollarVolume;
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
        builder.append(this.unitVolume);
        builder.append(", dollar_volume=");
        builder.append(this.dollarVolume);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.close);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.date == null) ? 0 : this.date.hashCode());
        temp = Double.doubleToLongBits(this.dollarVolume);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.high == null) ? 0 : this.high.hashCode());
        result = prime * result + ((this.low == null) ? 0 : this.low.hashCode());
        temp = Double.doubleToLongBits(this.open);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.unitVolume);
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
        final HistoryEntry other = (HistoryEntry) obj;
        if (Double.doubleToLongBits(this.close) != Double.doubleToLongBits(other.close)) {
            return false;
        }
        if (this.date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!this.date.equals(other.date)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dollarVolume) != Double
                .doubleToLongBits(other.dollarVolume)) {
            return false;
        }
        if (this.high == null) {
            if (other.high != null) {
                return false;
            }
        } else if (!this.high.equals(other.high)) {
            return false;
        }
        if (this.low == null) {
            if (other.low != null) {
                return false;
            }
        } else if (!this.low.equals(other.low)) {
            return false;
        }
        if (Double.doubleToLongBits(this.open) != Double.doubleToLongBits(other.open)) {
            return false;
        }
        if (Double.doubleToLongBits(this.unitVolume) != Double.doubleToLongBits(other.unitVolume)) {
            return false;
        }
        return true;
    }

}
