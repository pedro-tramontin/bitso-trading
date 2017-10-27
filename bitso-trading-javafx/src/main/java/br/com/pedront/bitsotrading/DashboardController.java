package br.com.pedront.bitsotrading;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import br.com.pedront.bitsotrading.service.OrderService;
import br.com.pedront.bitsotrading.service.TradeService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

@FXMLController
public class DashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    private static final Integer X_INITIAL = 5;

    private static final Integer M_INITIAL = 2;

    private static final Integer N_INITIAL = 2;

    public static final String LABEL_ENABLED_TEXT = "ENABLED";

    public static final String LABEL_DISABLED_TEXT = "DISABLED";

    public static final String BUTTON_DISABLE_TEXT = "DISABLE";

    public static final String BUTTON_ENABLE_TEXT = "ENABLE";

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

    private final SimpleIntegerProperty xInteger = new SimpleIntegerProperty();

    private final SimpleIntegerProperty mInteger = new SimpleIntegerProperty();

    private final SimpleIntegerProperty nInteger = new SimpleIntegerProperty();

    private final SimpleBooleanProperty enableSimulation = new SimpleBooleanProperty();

    // private ObservableList<Order> obsAsks;
    // private SortedList<Order> sortedAsks;
    // private FilteredList<Order> filteredAsks;
    //
    // private ObservableList<Order> obsBids;
    // private SortedList<Order> sortedBids;
    // private FilteredList<Order> filteredBids;
    //
    // private Runnable orderQueueConsumer;

    @Autowired
    private BitsoService bitsoService;

    private TradeService tradeService;

    private OrderService orderService;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        xTextField.textProperty().set(X_INITIAL.toString());
        mTextField.textProperty().set(M_INITIAL.toString());
        nTextField.textProperty().set(N_INITIAL.toString());

        xInteger.bind(Bindings.createIntegerBinding(() -> Integer.valueOf(xTextField.textProperty().get()),
                xTextField.textProperty()));
        mInteger.bind(Bindings.createIntegerBinding(() -> Integer.valueOf(mTextField.textProperty().get()),
                mTextField.textProperty()));
        nInteger.bind(Bindings.createIntegerBinding(() -> Integer.valueOf(nTextField.textProperty().get()),
                nTextField.textProperty()));
        enableSimulation.bind(Bindings.equal(LABEL_ENABLED_TEXT, simulatorLabel.textProperty()));

        xTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ("".equals(newValue)) {
                xTextField.setText("0");
            }
        });

        simulatorToggleButton.setOnAction((event) -> {
            if (enableSimulation.get()) {
                disableToggleButton();
            } else {
                enableToggleButton();
            }
        });

        this.tradeService = new TradeService(bitsoService, xInteger, mInteger, nInteger,
                enableSimulation, tradesTableView);

        this.orderService = new OrderService(bitsoService, xInteger, bestBidsTableView, bestAsksTableView);

        paneProgressTrades.visibleProperty().bind(tradeService.stateProperty().isEqualTo(Worker.State.RUNNING));
        paneProgressBids.visibleProperty().bind(orderService.stateProperty().isEqualTo(Worker.State.RUNNING));
        paneProgressAsks.visibleProperty().bind(orderService.stateProperty().isEqualTo(Worker.State.RUNNING));

        loadTrades();
        loadOrderBook();

        /*
         * ClientManager client = ClientManager.createClient(); try { BitsoWebSocket bitsoWebSocket = new
         * BitsoWebSocket(orderQueueConsumer, obsAsks, obsBids, this); client.connectToServer(bitsoWebSocket, new
         * URI("wss://ws.bitso.com")); } catch (DeploymentException | URISyntaxException | IOException e) { throw new
         * RuntimeException(e); // TODO Treat the exception }
         */
    }

    private void enableToggleButton() {
        toggleSimulatorButton(BUTTON_DISABLE_TEXT, LABEL_ENABLED_TEXT,
                "-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; "
                        + "-fx-border-color: #c3e6cb; -fx-border-width: 1px");
    }

    private void disableToggleButton() {
        toggleSimulatorButton(BUTTON_ENABLE_TEXT, LABEL_DISABLED_TEXT,
                "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; "
                        + "-fx-border-color: #f5c6cb; -fx-border-width: 1px");
    }

    private void toggleSimulatorButton(final String buttonText, final String labelText, final String labelStyle) {
        simulatorToggleButton.setText(buttonText);

        simulatorLabel.setText(labelText);
        simulatorLabel.setStyle(labelStyle);
    }

    void loadTrades() {
        if (tradeService.getState() != Worker.State.READY) {
            LOGGER.error("The TradeService is not in the READY state, it's in {}.",
                    tradeService.getState());
        } else {
            tradeService.start();
        }
    }

    void loadOrderBook() {
        if (orderService.getState() != Worker.State.READY) {
            LOGGER.error("The OrderService is not in the READY state, it's in {}.",
                    orderService.getState());
        } else {
            orderService.start();
        }
    }

    // public void removeOrder(int makerSide, Order order) {
    // if (makerSide == 0) {
    // obsBids.remove(order);
    // filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xInteger.get());
    // } else {
    // obsAsks.remove(order);
    // filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xInteger.get());
    // }
    // }
    //
    // public void addOrder(int makerSide, Order order) {
    // if (makerSide == 0) {
    // obsBids.add(order);
    // filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xInteger.get());
    // } else {
    // obsAsks.add(order);
    // filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xInteger.get());
    // }
    // }
}
