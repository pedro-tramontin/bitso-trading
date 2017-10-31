package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload from the Bitso public API endpoint: /order_book<br/>
 */
public class OrderBook {

    public static final OrderBook EMPTY = initNullOrderBook();

    private static OrderBook initNullOrderBook() {
        OrderBook orderBook = new OrderBook();
        orderBook.setAsks(Collections.emptyList());
        orderBook.setBids(Collections.emptyList());

        return orderBook;
    }

    /**
     * List of open asks
     */
    private List<Order> asks;

    /**
     * List of open bids
     */
    private List<Order> bids;

    /**
     * Timestamp at which the order was last updated
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * Increasing integer value for each order book update.
     */
    private Long sequence;

    public List<Order> getAsks() {
        return asks;
    }

    public void setAsks(final List<Order> asks) {
        this.asks = asks;
    }

    public List<Order> getBids() {
        return bids;
    }

    public void setBids(final List<Order> bids) {
        this.bids = bids;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(final Long sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
            .append("OrderBook [")//
            .append("asks=")//
            .append(asks)//
            .append(",bids=")//
            .append(bids)//
            .append(",updatedAt=\"")//
            .append(updatedAt).append("\"")//
            .append(",sequence=\"")//
            .append(sequence).append("\"")//
            .append("]");
        return builder.toString();
    }
}
