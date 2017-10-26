package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload from the Bitso public API endpoint: /ticker<br/>
 */
public class Ticker {

    /** Order book symbol */
    private String book;

    /** Last 24 hours volume */
    private String volume;

    /** Last 24 hours price high */
    private String high;

    /** Last traded price */
    private String last;

    /** Last 24 hours price low */
    private String low;

    /** Last 24 hours volume weighted average price: VWAP */
    private String vwap;

    /** Lowest sell order */
    private String ask;

    /** Highest buy order */
    private String bid;

    /** Timestamp at which the ticker was generated */
    @JsonProperty("created_at")
    private String createdAt;

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(final String volume) {
        this.volume = volume;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(final String high) {
        this.high = high;
    }

    public String getLast() {
        return last;
    }

    public void setLast(final String last) {
        this.last = last;
    }

    public String getLow() {
        return low;
    }

    public void setLow(final String low) {
        this.low = low;
    }

    public String getVwap() {
        return vwap;
    }

    public void setVwap(final String vwap) {
        this.vwap = vwap;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(final String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(final String bid) {
        this.bid = bid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("Ticker [")//
                .append("book=\"")//
                .append(book).append("\"")//
                .append(",volume=\"")//
                .append(volume).append("\"")//
                .append(",high=\"")//
                .append(high).append("\"")//
                .append(",last=\"")//
                .append(last).append("\"")//
                .append(",low=\"")//
                .append(low).append("\"")//
                .append(",vwap=\"")//
                .append(vwap).append("\"")//
                .append(",ask=\"")//
                .append(ask).append("\"")//
                .append(",bid=\"")//
                .append(bid).append("\"")//
                .append(",createdAt=\"")//
                .append(createdAt).append("\"")//
                .append("]");
        return builder.toString();
    }
}
