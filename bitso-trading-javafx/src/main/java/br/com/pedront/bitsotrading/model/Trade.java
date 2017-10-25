package br.com.pedront.bitsotrading.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 * $Id: $
 * @since 10/20/17 4:31 PM
 */
public class Trade {
    private final SimpleStringProperty createdAt = new SimpleStringProperty("");
    private final SimpleStringProperty makerSide = new SimpleStringProperty("");
    private final SimpleDoubleProperty amount = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty price = new SimpleDoubleProperty(0.0);
    private final SimpleIntegerProperty tid = new SimpleIntegerProperty(0);

    public Trade() {
        this("", "", 0.0, 0.0, 0);
    }

    public Trade(String createdAt, String makerSide, Double amount, Double price, Integer tid) {
        setCreatedAt(createdAt);
        setMakerSide(makerSide);
        setAmount(amount);
        setPrice(price);
        setTid(tid);
    }

    public String getCreatedAt() {
        return createdAt.get();
    }

    public SimpleStringProperty createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt.set(createdAt);
    }

    public String getMakerSide() {
        return makerSide.get();
    }

    public SimpleStringProperty makerSideProperty() {
        return makerSide;
    }

    public void setMakerSide(String makerSide) {
        this.makerSide.set(makerSide);
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public Integer getTid() {
        return tid.get();
    }

    public SimpleIntegerProperty tidProperty() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid.set(tid);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "createdAt=" + createdAt +
                ", makerSide=" + makerSide +
                ", amount=" + String.format("%.8f", amount) +
                ", price=" + String.format("%.2f", price) +
                ", tid=" + tid +
                '}';
    }
}
