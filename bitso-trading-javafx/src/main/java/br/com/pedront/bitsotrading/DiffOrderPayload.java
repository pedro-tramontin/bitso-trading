package br.com.pedront.bitsotrading;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiffOrderPayload {

    @JsonProperty("d")
    private String timestamp;

    @JsonProperty("r")
    private Double rate;

    @JsonProperty("t")
    private Integer makerSide;

    @JsonProperty("a")
    private Double amount;

    @JsonProperty("v")
    private Double value;

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

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getMakerSide() {
        return makerSide;
    }

    public void setMakerSide(Integer makerSide) {
        this.makerSide = makerSide;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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
