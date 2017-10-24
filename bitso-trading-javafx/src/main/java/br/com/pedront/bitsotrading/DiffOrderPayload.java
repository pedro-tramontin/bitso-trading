package br.com.pedront.bitsotrading;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiffOrderPayload {

    @JsonProperty("d")
    private String timestamp;

    @JsonProperty("r")
    private String rate;

    @JsonProperty("t")
    private String makerSide;

    @JsonProperty("a")
    private String amount;

    @JsonProperty("v")
    private String value;

    @JsonProperty("s")
    private String status;

    @JsonProperty("o")
    private String oid;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMakerSide() {
        return makerSide;
    }

    public void setMakerSide(String makerSide) {
        this.makerSide = makerSide;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @Override
    public String toString() {
        return "DiffOrderPayload{" +
                "timestamp='" + timestamp + '\'' +
                ", rate='" + rate + '\'' +
                ", makerSide='" + makerSide + '\'' +
                ", amount='" + amount + '\'' +
                ", value='" + value + '\'' +
                ", status='" + status + '\'' +
                ", oid='" + oid + '\'' +
                '}';
    }
}
