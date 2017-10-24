package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 6:12 PM
 */
public class OrderDTO {

    public static final OrderDTO NULL_ORDER_DTO = new OrderDTO();

    private String book;

    private Double price;

    private Double amount;

    private String oid;

    public OrderDTO() {
    }

    public OrderDTO(String book, Double price, Double amount, String oid) {
        this.book = book;
        this.price = price;
        this.amount = amount;
        this.oid = oid;
    }

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(final Double amount) {
        this.amount = amount;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(final String oid) {
        this.oid = oid;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("OrderDTO [")//
                .append("book=\"")//
                .append(book).append("\"")//
                .append(",price=\"")//
                .append(String.format("%.2f", price)).append("\"")//
                .append(",amount=\"")//
                .append(String.format("%.8f", amount)).append("\"")//
                .append(",oid=\"")//
                .append(oid).append("\"")//
                .append("]");
        return builder.toString();
    }
}
