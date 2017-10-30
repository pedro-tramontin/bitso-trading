package br.com.pedront.bitsotrading.controller;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.comparator.TradeComparator;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import br.com.pedront.bitsotrading.service.ContrarianTradingService;
import br.com.pedront.bitsotrading.service.DiffOrderConsumer;
import br.com.pedront.bitsotrading.service.OrderService;
import br.com.pedront.bitsotrading.service.TradeService;
import br.com.pedront.bitsotrading.service.dto.DiffOrderData;
import br.com.pedront.bitsotrading.websocket.DiffOrderWebSocketConsumer;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * The Dashboard controller
 */
@FXMLController
public class DashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    private static final Integer X_INITIAL = 5;

    private static final Integer M_INITIAL = 2;

    private static final Integer N_INITIAL = 2;

    private static final String LABEL_ENABLED_TEXT = "ENABLED";

    private static final String LABEL_DISABLED_TEXT = "DISABLED";

    private static final String BUTTON_DISABLE_TEXT = "DISABLE";

    private static final String BUTTON_ENABLE_TEXT = "ENABLE";

    /* Fields */
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

    @FXML
    public HBox paneProgressBids;

    @FXML
    public HBox paneProgressAsks;

    @FXML
    public HBox paneProgressTrades;

    /* Properties */
    private final SimpleIntegerProperty xProperty;
    private final SimpleIntegerProperty mProperty;
    private final SimpleIntegerProperty nProperty;

    private final SimpleListProperty<Trade> trades;
    private final SimpleListProperty<Order> bids;
    private final SimpleListProperty<Order> asks;

    /* Recalculated lists */
    private SortedList<Trade> sortedTrades;
    private FilteredList<Trade> filteredTrades;

    private SortedList<Order> sortedBids;
    private FilteredList<Order> filteredBids;

    private SortedList<Order> sortedAsks;
    private FilteredList<Order> filteredAsks;

    /* Flag indicating if the simulation is running */
    private Boolean simulate;

    /* Last Trade ID used in TradeService */
    private Integer lastTid;

    /* Tries to restart the Order/Trade services */
    private Integer tradesTries;
    private Integer ordersTries;

    /* Queue to centralise the Diff-Order messages received from Bitso WebSocket */
    private final BlockingQueue<DiffOrderMessage> ordersQueue;

    /* Consumer for the messages in the queue */
    private DiffOrderConsumer newOrdersConsumer;

    @Autowired
    private BitsoService bitsoService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ContrarianTradingService simulator;

    public DashboardController() {

        // simple properties
        xProperty = new SimpleIntegerProperty();
        mProperty = new SimpleIntegerProperty();
        nProperty = new SimpleIntegerProperty();

        // list properties
        trades = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        bids = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        asks = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

        // objects
        ordersQueue = new LinkedBlockingQueue<>();

        // simple variables
        lastTid = 0;
        simulate = false;
        tradesTries = 3;
        ordersTries = 3;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        configServices();
        configComponents();
        configInitialValues();
        configListeners();
        configBinds();
        configCallbacks();

        loadTrades();
        loadOrderBook();
    }

    private void configServices() {
        bitsoService.subscribeToDiffOrders(new DiffOrderWebSocketConsumer(ordersQueue));
    }

    private void configComponents() {
        tradesTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        bestBidsTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        bestAsksTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    }

    private void configInitialValues() {
        xTextField.textProperty().set(X_INITIAL.toString());
        mTextField.textProperty().set(M_INITIAL.toString());
        nTextField.textProperty().set(N_INITIAL.toString());
    }

    private void configListeners() {
        xProperty.addListener((observable, oldValue, newValue) -> {
            updateFilteredTableView(filteredTrades, sortedTrades, newValue);
            updateFilteredTableView(filteredBids, sortedBids, newValue);
            updateFilteredTableView(filteredAsks, sortedAsks, newValue);
        });
        mProperty.addListener((observable, oldValue, newValue) -> simulator.setUpticks(newValue.intValue()));
        nProperty.addListener((observable, oldValue, newValue) -> simulator.setDownTicks(newValue.intValue()));

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                xTextField.setText("0");
            } else {
                xTextField.setText(Integer.toString(Integer.valueOf(newValue)));
            }
        });
    }

    private void configBinds() {
        xProperty.bind(
                Bindings.createIntegerBinding(textPropertyToInteger(xTextField.textProperty()),
                        xTextField.textProperty()));
        mProperty.bind(
                Bindings.createIntegerBinding(textPropertyToInteger(mTextField.textProperty()),
                        mTextField.textProperty()));
        nProperty.bind(
                Bindings.createIntegerBinding(textPropertyToInteger(nTextField.textProperty()),
                        nTextField.textProperty()));

        paneProgressTrades.visibleProperty()
                .bind(tradeService.stateProperty()
                        .isEqualTo(Worker.State.RUNNING));
        paneProgressBids.visibleProperty()
                .bind(orderService.stateProperty()
                        .isEqualTo(Worker.State.RUNNING));
        paneProgressAsks.visibleProperty()
                .bind(orderService.stateProperty()
                        .isEqualTo(Worker.State.RUNNING));
    }

    @SuppressWarnings("unchecked")
    private void configCallbacks() {
        simulatorToggleButton.setOnAction(event -> {
            if (simulate) {
                disableToggleButton();
            } else {
                enableToggleButton();
            }

            simulate = !simulate;
        });

        tradeService.setOnSucceeded(event -> {
            List<Trade> tradeList = (List<Trade>) event.getSource().getValue();

            if (simulate) {
                final List<Trade> simTrades = tradeList.stream()
                        .map(simulator::simulate)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                trades.addAll(simTrades);
            }

            trades.addAll(tradeList);
            sortedTrades = trades.sorted(new TradeComparator());
            filteredTrades = sortedTrades.filtered(p -> sortedTrades.indexOf(p) < xProperty.get());
            tradesTableView.setItems(filteredTrades);

            if (sortedTrades.size() > 0) {
                lastTid = sortedTrades.get(0).getTid();
            }

            tradeService.reset();
            tradesTries = 3;
        });

        tradeService.setOnFailed(event -> tradesTries = treatFailService(tradeService, tradesTries));

        orderService.setOnSucceeded(event -> {
            OrderBook orderBook = (OrderBook) event.getSource().getValue();

            bids.clear();
            bids.addAll(orderBook.getBids());
            sortedBids = bids.sorted(Comparator.comparing(Order::getPrice).reversed());
            filteredBids = sortedBids.filtered(p -> sortedBids.indexOf(p) < xProperty.get());
            bestBidsTableView.setItems(filteredBids);

            asks.clear();
            asks.addAll(orderBook.getAsks());
            sortedAsks = asks.sorted(Comparator.comparing(Order::getPrice));
            filteredAsks = sortedAsks.filtered(p -> sortedAsks.indexOf(p) < xProperty.get());
            bestAsksTableView.setItems(filteredAsks);

            newOrdersConsumer = new DiffOrderConsumer(ordersQueue);
            newOrdersConsumer.setDataConsumer(this::applyDiffOrderData);
            newOrdersConsumer.start();

            orderService.reset();
        });

        orderService.setOnFailed(event -> ordersTries = treatFailService(orderService, ordersTries));
    }

    private <T> void updateFilteredTableView(final FilteredList<T> filteredList, final SortedList<T> sortedList,
            final Number newValue) {
        if (filteredList != null && sortedList != null) {
            filteredList.setPredicate(p -> sortedList.indexOf(p) < newValue.intValue());
        }
    }

    private Callable<Integer> textPropertyToInteger(StringProperty stringProperty) {
        return () -> {
            if (!"".equals(stringProperty.get())) {
                return Integer.valueOf(stringProperty.get());
            }

            return 0;
        };
    }

    private Integer treatFailService(final Service service, final Integer tries) {
        Integer triesNewValue = tries;

        LOGGER.info("Something went wrong with service {}, gonna try {} more times.", service, tries);

        if (tries > 0) {
            triesNewValue = tries - 1;

            service.reset();
            service.start();
        }

        return triesNewValue;
    }

    private void enableToggleButton() {
        toggleSimulatorButton(BUTTON_DISABLE_TEXT, LABEL_ENABLED_TEXT,
                "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; "
                        + "-fx-border-color: #c3e6cb; -fx-border-width: 1px");

        Double lastTradePrice = 0.0;
        if (trades.size() > 0) {
            lastTradePrice = trades.get(0).getPrice();
        }

        simulator.init(mProperty.get(), nProperty.get(), lastTradePrice);
    }

    private void disableToggleButton() {
        toggleSimulatorButton(BUTTON_ENABLE_TEXT, LABEL_DISABLED_TEXT,
                "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; "
                        + "-fx-border-color: #f5c6cb; -fx-border-width: 1px");
    }

    private void toggleSimulatorButton(final String buttonText, final String labelText,
            final String labelStyle) {
        simulatorToggleButton.setText(buttonText);

        simulatorLabel.setText(labelText);
        simulatorLabel.setStyle(labelStyle);
    }

    private void loadTrades() {
        if (tradeService.getState() != Worker.State.READY) {
            LOGGER.error("The TradeService is not in the READY state, it's in {}.",
                    tradeService.getState());
        } else {
            tradeService.setLastTid(lastTid);
            tradeService.start();
        }
    }

    private void loadOrderBook() {
        if (orderService.getState() != Worker.State.READY) {
            LOGGER.error("The OrderService is not in the READY state, it's in {}.",
                    orderService.getState());
        } else {
            bids.clear();
            asks.clear();

            orderService.start();
        }
    }

    private void applyDiffOrderData(DiffOrderData diffOrderData) {
        diffOrderData.getRemoveBidList().forEach(this::removeBidIfPresent);
        bids.addAll(diffOrderData.getAddBidList());

        sortedBids = bids.sorted(Comparator.comparing(Order::getPrice).reversed());
        filteredBids = sortedBids.filtered(p -> sortedBids.indexOf(p) < xProperty.get());
        bestBidsTableView.setItems(filteredBids);

        diffOrderData.getRemoveAskList().forEach(this::removeAskIfPresent);
        asks.addAll(diffOrderData.getAddAskList());

        sortedAsks = asks.sorted(Comparator.comparing(Order::getPrice));
        filteredAsks = sortedAsks.filtered(p -> sortedAsks.indexOf(p) < xProperty.get());
        bestAsksTableView.setItems(filteredAsks);

        if (diffOrderData.hasNewTrades()) {
            loadTrades();
        }

        if (diffOrderData.reloadOrder()) {
            loadOrderBook();
        }
    }

    private boolean removeBidIfPresent(Order order) {
        return bids.removeIf(p -> p.getOid().equals(order.getOid()));
    }

    private boolean removeAskIfPresent(Order order) {
        return asks.removeIf(p -> p.getOid().equals(order.getOid()));
    }

    /** @noinspection unused */
    @PreDestroy
    public void close() {
        newOrdersConsumer.stopRunning();
    }
}
