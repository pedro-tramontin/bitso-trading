package br.com.pedront.bitsotrading;

import java.net.URL;
import java.util.ResourceBundle;

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
    public ListView bestBidsListView;

    @FXML
    public ListView bestAsksListView;

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

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final TradeResponseDTO tradeResponse = bitsoApiIntegration.getTrade(book);

        final ObservableList<Trade> obsTrades = FXCollections
                .observableArrayList(TradeDTOConverter.convert(tradeResponse.getPayload()));

        tradesTableView.itemsProperty().setValue(obsTrades);
    }
}
