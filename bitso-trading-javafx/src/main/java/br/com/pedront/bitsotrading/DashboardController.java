package br.com.pedront.bitsotrading;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.DeploymentException;

import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeDTO;
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
import javafx.collections.transformation.SortedList;
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

    private final BlockingQueue<DiffOrder> ordersQueue = new LinkedBlockingQueue<>();

    private ObservableList<Trade> obsTrades;
    private SortedList<Trade> sortedTrades;
    private FilteredList<Trade> filteredTrades;

    private ObservableList<OrderDTO> obsAsks;
    private SortedList<OrderDTO> sortedAsks;
    private FilteredList<OrderDTO> filteredAsks;

    private ObservableList<OrderDTO> obsBids;
    private SortedList<OrderDTO> sortedBids;
    private FilteredList<OrderDTO> filteredBids;

    private Runnable orderQueueConsumer;

    private Integer lastTid = 0;

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        // final TradeResponseDTO tradeResponse = bitsoApiIntegration.getTrade(book, null);
        final OrderResponseDTO orderResponse = bitsoApiIntegration.getOrder(book, "false");

        Bindings.bindBidirectional(xTextField.textProperty(), xInteger, new NumberStringConverter());
        xTextField.textProperty().set("5");

        // // Created the observable list, filters and sets to the Trades TableView
        // final ObservableList<Trade> obsTrades = FXCollections
        // .observableArrayList(TradeDTOConverter.convert(tradeResponse.getPayload()));
        // final FilteredList<Trade> filteredTrades = obsTrades.filtered(p -> obsTrades.indexOf(p) < xInteger.get());
        // tradesTableView.setItems(filteredTrades);
        reloadTrades();

        // Created the observable list, filters and sets to the Asks TableView
        obsAsks = FXCollections.observableList(orderResponse.getPayload().getAsks());
        sortedAsks = obsAsks.sorted(Comparator.comparing(OrderDTO::getPrice));
        filteredAsks = sortedAsks.filtered(p -> sortedAsks.indexOf(p) < xInteger.get());
        bestAsksListView.setItems(filteredAsks);

        // Created the observable list, filters and sets to the Bids TableView
        obsBids = FXCollections.observableList(orderResponse.getPayload().getBids());
        sortedBids = obsBids.sorted(Comparator.comparing(OrderDTO::getPrice).reversed());
        filteredBids = sortedBids.filtered(p -> sortedBids.indexOf(p) < xInteger.get());
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
                filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < newValueInteger);
                filteredBids.setPredicate(p -> sortedBids.indexOf(p) < newValueInteger);
            } catch (NumberFormatException e) {
                xTextField.setText(oldValue);
            }
        });

        ClientManager client = ClientManager.createClient();
        try {
            BitsoWebSocket bitsoWebSocket = new BitsoWebSocket(orderQueueConsumer, obsAsks, obsBids, this);
            client.connectToServer(bitsoWebSocket, new URI("wss://ws.bitso.com"));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
            // TODO Treat the exception
        }
    }

    public void reloadTrades() {
        TradeResponseDTO tradeResponse;

        if (lastTid == 0) {
            tradeResponse = bitsoApiIntegration.getTrade(book, null, null, 100);
        } else {
            tradeResponse = bitsoApiIntegration.getTrade(book, lastTid, "asc", 100);
        }

        if (obsTrades == null) {
            obsTrades = FXCollections.observableArrayList(TradeDTOConverter.convert(tradeResponse.getPayload()));
        } else {
            obsTrades.addAll(TradeDTOConverter.convert(tradeResponse.getPayload()));
        }

        // Created the observable list, filters and sets to the Trades TableView
        sortedTrades = obsTrades.sorted((t1, t2) -> t2.getTid().compareTo(t1.getTid()));
        filteredTrades = sortedTrades.filtered(p -> sortedTrades.indexOf(p) < xInteger.get());
        tradesTableView.setItems(filteredTrades);

        lastTid = sortedTrades.get(0).getTid();
    }
}
