import java.math.BigDecimal;
import java.math.RoundingMode;

public class TDXRecord {
    private long date;
    private long open;
    private long high;
    private long low;
    private long close;
    private float amount;
    private long vol;
    private long preClose;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public BigDecimal getOpen() {
        return new BigDecimal(open * 0.01).setScale(2, RoundingMode.HALF_UP);
    }

    public void setOpen(long open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return new BigDecimal(high * 0.01).setScale(2, RoundingMode.HALF_UP);
    }

    public void setHigh(long high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return new BigDecimal(low * 0.01).setScale(2, RoundingMode.HALF_UP);
    }

    public void setLow(long low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return new BigDecimal(close * 0.01).setScale(2, RoundingMode.HALF_UP);
    }

    public void setClose(long close) {
        this.close = close;
    }

    public BigDecimal getAmount() {
        return new BigDecimal(amount * 0.01).setScale(2, RoundingMode.HALF_UP);
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public long getVol() {
        return vol;
    }

    public void setVol(long vol) {
        this.vol = vol;
    }

    public long getPreClose() {
        return preClose;
    }

    public void setPreClose(long preClose) {
        this.preClose = preClose;
    }

    @Override
    public String toString() {
        return "date=" + getDate() + ", open=" + getOpen() + ", high=" + getHigh() + ", low=" + getLow() + ", close="
                + getClose() + ", amount=" + getAmount() + ", volume=" + getVol() + ", preclose=" + getPreClose();
    }

}
