package br.com.pedront.bitsotrading;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderResponseDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.transformation.FilteredList;
import javafx.util.converter.NumberStringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeResponseDTO;
import br.com.pedront.bitsotrading.model.Trade;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

@FXMLController
public class DashboardController implements Initializable {

    private static final String book = "btc_mxn";

    @FXML
    public ListView<OrderDTO> bestBidsListView;

    @FXML
    public ListView<OrderDTO> bestAsksListView;

    @FXML
    public TableView<Trade> tradesTableView;

    @FXML
    public TextField xTextField;

    @FXML
    public TextField mTextField;

    @FXML
    public TextField nTextField;

    @FXML
    public Button simulatorToggleButton;

    private final SimpleIntegerProperty xInteger = new SimpleIntegerProperty();

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Bindings.bindBidirectional(xTextField.textProperty(), xInteger, new NumberStringConverter());

        final TradeResponseDTO tradeResponse = bitsoApiIntegration.getTrade(book, null);

        final ObservableList<Trade> obsTrades = FXCollections
                .observableArrayList(TradeDTOConverter.convert(tradeResponse.getPayload()));

        FilteredList<Trade> filtered = new FilteredList<Trade>(obsTrades);
        filtered.setPredicate(p -> {
            System.out.println(p);

            if (filtered.size() < xInteger.get()) {
                System.out.println("true");

                return true;
            }

            System.out.println("false");
            return false;
        });

        tradesTableView.setItems(filtered);


        OrderResponseDTO orderResponse = bitsoApiIntegration.getOrder(book);

        ObservableList<OrderDTO> obsAsks = FXCollections.observableArrayList(orderResponse.getPayload().getAsks());
        ObservableList<OrderDTO> obsBids = FXCollections.observableArrayList(orderResponse.getPayload().getBids());

        bestAsksListView.setItems(obsAsks);
        bestBidsListView.setItems(obsBids);

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                xTextField.setText("0");

                return;
            }

            try {
                Integer newValueInteger = Integer.parseInt(newValue);

                xTextField.setText(newValueInteger.toString());
            } catch (NumberFormatException e) {
                xTextField.setText(oldValue);
            }
        });

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(String.format("oldvalue: %s, newValue: %s", oldValue, newValue));
        });
    }
}
