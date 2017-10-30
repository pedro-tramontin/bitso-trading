package br.com.pedront.bitsotrading.formatter;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Formatter used for double values.<br/>
 * By default it uses the pattern '%.2f', but it can be informed in jxml by using the parameter <code>pattern</code>.
 */
public class FormattedDoubleTableCell<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private static final String DEFAULT_DOUBLE_PATTERN = "%.2f";

    /** Pattern to format the double value */
    private String pattern;

    public FormattedDoubleTableCell() {
        this.pattern = DEFAULT_DOUBLE_PATTERN;
    }

    /** @noinspection WeakerAccess */
    public String getPattern() {
        return pattern;
    }

    /** @noinspection unused */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public TableCell<S, T> call(TableColumn<S, T> column) {
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
                } else if (item instanceof Double) {
                    super.setText(String.format(getPattern(), item));
                    super.setGraphic(null);
                } else {
                    super.setText(item.toString());
                    super.setGraphic(null);
                }
            }
        };
    }
}