package br.com.pedront.bitsotrading;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.service.OrderService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@ClientEndpoint
public class BitsoWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private static final String BOOK = "btc_mxn";

    public static final String BITSO_WSS_URL = "wss://ws.bitso.com";

    private Runnable orderQueueConsumer;

    private final BlockingQueue<DiffOrder> ordersQueue = new LinkedBlockingQueue<>();

    private ObservableList<Order> obsAsks;

    private ObservableList<Order> obsBids;

    private DashboardController dashboardController;

    public BitsoWebSocket(final Runnable orderQueueConsumer,
            final ObservableList<Order> obsAsks,
            final ObservableList<Order> obsBids,
            final DashboardController dashboardController) {
        this.orderQueueConsumer = orderQueueConsumer;
        this.obsAsks = FXCollections.synchronizedObservableList(obsAsks);
        this.obsBids = FXCollections.synchronizedObservableList(obsBids);
        this.dashboardController = dashboardController;
    }

    public void start() {
        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(this, new URI(BITSO_WSS_URL));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            LOGGER.error("Unexpected error with BitsoWebSocket, stack trace", e);
        }

    }

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

            System.out.println(jsonNode.toString());

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
                                if (diffOrder != null && diffOrder.getPayload() != null) {

                                    final MainViewUpdate mainViewUpdate = new MainViewUpdate();

                                    final List<DiffOrderPayload> diffOrderPayload = diffOrder.getPayload();
                                    for (DiffOrderPayload payload : diffOrderPayload) {
                                        int makerSide = payload.getMakerSide();

                                        ObservableList<Order> orderList = getOrderList(makerSide);

                                        Optional<Order> foundOrder = null;

                                        String orderId = payload.getOid();
                                        if (orderId != null) {
                                            while (foundOrder == null) {
                                                try {
                                                    foundOrder = orderList
                                                            .stream()
                                                            .filter(o -> orderId.equals(o.getOid()))
                                                            .findFirst();
                                                } catch (ConcurrentModificationException e) {
                                                    System.out.println(
                                                            "The list is beaing modified! Trying again...");
                                                    Thread.sleep(100);
                                                }
                                            }
                                        }

                                        if (orderList != null) {
                                            if ("cancelled".equals(payload.getStatus())
                                                    || "completed".equals(payload.getStatus())) {

                                                if (foundOrder.isPresent()) {
                                                    final Order removeOrder = foundOrder.get();

                                                    mainViewUpdate.addRunnable(() -> dashboardController
                                                            .removeOrder(makerSide, removeOrder));

                                                } else {

                                                    // TODO Try to understand what happened
                                                    System.out.println("No order found");
                                                }

                                                if ("completed".equals(payload.getStatus())) {
                                                    mainViewUpdate.setReloadTrades(true);
                                                }
                                            } else if ("open".equals(payload.getStatus())) {

                                                // The order must have changed the amount
                                                if (foundOrder.isPresent()) {
                                                    final Order removeOrder = foundOrder.get();

                                                    mainViewUpdate.addRunnable(() -> dashboardController
                                                            .removeOrder(makerSide, removeOrder));
                                                }

                                                Order order = new Order(diffOrder.getBook(), payload.getRate(),
                                                        payload.getAmount(), payload.getOid());
                                                System.out.println(
                                                        "Processing new " + payload.getMakerSide() + " message " + order
                                                                .toString());

                                                mainViewUpdate.addRunnable(
                                                        () -> dashboardController.addOrder(makerSide, order));
                                            } else {
                                                // TODO Can't treat this DiffOrder
                                                System.out.println("Error " + payload.toString());
                                            }
                                        }
                                    }

                                    Platform.runLater(() -> {
                                        for (Runnable r : mainViewUpdate.getUpdatesRunnables()) {
                                            r.run();
                                        }

                                        if (mainViewUpdate.getReloadTrades()) {
                                            System.out.println("Reloading trades!");
                                            dashboardController.loadTrades();
                                        }
                                    });
                                }
                            } catch (InterruptedException e) {
                                // TODO Treat this interruption
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

                // ignore Keep-Alive packages
                if (!"ka".equals(diffOrder.getType())) {
                    ordersQueue.put(diffOrder);
                }
            } else {
                // TODO Don't know how to treat this message
                System.out.println("Don't know how to treat this message: " + jsonNode.toString());
            }
        } catch (IOException | InterruptedException e) {
            // TODO Treat the exceptions
            e.printStackTrace();
        }
    }

    private ObservableList<Order> getOrderList(final Integer makerSide) {
        if (makerSide == 0) {
            return obsBids;
        } else if (makerSide == 1) {
            return obsAsks;
        }

        System.out.println(String.format("Maker size has invalid value: %d", makerSide));
        return null;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println(String.format("Session %s close because of %s", session.getId(), closeReason));
        // TODO Check internet and try to reconnect
    }

    private class MainViewUpdate {

        private List<Runnable> updatesRunnables;

        private Boolean reloadTrades;

        public MainViewUpdate() {
            this.updatesRunnables = new ArrayList<>();
            this.reloadTrades = false;
        }

        public void addRunnable(Runnable runnable) {
            getUpdatesRunnables().add(runnable);
        }

        public List<Runnable> getUpdatesRunnables() {
            return updatesRunnables;
        }

        public void setUpdatesRunnables(final List<Runnable> updatesRunnables) {
            this.updatesRunnables = updatesRunnables;
        }

        public Boolean getReloadTrades() {
            return reloadTrades;
        }

        public void setReloadTrades(final Boolean reloadTrades) {
            this.reloadTrades = reloadTrades;
        }
    }
}
