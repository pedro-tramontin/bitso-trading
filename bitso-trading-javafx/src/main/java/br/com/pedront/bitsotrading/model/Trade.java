package br.com.pedront.bitsotrading.model;

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
    private final SimpleStringProperty amount = new SimpleStringProperty("");
    private final SimpleStringProperty price = new SimpleStringProperty("");

    public Trade() {
        this("", "", "", "");
    }

    public Trade(String createdAt, String makerSide, String amount, String price) {
        setCreatedAt(createdAt);
        setMakerSide(makerSide);
        setAmount(amount);
        setPrice(price);
    }

    public SimpleStringProperty createdAtProperty() {
        return createdAt;
    }

    public SimpleStringProperty makerSideProperty() {
        return makerSide;
    }

    public SimpleStringProperty amountProperty() {
        return amount;
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public String getCreatedAt() {
        return createdAtProperty().get();
    }

    public void setCreatedAt(final String createdAt) {
        createdAtProperty().set(createdAt);
    }

    public String getMakerSide() {
        return makerSideProperty().get();
    }

    public void setMakerSide(final String makerSide) {
        makerSideProperty().set(makerSide);
    }

    public String getAmount() {
        return amountProperty().get();
    }

    public void setAmount(final String amount) {
        amountProperty().set(amount);
    }

    public String getPrice() {
        return priceProperty().get();
    }

    public void setPrice(final String price) {
        priceProperty().set(price);
    }
}
