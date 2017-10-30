package br.com.pedront.bitsotrading.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A Trade model to be used in the TableView in JavaFX.
 */
public class Trade {

    /** Created at */
    private final SimpleStringProperty createdAt = new SimpleStringProperty("");

    /** Maker side: bid or sell */
    private final SimpleStringProperty makerSide = new SimpleStringProperty("");

    /** Amount */
    private final SimpleDoubleProperty amount = new SimpleDoubleProperty(0.0);

    /** Price */
    private final SimpleDoubleProperty price = new SimpleDoubleProperty(0.0);

    /** Trade ID */
    private final SimpleIntegerProperty tid = new SimpleIntegerProperty(0);

    /** Flag to show if this Trade is simulated or real */
    private final SimpleBooleanProperty simulated = new SimpleBooleanProperty(false);

    public Trade(String createdAt, String makerSide, Double amount, Double price, Integer tid) {
        setCreatedAt(createdAt);
        setMakerSide(makerSide);
        setAmount(amount);
        setPrice(price);
        setTid(tid);
        setSimulated(false);
    }

    public Trade(Trade trade, String makerSide, Double amount) {
        setCreatedAt(trade.getCreatedAt());
        setMakerSide(makerSide);
        setAmount(amount);
        setPrice(trade.getPrice());
        setTid(trade.getTid());
        setSimulated(true);
    }

    public String getCreatedAt() {
        return createdAt.get();
    }

    /** @noinspection unused */
    public SimpleStringProperty createdAtProperty() {
        return createdAt;
    }

    /** @noinspection WeakerAccess */
    public void setCreatedAt(String createdAt) {
        this.createdAt.set(createdAt);
    }

    /** @noinspection unused */
    public String getMakerSide() {
        return makerSide.get();
    }

    /** @noinspection unused */
    public SimpleStringProperty makerSideProperty() {
        return makerSide;
    }

    /** @noinspection WeakerAccess */
    public void setMakerSide(String makerSide) {
        this.makerSide.set(makerSide);
    }

    /** @noinspection unused */
    public double getAmount() {
        return amount.get();
    }

    /** @noinspection unused */
    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    /** @noinspection WeakerAccess */
    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public double getPrice() {
        return price.get();
    }

    /** @noinspection unused */
    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    /** @noinspection WeakerAccess */
    public void setPrice(double price) {
        this.price.set(price);
    }

    public Integer getTid() {
        return tid.get();
    }

    /** @noinspection unused */
    public SimpleIntegerProperty tidProperty() {
        return tid;
    }

    /** @noinspection WeakerAccess */
    public void setTid(Integer tid) {
        this.tid.set(tid);
    }

    /** @noinspection unused */
    public void setTid(final int tid) {
        this.tid.set(tid);
    }

    public boolean isSimulated() {
        return simulated.get();
    }

    /** @noinspection unused */
    public SimpleBooleanProperty simulatedProperty() {
        return simulated;
    }

    /** @noinspection WeakerAccess */
    public void setSimulated(final boolean simulated) {
        this.simulated.set(simulated);
    }

    @Override
    public String toString() {
        return "Trade [" + //
                "createdAt=" + //
                createdAt + //
                ",makerSide=" + //
                makerSide + //
                ",amount=" + //
                amount + //
                ",price=" + //
                price + //
                ",tid=" + //
                tid + //
                ",simulated=" + //
                simulated + //
                "]";
    }
}
