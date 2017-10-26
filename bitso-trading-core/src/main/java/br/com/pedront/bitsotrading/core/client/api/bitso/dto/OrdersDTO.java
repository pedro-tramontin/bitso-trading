package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 6:14 PM
 */
public class OrdersDTO {

    private List<OrderDTO> asks;

    private List<OrderDTO> bids;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String sequence;

    public List<OrderDTO> getAsks() {
        return asks;
    }

    public void setAsks(final List<OrderDTO> asks) {
        this.asks = asks;
    }

    public List<OrderDTO> getBids() {
        return bids;
    }

    public void setBids(final List<OrderDTO> bids) {
        this.bids = bids;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("OrdersDTO [")//
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
