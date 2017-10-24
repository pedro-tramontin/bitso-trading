package br.com.pedront.bitsotrading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@ClientEndpoint
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

    private ObservableList<OrderDTO> obsAsks;

    private ObservableList<OrderDTO> obsBids;

    private FilteredList<OrderDTO> filteredAsks;

    private FilteredList<OrderDTO> filteredBids;

    private Runnable orderQueueConsumer;

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected ... " + session.getId());
        try {
            Subscribe subscribe = new Subscribe("subscribe", "btc_mxn", "diff-orders");

            session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(subscribe));
        } catch (IOException e) {
            // TODO Treat exception
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ObjectMapper jsonMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = jsonMapper.readTree(message);
            if (jsonNode.has("action") && jsonNode.has("response")) {
                String action = jsonNode.get("action").asText();
                String response = jsonNode.get("response").asText();

                if ("subscribe".equals(action) && "ok".equals(response)) {
                    System.out.println("Subcribe to Diff-Order OK!");

                    orderQueueConsumer = () -> {
                        while (true) {
                            try {
                                // Poll instead of take, don't block the thread forever
                                DiffOrder diffOrder = ordersQueue.poll(30, TimeUnit.SECONDS);
                                if (diffOrder != null) {
                                    DiffOrderPayload payload = diffOrder.getPayload().get(0);

                                    if ("cancelled".equals(payload.getStatus())) {
                                        // TODO cancel this order from the list
                                        System.out.println("Cancelled " + payload.getOid());
                                    } else if ("completed".equals(payload.getStatus())) {
                                        // TODO complete this order from the list
                                        System.out.println("Completed " + payload.getOid());
                                    } else if ("open".equals(payload.getStatus())) {
                                        OrderDTO order = new OrderDTO(diffOrder.getBook(), payload.getValue(), payload.getAmount(), payload.getOid());
                                        System.out.println("Processing new message " + order.toString());

                                        // 0 - Buy, 1 - Sell
                                        if ("0".equals(payload.getMakerSide())) {
                                            if (obsBids != null) {
                                                obsBids.add(order);
                                            } else {
                                                System.out.println("Bids is null");
                                            }
                                        } else {
                                            if (obsAsks != null) {
                                                obsAsks.add(order);
                                            } else {
                                                System.out.println("Asks is null");
                                            }
                                        }
                                    } else {
                                        // TODO Can't treat this DiffOrder
                                        System.out.println("Error " + payload.toString());
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    new Thread(orderQueueConsumer).start();
                } else {
                    System.out.println("Error subscribing to diff-order!");

                    // TODO Try connecting at least 3 times
                }
            } else if (jsonNode.has("type")) {
                DiffOrder diffOrder = jsonMapper.readValue(message, DiffOrder.class);

                System.out.println(diffOrder);

                // ignore Keep-Alive packages
                if (!"ka".equals(diffOrder.getType())) {
                    ordersQueue.put(diffOrder);
                }
            } else {
                // TODO Don't know how to treat this message
            }
        } catch (IOException | InterruptedException e) {
            // TODO Treat the exceptions
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println(String.format("Session %s close because of %s", session.getId(), closeReason));
        // TODO Check internet and try to reconnect
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

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
        obsAsks = FXCollections.observableArrayList(orderResponse.getPayload().getAsks());
        filteredAsks = obsAsks.filtered(p -> obsAsks.indexOf(p) < xInteger.get());
        bestAsksListView.setItems(filteredAsks);

        // Created the observable list, filters and sets to the Bids TableView
        obsBids = FXCollections.observableArrayList(orderResponse.getPayload().getBids());
        filteredBids = obsBids.filtered(p -> obsBids.indexOf(p) < xInteger.get());
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

        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(DashboardController.class, new URI("wss://ws.bitso.com"));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
            // TODO Treat the exception
        }
    }
}
