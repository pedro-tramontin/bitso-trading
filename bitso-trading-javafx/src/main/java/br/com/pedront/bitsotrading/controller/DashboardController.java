package br.com.pedront.bitsotrading.controller;

import static br.com.pedront.bitsotrading.comparator.TradeComparators.compareAscending;
import static br.com.pedront.bitsotrading.comparator.TradeComparators.compareDescending;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.service.BitsoWebSocketService;
import br.com.pedront.bitsotrading.core.utils.ListUtils;
import br.com.pedront.bitsotrading.model.Trade;
import br.com.pedront.bitsotrading.service.ContrarianTradingService;
import br.com.pedront.bitsotrading.service.DiffOrderConsumer;
import br.com.pedront.bitsotrading.service.OrderService;
import br.com.pedront.bitsotrading.service.OrderWebSocketService;
import br.com.pedront.bitsotrading.service.TradeService;
import br.com.pedront.bitsotrading.service.dto.DiffOrderData;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * The Dashboard controller
 */
@FXMLController
public class DashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    private static final Integer X_INITIAL = 5;

    private static final Integer M_INITIAL = 2;

    private static final Integer N_INITIAL = 2;

    private static final Duration TRADES_POLL_PERIOD = Duration.seconds(30);

    private static final Integer TRADES_MAX_RETRIES = 10;

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
    public Button simulateButton;

    @FXML
    public Label simulateLabel;

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

    /* Tries to restart the order book service */
    private Integer ordersTries;

    /* Queue to centralise the Diff-Order messages received from Bitso WebSocket */
    private final PriorityBlockingQueue<DiffOrderMessage> ordersQueue;

    /* Consumer for the messages in the queue */
    private DiffOrderConsumer newOrdersConsumer;

    private BitsoWebSocketService bitsoWebSocketService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderWebSocketService orderWebSocketService;

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
        ordersQueue = new PriorityBlockingQueue<>(X_INITIAL, Comparator.comparing(DiffOrderMessage::getSequence));

        // simple variables
        simulate = false;
        ordersTries = 3;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        configLists();
        configServices();
        configComponents();
        configInitialValues();
        configBinds();
        configListeners();
        configCallbacks();

        startTradeService();
        startOrderBookService();
    }

    private void configLists() {

        // Configure the chain of observables to sort and filter for each TableView
        sortedTrades = trades.sorted(compareDescending());
        filteredTrades = sortedTrades
                .filtered(getFilterPredicate(sortedTrades, xProperty.intValue()));
        tradesTableView.setItems(filteredTrades);

        sortedBids = bids.sorted(Comparator.comparing(Order::getPrice).reversed());
        filteredBids = sortedBids.filtered(getFilterPredicate(sortedBids, xProperty.intValue()));
        bestBidsTableView.setItems(filteredBids);

        sortedAsks = asks.sorted(Comparator.comparing(Order::getPrice));
        filteredAsks = sortedAsks.filtered(getFilterPredicate(sortedAsks, xProperty.intValue()));
        bestAsksTableView.setItems(filteredAsks);
    }

    private void configServices() {

        orderWebSocketService.setOrdersQueue(ordersQueue);
        orderWebSocketService.start();

        // Sets the period for the trades polling
        tradeService.setPeriod(TRADES_POLL_PERIOD);

        // Sets the maximum number of retries after fail attempts
        tradeService.setMaximumFailureCount(TRADES_MAX_RETRIES);
    }

    private void configComponents() {
        bestBidsTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        bestAsksTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        tradesTableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        tradesTableView.setRowFactory(row -> new TableRow<Trade>() {
            @Override
            protected void updateItem(final Trade item, final boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    removeSimulatedClass();
                } else {
                    if (item.isSimulated()) {
                        addSimulatedClass();
                    } else {
                        removeSimulatedClass();
                    }
                }
            }

            private void addSimulatedClass() {
                getStyleClass().add("table-row-simulated");
            }

            private void removeSimulatedClass() {
                getStyleClass().clear();
                getStyleClass().addAll("cell", "indexed-cell", "table-row-cell");
            }
        });
    }

    private void configInitialValues() {
        xTextField.textProperty().set(X_INITIAL.toString());
        mTextField.textProperty().set(M_INITIAL.toString());
        nTextField.textProperty().set(N_INITIAL.toString());
    }

    private void configListeners() {
        xProperty.addListener((observable, oldValue, newValue) -> {
            updateFilteredTableView(filteredTrades, sortedTrades, newValue.intValue());
            updateFilteredTableView(filteredBids, sortedBids, newValue.intValue());
            updateFilteredTableView(filteredAsks, sortedAsks, newValue.intValue());

            if (changedXAndNotEnoughDataInCache(oldValue, newValue)) {
                startTradeService();
            }
        });
        mProperty.addListener(
                (observable, oldValue, newValue) -> simulator.setUpticks(newValue.intValue()));
        nProperty.addListener(
                (observable, oldValue, newValue) -> simulator.setDownTicks(newValue.intValue()));

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                xTextField.setText("0");
            } else {
                xTextField.setText(Integer.toString(Integer.valueOf(newValue)));
            }
        });
    }

    private boolean changedXAndNotEnoughDataInCache(final Number oldValue, final Number newValue) {
        return oldValue.intValue() != newValue.intValue() && trades.size() < newValue.intValue();
    }

    private void configBinds() {

        // Convert the textProperty to intProperty to facilitate
        xProperty.bind(
                Bindings.createIntegerBinding(textPropertyToInteger(xTextField.textProperty()),
                        xTextField.textProperty()));
        mProperty.bind(
                Bindings.createIntegerBinding(textPropertyToInteger(mTextField.textProperty()),
                        mTextField.textProperty()));
        nProperty.bind(
                Bindings.createIntegerBinding(textPropertyToInteger(nTextField.textProperty()),
                        nTextField.textProperty()));

        // Bind the visibility of each progress component to each responsible service
        paneProgressTrades.visibleProperty()
                .bind(tradeService.stateProperty()
                        .isEqualTo(Worker.State.RUNNING));
        paneProgressBids.visibleProperty()
                .bind(orderService.stateProperty()
                        .isEqualTo(Worker.State.RUNNING));
        paneProgressAsks.visibleProperty()
                .bind(orderService.stateProperty()
                        .isEqualTo(Worker.State.RUNNING));

        // Bind X and CacheSize to the TradeService properties
        tradeService.xProperty().bind(xProperty);
        tradeService.cacheSizeProperty().bind(trades.sizeProperty());
    }

    @SuppressWarnings("unchecked")
    private void configCallbacks() {
        simulateButton.setOnAction(event -> {

            // toggle the simulator functionality on/off
            if (simulate) {
                disableToggleButton();
            } else {
                enableToggleButton();
            }

            simulate = !simulate;
        });

        tradeService.setOnSucceeded((WorkerStateEvent event) -> {
            List<Trade> tradeList = (List<Trade>) event.getSource().getValue();
            trades.addAll(tradeList);

            LOGGER.debug("New trades list size {}.", tradeList.size());

            if (simulate && !tradeList.isEmpty()) {
                reSimulateTradingList();
            }

            updateFilteredTableView(filteredTrades, sortedTrades, xProperty.intValue());
            tradesTableView.refresh();
        });

        tradeService.setOnFailed(event -> {
            LOGGER.debug("TradeService failed.", tradeService.getException());
        });

        orderService.setOnSucceeded(event -> {
            OrderBook orderBook = (OrderBook) event.getSource().getValue();

            LOGGER.debug("New order book, bids size {}, asks size {}.", orderBook.getBids().size(),
                    orderBook.getAsks().size());

            bids.clear();
            bids.addAll(orderBook.getBids());
            updateFilteredTableView(filteredBids, sortedBids, xProperty.intValue());

            asks.clear();
            asks.addAll(orderBook.getAsks());
            updateFilteredTableView(filteredAsks, sortedAsks, xProperty.intValue());

            newOrdersConsumer = new DiffOrderConsumer(ordersQueue, orderBook.getSequence());
            newOrdersConsumer.setDataConsumer(this::applyDiffOrderData);
            newOrdersConsumer.start();

            orderService.reset();
        });

        orderService.setOnFailed(event -> {
            LOGGER.info("Something went wrong with service {}, gonna try {} more times.", orderService, ordersTries);

            if (ordersTries > 0) {
                ordersTries--;

                orderService.reset();
                orderService.start();
            }
        });

        orderWebSocketService.setOnSucceeded(event -> {
            LOGGER.info("WebSocket connected.");

            bitsoWebSocketService = (BitsoWebSocketService) event.getSource().getValue();
        });
    }

    /**
     * Remove every simulated trading from the trades list and run the simulation again for the list.
     */
    private void reSimulateTradingList() {
        clearTradesListFromSimulatedData();

        trades.addAll(generateSimulatedData());
        updateFilteredTableView(filteredTrades, sortedTrades, xProperty.intValue());
    }

    private void clearTradesListFromSimulatedData() {
        trades.removeAll(
                trades.stream()
                        .filter(Trade::isSimulated)
                        .collect(Collectors.toList()));
    }

    /**
     * Run the simulation for the trades list.
     */
    private List<Trade> generateSimulatedData() {
        final List<Trade> sortedAscTrades = trades.stream()
                .sorted(compareAscending())
                .collect(Collectors.toList());

        resetSimulatorState(ListUtils.getFirstItem(sortedAscTrades)
                .map(Trade::getPrice).orElse(0.0));

        return sortedAscTrades.stream()
                .map(simulator::simulate)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <T> void updateFilteredTableView(final FilteredList<T> filteredList,
            final SortedList<T> sortedList, final Integer newValue) {
        if (filteredList != null && sortedList != null) {
            filteredList.setPredicate(getFilterPredicate(sortedList, newValue));
        }
    }

    private <T> Predicate<T> getFilterPredicate(final SortedList<T> sortedList,
            final Integer newValue) {

        return p -> sortedList.indexOf(p) < newValue;
    }

    private Callable<Integer> textPropertyToInteger(StringProperty stringProperty) {
        return () -> {
            if (!"".equals(stringProperty.get())) {
                return Integer.valueOf(stringProperty.get());
            }

            return 0;
        };
    }

    private void enableToggleButton() {
        toggleSimulatorButton(BUTTON_DISABLE_TEXT, LABEL_ENABLED_TEXT, "simulator-on");

        resetSimulatorState();
        reSimulateTradingList();
    }

    /**
     * Resets the simulator state using the price value parameter
     */
    private void resetSimulatorState(Double initPrice) {
        simulator.init(mProperty.get(), nProperty.get(), initPrice);
    }

    /**
     * Resets the simulator state using the sortedTrades list
     */
    private void resetSimulatorState() {
        final Double initTradeValue = ListUtils.getLastItem(sortedTrades)
                .map(Trade::getPrice).orElse(0.0);

        simulator.init(mProperty.get(), nProperty.get(), initTradeValue);
    }

    private void disableToggleButton() {
        toggleSimulatorButton(BUTTON_ENABLE_TEXT, LABEL_DISABLED_TEXT, "simulator-off");

        clearTradesListFromSimulatedData();
    }

    private void toggleSimulatorButton(final String buttonText, final String labelText,
            final String styleClass) {
        simulateButton.setText(buttonText);

        simulateLabel.setText(labelText);

        simulateLabel.getStyleClass().clear();
        simulateLabel.getStyleClass().add(styleClass);
    }

    private void startTradeService() {
        switch (tradeService.getState()) {
        case READY:
            LOGGER.debug("Loading new trades.");

            tradeService.start();
            break;

        case SCHEDULED:
        case RUNNING:
            LOGGER.debug("Forcing reload.");

            tradeService.cancel();
            tradeService.restart();
            break;

        case FAILED:
        case CANCELLED:
            LOGGER.debug("Restarting TradeService.");

            tradeService.restart();
            break;

        case SUCCEEDED:
            LOGGER.debug("Finishing executing, will restart soon.");
            break;
        default:
            LOGGER.error("The TradeService is not in the READY or SCHEDULED state, it's in {}.",
                    tradeService.getState());
        }
    }

    private void startOrderBookService() {
        if (orderService.getState() == Worker.State.READY) {
            LOGGER.debug("Loading bids/asks.");

            bids.clear();
            asks.clear();

            orderService.start();
        } else {
            LOGGER.error("The OrderService is not in the READY state, it's in {}.",
                    orderService.getState());
        }
    }

    private void applyDiffOrderData(DiffOrderData diffOrderData) {
        LOGGER.debug("Appying diff-orders");

        diffOrderData.getRemoveBidList().forEach(this::removeBidIfPresent);
        bids.addAll(diffOrderData.getAddBidList());
        updateFilteredTableView(filteredBids, sortedBids, xProperty.intValue());

        diffOrderData.getRemoveAskList().forEach(this::removeAskIfPresent);
        asks.addAll(diffOrderData.getAddAskList());
        updateFilteredTableView(filteredAsks, sortedAsks, xProperty.intValue());

        if (diffOrderData.reloadTrades()) {
            startTradeService();
        }

        if (diffOrderData.reloadOrder()) {
            startOrderBookService();
        }
    }

    private boolean removeBidIfPresent(Order order) {
        return bids.removeIf(p -> p.getOid().equals(order.getOid()));
    }

    private boolean removeAskIfPresent(Order order) {
        return asks.removeIf(p -> p.getOid().equals(order.getOid()));
    }

    /**
     * @noinspection unused
     */
    @PreDestroy
    public void close() {
        newOrdersConsumer.stopRunning();
        bitsoWebSocketService.stop();
    }
}
