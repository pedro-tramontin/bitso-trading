package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

/**
 * Order payload from the Bitso public API endpoint: /order_book<br/>
 */
public class Order {

    /** Order book symbol */
    private String book;

    /** Price per unit of major */
    private Double price;

    /** Major amount in order */
    private Double amount;

    /** Order ID */
    private String oid;

    public Order() {
    }

    public Order(String book, Double price, Double amount, String oid) {
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
                .append("Order [")//
                .append("book=\"")//
                .append(book).append("\"")//
                .append(",price=")//
                .append(price)//
                .append(",amount=")//
                .append(amount)//
                .append(",oid=\"")//
                .append(oid).append("\"")//
                .append("]");
        return builder.toString();
    }
}
