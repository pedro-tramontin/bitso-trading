package br.com.pedront.bitsotrading.formatter;

import br.com.pedront.bitsotrading.model.Trade;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

public class SimulatorRowFormatter<S, String> implements
    Callback<TableColumn<S, String>, TableCell<S, String>> {

    @Override
    public TableCell<S, String> call(TableColumn<S, String> p) {
        TableCell<S, String> cell = new TableCell<S, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem(item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    super.setText(item.toString());
                    super.setGraphic(null);
                }

                if (!isEmpty()) {
                    TableRow tableRow = getTableRow();
                    Trade trade = (Trade) tableRow.getItem();

                    if (trade != null && trade.isSimulated()) {
                        tableRow.setStyle("-fx-background-color: #f8d7da");
                    } else {
                        tableRow.setStyle("");
                    }
                }
            }
        };

        return cell;
    }
}