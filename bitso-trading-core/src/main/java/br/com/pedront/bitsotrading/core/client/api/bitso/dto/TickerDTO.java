package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 5:41 PM
 */
public class TickerDTO {

    private String book;

    private String volume;

    private String high;

    private String last;

    private String low;

    private String vwap;

    private String ask;

    private String bid;

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
                .append("TickerDTO [")//
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
