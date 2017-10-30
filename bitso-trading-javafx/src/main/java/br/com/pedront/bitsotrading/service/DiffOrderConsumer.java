package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.service.dto.DiffOrderData;
import br.com.pedront.bitsotrading.wrapper.DiffOrderStatus;
import br.com.pedront.bitsotrading.wrapper.DiffOrderWrapper;
import br.com.pedront.bitsotrading.wrapper.OrderSide;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrder;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiffOrderConsumer extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrderConsumer.class);

    private static final Long SEQUENCE_INIT_VAL = 0L;

    private BlockingQueue<DiffOrderMessage> ordersQueue;

    private DiffOrderData diffOrderData;

    private DiffOrderDataConsumer dataConsumer;

    private Long nextSequence;

    private Boolean running;

    public DiffOrderConsumer(BlockingQueue<DiffOrderMessage> ordersQueue) {
        this.ordersQueue = ordersQueue;

        this.diffOrderData = new DiffOrderData();

        this.running = false;
        this.nextSequence = SEQUENCE_INIT_VAL;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                // Poll instead of take, don't block the thread forever
                DiffOrderMessage diffOrderMessage = ordersQueue
                    .poll(30, TimeUnit.SECONDS);
                if (diffOrderMessage != null) {
                    Long msgSequence = diffOrderMessage.getSequence();
                    if (SEQUENCE_INIT_VAL.equals(nextSequence)) {
                        nextSequence = msgSequence;
                    }

                    if (nextSequence.equals(msgSequence)) {
                        nextSequence++;

                        processDiffOrderPayload(diffOrderMessage.getBook(),
                            diffOrderMessage.getPayload());
                    } else {
                        LOGGER.warn(
                            "Sequence desynchronized, must have lost packet, nextSequence={}, received={}",
                            nextSequence, msgSequence);

                        running = false;

                        nextSequence = SEQUENCE_INIT_VAL;

                        diffOrderData.setReloadLists();
                        applyDiffOrderData();
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("Received interruption while waiting for new message in queue.", e);

                running = false;
            }
        }

        LOGGER.info("Consumer exiting.");
    }

    private void processDiffOrderPayload(final String book, final List<DiffOrder> diffOrderList) {
        for (DiffOrder diffOrder : diffOrderList) {

            DiffOrderWrapper wrapper = DiffOrderWrapper.newWrapper(diffOrder);
            if (wrapper.status() != DiffOrderStatus.UNKNOWN) {

                if (wrapper.getOrderSide() != OrderSide.UNKNOWN) {

                    if (wrapper.status() == DiffOrderStatus.COMPLETED) {
                        diffOrderData.setNewTrades(true);
                    }

                    Order order = wrapper.getOrder(book);

                    if (wrapper.getOrderSide() == OrderSide.BUY) {
                        diffOrderData.removeBid(order);
                    } else {
                        diffOrderData.removeAsk(order);
                    }

                    if (wrapper.status() == DiffOrderStatus.OPEN) {
                        if (wrapper.getOrderSide() == OrderSide.BUY) {
                            diffOrderData.addBid(order);
                        } else {
                            diffOrderData.addAsk(order);
                        }
                    }
                } else {
                    LOGGER
                        .error("Can't process message, order side unknown, message={}", diffOrder);
                }
            } else {
                LOGGER.error("Can't process message, status unknown, message={}", diffOrder);
            }
        }

        applyDiffOrderData();
    }

    public void stopRunning() {
        running = false;
    }

    public void setDataConsumer(DiffOrderDataConsumer dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    private void applyDiffOrderData() {
        Platform.runLater(() -> {
            dataConsumer.apply(diffOrderData.clone());
            diffOrderData.reset();
        });
    }

    /*public void setAddOrders(BiConsumer<List<Order>, List<Order>> addOrders) {
        this.addOrders = addOrders;
    }

    public void setRemoveOrders(BiConsumer<List<Order>, List<Order>> removeOrders) {
        this.removeOrders = removeOrders;
    }

    public void setTradesReload(DiffOrderDataConsumer tradesReload) {
        this.tradesReload = tradesReload;
    }

    public void setOrdersReload(DiffOrderDataConsumer ordersReload) {
        this.ordersReload = ordersReload;
    }

    private void reloadTrades() {
        LOGGER.debug("Reloading trades list");

        reloadTrades = false;

        if (tradesReload != null) {
            Platform.runLater(() -> tradesReload.apply());
        }
    }

    private void reloadOrders() {
        LOGGER.debug("Reloading asks/bids list.");

        if (ordersReload != null) {
            Platform.runLater(() -> ordersReload.apply());
        }
    }

    private void addOrdersList() {
        LOGGER.debug("Adding new itens to asks/bids list.");

        if (addOrders != null) {
            List<Order> addBidsList = new ArrayList<>(toAddBidList);
            List<Order> addAsksList = new ArrayList<>(toAddAskList);

            Platform.runLater(() -> addOrders.accept(addBidsList, addAsksList));
        }

        toAddBidList.clear();
        toAddAskList.clear();
    }

    private void removeOrdersList() {
        LOGGER.debug("Removing itens from asks/bids list.");

        if (removeOrders != null) {
            List<Order> removeBidsList = new ArrayList<>(toRemoveBidList);
            List<Order> removeAsksList = new ArrayList<>(toRemoveAskList);

            Platform.runLater(() -> removeOrders.accept(removeBidsList, removeAsksList));
        }

        toRemoveBidList.clear();
        toRemoveAskList.clear();
    }*/
}
