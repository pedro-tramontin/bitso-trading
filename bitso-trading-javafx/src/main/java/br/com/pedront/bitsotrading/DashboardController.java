package br.com.pedront.bitsotrading;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeResponseDTO;
import br.com.pedront.bitsotrading.model.Trade;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

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

        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, final EndpointConfig config) {
                    session.addMessageHandler(new MessageHandler.Whole<String>() {

                        @Override
                        public void onMessage(final String message) {
                            System.out.println(message);
                        }
                    });
                }
            }, config, new URI("wss://ws.bitso.com"));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        final TradeResponseDTO tradeResponse = bitsoApiIntegration.getTrade(book, null);
        final OrderResponseDTO orderResponse = bitsoApiIntegration.getOrder(book);

        Bindings.bindBidirectional(xTextField.textProperty(), xInteger, new NumberStringConverter());
        xTextField.textProperty().set("5");

        // Created the observable list, filters and sets to the Trades TableView
        final ObservableList<Trade> obsTrades = FXCollections
                .observableArrayList(TradeDTOConverter.convert(tradeResponse.getPayload()));
        final FilteredList<Trade> filteredTrades = obsTrades.filtered(p -> obsTrades.indexOf(p) < xInteger.get());
        tradesTableView.setItems(filteredTrades);

        // Created the observable list, filters and sets to the Asks TableView
        final ObservableList<OrderDTO> obsAsks = FXCollections
                .observableArrayList(orderResponse.getPayload().getAsks());
        final FilteredList<OrderDTO> filteredAsks = obsAsks.filtered(p -> obsAsks.indexOf(p) < xInteger.get());
        bestAsksListView.setItems(filteredAsks);

        // Created the observable list, filters and sets to the Bids TableView
        final ObservableList<OrderDTO> obsBids = FXCollections
                .observableArrayList(orderResponse.getPayload().getBids());
        final FilteredList<OrderDTO> filteredBids = obsBids.filtered(p -> obsBids.indexOf(p) < xInteger.get());
        bestBidsListView.setItems(filteredBids);

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                xTextField.setText("0");

                return;
            }

            try {
                Integer newValueInteger = Integer.parseInt(newValue);

                xTextField.setText(newValueInteger.toString());

                // TODO NEED TO CHECK IF THERE IS MORE DATA IN SERVER
                if (obsTrades.size() < newValueInteger) {
                    // TODO GET MORE TRADES FROM SERVER
                }

                filteredTrades.setPredicate(p -> obsTrades.indexOf(p) < newValueInteger);
                filteredAsks.setPredicate(p -> obsAsks.indexOf(p) < newValueInteger);
                filteredBids.setPredicate(p -> obsBids.indexOf(p) < newValueInteger);
            } catch (NumberFormatException e) {
                xTextField.setText(oldValue);
            }
        });
    }
}
