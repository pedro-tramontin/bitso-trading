package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/17/17 5:18 PM
 */
public class BookDTO {

    private String book;

    @JsonProperty("minimum_amount")
    private String minimumAmount;

    @JsonProperty("maximum_amount")
    private String maximumAmount;

    @JsonProperty("minimum_price")
    private String minimumPrice;

    @JsonProperty("maximum_price")
    private String maximumPrice;

    @JsonProperty("minimum_value")
    private String minimumValue;

    @JsonProperty("maximum_value")
    private String maximumValue;

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(final String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(final String maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public String getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(final String minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(final String maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public String getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(final String minimumValue) {
        this.minimumValue = minimumValue;
    }

    public String getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(final String maximumValue) {
        this.maximumValue = maximumValue;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("Book [")//
                .append("book=\"")//
                .append(book).append("\"")//
                .append(",minimumAmount=\"")//
                .append(minimumAmount).append("\"")//
                .append(",maximumAmount=\"")//
                .append(maximumAmount).append("\"")//
                .append(",minimumPrice=\"")//
                .append(minimumPrice).append("\"")//
                .append(",maximumPrice=\"")//
                .append(maximumPrice).append("\"")//
                .append(",minimumValue=\"")//
                .append(minimumValue).append("\"")//
                .append(",maximumValue=\"")//
                .append(maximumValue).append("\"")//
                .append("]");
        return builder.toString();
    }
}
