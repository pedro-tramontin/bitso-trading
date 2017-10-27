package br.com.pedront.bitsotrading;

import br.com.pedront.bitsotrading.model.Trade;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.beans.property.SimpleBooleanProperty;
import javax.annotation.PostConstruct;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.service.TradeService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

@FXMLController
public class DashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    private static final String book = "btc_mxn";

    private static final Integer X_INITIAL = 5;

    private static final Integer M_INITIAL = 2;

    private static final Integer N_INITIAL = 2;

    public static final int TRADES_FETCH_DEFAULT = 50;

    @FXML
    public TableView<Order> bestBidsTableView;

    @FXML
    public TableView<Order> bestAsksTableView;

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

    @FXML
    public Label simulatorLabel;

    private final SimpleIntegerProperty xInteger = new SimpleIntegerProperty();

    private final SimpleIntegerProperty mInteger = new SimpleIntegerProperty();

    private final SimpleIntegerProperty nInteger = new SimpleIntegerProperty();

    private final SimpleBooleanProperty enableSimulation = new SimpleBooleanProperty();

    private ObservableList<Order> obsAsks;
    private SortedList<Order> sortedAsks;
    private FilteredList<Order> filteredAsks;

    private ObservableList<Order> obsBids;
    private SortedList<Order> sortedBids;
    private FilteredList<Order> filteredBids;

    private Runnable orderQueueConsumer;

    @Autowired
    private BitsoService bitsoService;

    private TradeService tradeService;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        OrderBook orderBook = bitsoService.fetchOrders(book);

        Bindings
            .bindBidirectional(xTextField.textProperty(), xInteger, new NumberStringConverter());
        Bindings
            .bindBidirectional(mTextField.textProperty(), mInteger, new NumberStringConverter());
        Bindings
            .bindBidirectional(nTextField.textProperty(), nInteger, new NumberStringConverter());

        enableSimulation.bind(Bindings.equal("ENABLED", simulatorLabel.textProperty()));

        xTextField.textProperty().set(X_INITIAL.toString());
        mTextField.textProperty().set(M_INITIAL.toString());
        nTextField.textProperty().set(N_INITIAL.toString());

        this.tradeService = new TradeService(bitsoService, xInteger, mInteger, nInteger,
            enableSimulation, tradesTableView);

        // Created the observable list, filters and sets to the Trades TableView
        reloadTrades();

        // Created the observable list, filters and sets to the Asks TableView
        obsAsks = FXCollections.observableList(orderBook.getAsks());
        sortedAsks = obsAsks.sorted(Comparator.comparing(Order::getPrice));
        filteredAsks = sortedAsks.filtered(p -> sortedAsks.indexOf(p) < xInteger.get());
        bestAsksTableView.setItems(filteredAsks);

        // Created the observable list, filters and sets to the Bids TableView
        obsBids = FXCollections.observableList(orderBook.getBids());
        sortedBids = obsBids.sorted(Comparator.comparing(Order::getPrice).reversed());
        filteredBids = sortedBids.filtered(p -> sortedBids.indexOf(p) < xInteger.get());
        bestBidsTableView.setItems(filteredBids);

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                xTextField.setText("0");

                return;
            }
        });

        simulatorToggleButton.setOnAction((event) -> {
            if (enableSimulation.get()) {
                simulatorToggleButton.setText("ENABLE");

                simulatorLabel.setText("DISABLED");
                simulatorLabel
                    .setStyle(
                        "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; "
                            + "-fx-border-color: #f5c6cb; -fx-border-width: 1px");
            } else {
                simulatorToggleButton.setText("DISABLE");

                simulatorLabel.setText("ENABLED");
                simulatorLabel
                    .setStyle(
                        "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; "
                            + "-fx-border-color: #c3e6cb; -fx-border-width: 1px");
            }
        });

        ClientManager client = ClientManager.createClient();
        try {
            BitsoWebSocket bitsoWebSocket = new BitsoWebSocket(orderQueueConsumer, obsAsks, obsBids,
                this);
            client.connectToServer(bitsoWebSocket, new URI("wss://ws.bitso.com"));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
            // TODO Treat the exception
        }
    }

    public void reloadTrades() {
        if (this.tradeService.getState() != Worker.State.READY) {
            LOGGER.error("The TradeService is not in the READY state, it's in {}.",
                this.tradeService.getState());

            return;
        }

        this.tradeService.start();
    }

    public void removeOrder(int makerSide, Order order) {
        if (makerSide == 0) {
            obsBids.remove(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xInteger.get());
        } else {
            obsAsks.remove(order);
            filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xInteger.get());
        }
    }

    public void addOrder(int makerSide, Order order) {
        if (makerSide == 0) {
            obsBids.add(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xInteger.get());
        } else {
            obsAsks.add(order);
            filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xInteger.get());
        }
    }
}
