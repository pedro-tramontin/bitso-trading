package br.com.pedront.bitsotrading.formatter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FormattedDoubleTableCell<Trade, String> implements
    Callback<TableColumn<Trade, String>, TableCell<Trade, String>> {

    private java.lang.String pattern;

    public FormattedDoubleTableCell() {
        this.pattern = "%.2f";
    }

    public java.lang.String getPattern() {
        return pattern;
    }

    public void setPattern(java.lang.String pattern) {
        this.pattern = pattern;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableCell<Trade, String> call(TableColumn<Trade, String> p) {
        TableCell<Trade, String> cell = new TableCell<Trade, String>() {

            @Override
            public void updateItem(Object item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem((String) item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (item instanceof Double) {
                    super.setText(java.lang.String.format(getPattern(), item));
                    super.setGraphic(null);
                } else {
                    super.setText(item.toString());
                    super.setGraphic(null);
                }
            }
        };

        return cell;
    }
}