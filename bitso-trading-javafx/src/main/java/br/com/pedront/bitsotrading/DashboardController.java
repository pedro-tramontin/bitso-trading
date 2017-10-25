package br.com.pedront.bitsotrading;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import javax.websocket.DeploymentException;

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
import javafx.scene.control.Label;
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

    @FXML
    public Label simulatorLabel;

    private final SimpleIntegerProperty xInteger = new SimpleIntegerProperty();

    private final SimpleIntegerProperty mInteger = new SimpleIntegerProperty();

    private final SimpleIntegerProperty nInteger = new SimpleIntegerProperty();

    private Boolean enableSimulation = false;

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

    private Integer simulatorCount = 0;
    private Double lastTradePrice = 0.0;

    private List<Trade> simulatorTrades = new ArrayList<>();

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        // final TradeResponseDTO tradeResponse = bitsoApiIntegration.getTrade(book, null);
        final OrderResponseDTO orderResponse = bitsoApiIntegration.getOrder(book, "false");

        Bindings.bindBidirectional(xTextField.textProperty(), xInteger, new NumberStringConverter());
        xTextField.textProperty().set("5");

        Bindings.bindBidirectional(mTextField.textProperty(), mInteger, new NumberStringConverter());
        mTextField.textProperty().set("3");

        Bindings.bindBidirectional(nTextField.textProperty(), nInteger, new NumberStringConverter());
        nTextField.textProperty().set("2");

        // // Created the observable list, filters and sets to the Trades TableView
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

        simulatorToggleButton.setOnAction((event) -> {
            if (enableSimulation) {
                simulatorToggleButton.setText("ENABLE");

                simulatorLabel.setText("DISABLED");
                simulatorLabel
                        .setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; "
                                + "-fx-border-color: #f5c6cb; -fx-border-width: 1px");

                enableSimulation = false;
            } else {
                simulatorCount = 0;
                lastTradePrice = sortedTrades.get(0).getPrice();

                simulatorToggleButton.setText("DISABLE");

                simulatorLabel.setText("ENABLED");
                simulatorLabel
                        .setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; "
                                + "-fx-border-color: #c3e6cb; -fx-border-width: 1px");

                enableSimulation = true;
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
            tradeResponse = bitsoApiIntegration.getTrade(book, null, "desc", 100);
        } else {
            tradeResponse = bitsoApiIntegration.getTrade(book, lastTid, "asc", 100);
        }

        if (obsTrades == null) {
            obsTrades = FXCollections.observableArrayList(TradeDTOConverter.convert(tradeResponse.getPayload()));
        } else {
            final List<Trade> newTrades = TradeDTOConverter.convert(tradeResponse.getPayload());

            if (enableSimulation) {
                final List<Trade> simTrades = newTrades.stream().map(this::simulate).filter(Objects::nonNull)
                        .collect(Collectors.toList());

                newTrades.addAll(simTrades);
            }

            obsTrades.addAll(newTrades);
        }

        // Created the observable list, filters and sets to the Trades TableView
        sortedTrades = obsTrades.sorted(new TradeComparator());
        filteredTrades = sortedTrades.filtered(p -> sortedTrades.indexOf(p) < xInteger.get());
        tradesTableView.setItems(filteredTrades);

        lastTid = sortedTrades.get(0).getTid();
    }

    public void removeOrder(int makerSide, OrderDTO order) {
        if (makerSide == 0) {
            obsBids.remove(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xInteger.get());
        } else {
            obsAsks.remove(order);
            filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xInteger.get());
        }
    }

    public void addOrder(int makerSide, OrderDTO order) {
        if (makerSide == 0) {
            obsBids.add(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xInteger.get());
        } else {
            obsAsks.add(order);
            filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xInteger.get());
        }
    }

    public Trade simulate(Trade newTrade) {
        Trade simulated = null;

        if (newTrade.getPrice() > lastTradePrice) {
            simulatorCount++;
        } else if (newTrade.getPrice() < lastTradePrice) {
            simulatorCount--;
        }

        if (simulatorCount == mInteger.get()) {
            simulated = new Trade(newTrade.getCreatedAt(), "sell", 1.0, newTrade.getPrice(), newTrade.getTid(), true);
            simulatorCount = 0;
        } else if (simulatorCount == -nInteger.get()) {
            simulated = new Trade(newTrade.getCreatedAt(), "buy", 1.0, newTrade.getPrice(), newTrade.getTid(), true);
            simulatorCount = 0;
        }

        lastTradePrice = newTrade.getPrice();

        return simulated;
    }

    private class TradeComparator implements Comparator<Trade> {

        @Override
        public int compare(final Trade trade1, final Trade trade2) {

            int resultCompare = trade2.getCreatedAt().compareTo(trade1.getCreatedAt());
            if (resultCompare == 0) {
                resultCompare = trade2.getTid().compareTo(trade1.getTid());

                // Only when the trade is simulated and needs to be above the other trade
                if (resultCompare == 0) {
                    if (trade2.isSimulated()) {
                        resultCompare = 1;
                    } else {
                        resultCompare = -1;
                    }
                }
            }

            return resultCompare;
        }
    }
}
