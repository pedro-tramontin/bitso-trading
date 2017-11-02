package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload from the Bitso public API endpoint: /trades<br/>
 */
public class TradeDTO {

    /** Order book symbol */
    private String book;

    /** Timestamp at which the trade was executed */
    @JsonProperty("created_at")
    private String createdAt;

    /** Major amount transacted */
    private Double amount;

    /** Indicates the maker order side (maker order is the order that was open on the order book) */
    @JsonProperty("maker_side")
    private String makerSide;

    /** Price per unit of Bitcoin */
    private Double price;

    /** TradeDTO ID */
    private Long tid;

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(final Double amount) {
        this.amount = amount;
    }

    public String getMakerSide() {
        return makerSide;
    }

    public void setMakerSide(final String makerSide) {
        this.makerSide = makerSide;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(final Long tid) {
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
                .append(",amount=")//
                .append(amount)//
                .append(",makerSide=\"")//
                .append(makerSide).append("\"")//
                .append(",price=")//
                .append(price)//
                .append(",tid=")//
                .append(tid)//
                .append("]");
        return builder.toString();
    }
}
