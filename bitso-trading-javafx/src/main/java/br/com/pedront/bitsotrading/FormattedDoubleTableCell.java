package br.com.pedront.bitsotrading;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.Normalizer;

public class FormattedDoubleTableCell<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private String pattern;

    public FormattedDoubleTableCell() {
        this.pattern = "%.2f";
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableCell<S, T> call(TableColumn<S, T> p) {
        TableCell<S, T> cell = new TableCell<S, T>() {

            @Override
            public void updateItem(Object item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem((T) item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (item instanceof Double) {
                    super.setText(String.format(getPattern(), item));
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