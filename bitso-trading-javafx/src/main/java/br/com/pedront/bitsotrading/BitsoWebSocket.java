package br.com.pedront.bitsotrading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@ClientEndpoint
public class BitsoWebSocket {

    private Runnable orderQueueConsumer;

    private final BlockingQueue<DiffOrder> ordersQueue = new LinkedBlockingQueue<>();

    private ObservableList<OrderDTO> obsAsks;

    private ObservableList<OrderDTO> obsBids;

    private DashboardController dashboardController;

    public BitsoWebSocket(final Runnable orderQueueConsumer,
            final ObservableList<OrderDTO> obsAsks,
            final ObservableList<OrderDTO> obsBids,
            final DashboardController dashboardController) {
        this.orderQueueConsumer = orderQueueConsumer;
        this.obsAsks = FXCollections.synchronizedObservableList(obsAsks);
        this.obsBids = FXCollections.synchronizedObservableList(obsBids);
        this.dashboardController = dashboardController;
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

                                        ObservableList<OrderDTO> orderList = getOrderList(makerSide);

                                        if (orderList != null) {
                                            if ("cancelled".equals(payload.getStatus())
                                                    || "completed".equals(payload.getStatus())) {
                                                String orderId = payload.getOid();

                                                OrderDTO foundOrder = null;
                                                while (foundOrder == null) {
                                                    try {
                                                        foundOrder = orderList
                                                                .stream()
                                                                .filter(o -> orderId.equals(o.getOid()))
                                                                .findFirst()
                                                                .orElse(OrderDTO.NULL_ORDER_DTO);
                                                    } catch (ConcurrentModificationException e) {
                                                        System.out.println(
                                                                "The list is beaing modified! Trying again...");
                                                        Thread.sleep(100);
                                                    }
                                                }

                                                if (foundOrder != OrderDTO.NULL_ORDER_DTO) {
                                                    final OrderDTO removeOrderDTO = foundOrder;

                                                    // Platform.runLater(() -> orderList.remove(removeOrderDTO));
                                                    mainViewUpdate.addRunnable(() -> orderList.remove(removeOrderDTO));

                                                    if ("completed".equals(payload.getStatus())) {
                                                        System.out.println("Reloading trades!");
                                                        // Platform.(() -> dashboardController.reloadTrades());
                                                        mainViewUpdate.setReloadTrades(true);
                                                    }
                                                } else {

                                                    // TODO Try to understand what happened
                                                    System.out.println("No order found");
                                                }
                                            } else if ("open".equals(payload.getStatus())) {
                                                OrderDTO order = new OrderDTO(diffOrder.getBook(), payload.getRate(),
                                                        payload.getAmount(), payload.getOid());
                                                System.out.println(
                                                        "Processing new " + payload.getMakerSide() + " message " + order
                                                                .toString());

                                                // Platform.(() -> orderList.add(order));
                                                mainViewUpdate.addRunnable(() -> orderList.add(order));
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
                                            dashboardController.reloadTrades();
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

    private ObservableList<OrderDTO> getOrderList(final Integer makerSide) {
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