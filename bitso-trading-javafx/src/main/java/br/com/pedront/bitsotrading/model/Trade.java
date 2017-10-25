package br.com.pedront.bitsotrading.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/20/17 4:31 PM
 */
public class Trade {
    private final SimpleStringProperty createdAt = new SimpleStringProperty("");
    private final SimpleStringProperty makerSide = new SimpleStringProperty("");
    private final SimpleDoubleProperty amount = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty price = new SimpleDoubleProperty(0.0);
    private final SimpleIntegerProperty tid = new SimpleIntegerProperty(0);
    private final SimpleBooleanProperty simulated = new SimpleBooleanProperty(false);

    public Trade() {
        this("", "", 0.0, 0.0, 0);
    }

    public Trade(String createdAt, String makerSide, Double amount, Double price, Integer tid) {
        setCreatedAt(createdAt);
        setMakerSide(makerSide);
        setAmount(amount);
        setPrice(price);
        setTid(tid);
        setSimulated(false);
    }

    public Trade(String createdAt, String makerSide, Double amount, Double price, Integer tid, Boolean simulated) {
        setCreatedAt(createdAt);
        setMakerSide(makerSide);
        setAmount(amount);
        setPrice(price);
        setTid(tid);
        setSimulated(simulated);
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

    public void setTid(final int tid) {
        this.tid.set(tid);
    }

    public boolean isSimulated() {
        return simulated.get();
    }

    public SimpleBooleanProperty simulatedProperty() {
        return simulated;
    }

    public void setSimulated(final boolean simulated) {
        this.simulated.set(simulated);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("Trade [")//
                .append("createdAt=")//
                .append(createdAt)//
                .append(",makerSide=")//
                .append(makerSide)//
                .append(",amount=")//
                .append(String.format("%.8f", amount.get()))//
                .append(",price=")//
                .append(String.format("%.2f", price.get()))//
                .append(",tid=")//
                .append(tid)//
                .append(",simulated=")//
                .append(simulated)//
                .append("]");
        return builder.toString();
    }
}
