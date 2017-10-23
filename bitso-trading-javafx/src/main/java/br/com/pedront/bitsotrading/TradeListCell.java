package br.com.pedront.bitsotrading;

import br.com.pedront.bitsotrading.model.Trade;
import javafx.scene.control.ListCell;

public class TradeListCell extends ListCell<Trade> {
    @Override
    protected void updateItem(Trade item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(null);
        setText(null);

        if (item != null) {
            setText(item.toString());
        }
    }
}
