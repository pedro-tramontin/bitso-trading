package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 6:26 PM
 */
public class TradeDTO {

    private String book;

    @JsonProperty("created_at")
    private String createdAt;

    private String amount;

    @JsonProperty("maker_side")
    private String makerSide;

    private String price;

    private String tid;

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getMakerSide() {
        return makerSide;
    }

    public void setMakerSide(final String makerSide) {
        this.makerSide = makerSide;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(final String tid) {
        this.tid = tid;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("TradeDTO [")//
                .append("book=\"")//
                .append(book).append("\"")//
                .append(",createdAt=\"")//
                .append(createdAt).append("\"")//
                .append(",amount=\"")//
                .append(amount).append("\"")//
                .append(",makerSide=\"")//
                .append(makerSide).append("\"")//
                .append(",price=\"")//
                .append(price).append("\"")//
                .append(",tid=\"")//
                .append(tid).append("\"")//
                .append("]");
        return builder.toString();
    }
}
