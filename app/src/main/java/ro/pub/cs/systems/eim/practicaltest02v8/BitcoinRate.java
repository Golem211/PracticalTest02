package ro.pub.cs.systems.eim.practicaltest02v8;

public class BitcoinRate {
    private String updated;
    private String usdRate;
    private String eurRate;

    public BitcoinRate(String updated, String usdRate, String eurRate) {
        this.updated = updated;
        this.usdRate = usdRate;
        this.eurRate = eurRate;
    }

    public String getUpdated() {
        return updated;
    }

    public String getUsdRate() {
        return usdRate;
    }

    public String getEurRate() {
        return eurRate;
    }
}