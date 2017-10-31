package br.com.pedront.bitsotrading.formatter;

import br.com.pedront.bitsotrading.model.Trade;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

/**
 * Formatter for a TableRow with simulated Trade.<br/> Displays the background with a different
 * color.
 */
public class SimulatorRowFormatter<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> p) {
        return new TableCell<S, T>() {

            @Override
            public void updateItem(T item, boolean empty) {
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

                TableRow tableRow = getTableRow();
                Trade trade = (Trade) tableRow.getItem();

                if (trade != null && trade.isSimulated()) {
                    if ("".equals(tableRow.getStyle())) {
                        //tableRow.setStyle("-fx-background-color: #f8d7da");
                        tableRow.getStyleClass().clear();
                        tableRow.getStyleClass().add("table-row-simulated");

                        forceRowRedraw(tableRow);
                    }
                } else {
                    if (!"".equals(tableRow.getStyle())) {
                        tableRow.setStyle("");

                        forceRowRedraw(tableRow);
                    }
                }
            }
        };
    }

    private static void forceRowRedraw(TableRow tableRow) {
        tableRow.setVisible(false);
        tableRow.setVisible(true);
    }
}