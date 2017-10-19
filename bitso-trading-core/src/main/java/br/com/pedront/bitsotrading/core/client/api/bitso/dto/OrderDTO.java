package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 6:12 PM
 */
public class OrderDTO {

    private String book;

    private String price;

    private String amount;

    private String oid;

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
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
                .append(price).append("\"")//
                .append(",amount=\"")//
                .append(amount).append("\"")//
                .append(",oid=\"")//
                .append(oid).append("\"")//
                .append("]");
        return builder.toString();
    }
}
