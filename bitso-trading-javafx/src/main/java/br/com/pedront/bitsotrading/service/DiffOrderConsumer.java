package br.com.pedront.bitsotrading.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.service.dto.DiffOrderData;
import br.com.pedront.bitsotrading.wrapper.DiffOrderWrapper;
import br.com.pedront.bitsotrading.wrapper.OrderSide;
import br.com.pedront.bitsotrading.wrapper.OrderStatus;
import javafx.application.Platform;

/**
 * Consumer for diff-order messages.
 */
public class DiffOrderConsumer extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrderConsumer.class);

    /**
     * Queue that contains the diff-orders to be processed
     */
    private BlockingQueue<DiffOrderMessage> ordersQueue;

    /**
     * Data produced by this consumer
     */
    private DiffOrderData diffOrderData;

    /**
     * Consumer to process the data produced in the main thread
     */
    private Consumer<DiffOrderData> dataConsumer;

    /**
     * Next sequence expected
     */
    private Long nextSequence;

    /**
     * Indicates if the thread is running
     */
    private Boolean running;

    public DiffOrderConsumer(BlockingQueue<DiffOrderMessage> ordersQueue, Long nextSequence) {
        this.ordersQueue = ordersQueue;

        this.diffOrderData = new DiffOrderData();

        this.running = false;
        this.nextSequence = nextSequence;
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

                    if (!isOld(msgSequence)) {
                        if (isSequenceOK(msgSequence)) {
                            nextSequence++;

                            processDiffOrderMessage(diffOrderMessage);
                        } else {
                            reloadListsAndStopThread(msgSequence);
                        }
                    } else {
                        LOGGER.debug("Ignoring message with sequence {}.", msgSequence);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("Received interruption while waiting for new message in queue.", e);

                Thread.currentThread().interrupt();
            }
        }

        LOGGER.info("Consumer exiting.");
    }

    private boolean isOld(final Long msgSequence) {
        return msgSequence < nextSequence;
    }

    private boolean isSequenceOK(final Long msgSequence) {
        return nextSequence.equals(msgSequence);
    }

    private void reloadListsAndStopThread(final Long msgSequence) {
        LOGGER.warn("Sequence desynchronized, must have lost packet, nextSequence={}, received={}",
            nextSequence, msgSequence);

        running = false;

        diffOrderData.setReloadLists();
        applyDiffOrderData();
    }

    /**
     * Process the diff-order message, adding/removing orders from the bid and ask lists and also
     * settings the trades list to be reloaded with the REST API.
     */
    private void processDiffOrderMessage(final DiffOrderMessage message) {
        message.getPayload().stream()
            .map(DiffOrderWrapper::newWrapper)
            .filter(DiffOrderWrapper::isValid)
            .forEach(wrapper -> {
                /*if (wrapper.status() == OrderStatus.COMPLETED) {
                    diffOrderData.setReloadTrades(true);
                }*/

                if (wrapper.getOrderSide() == OrderSide.BUY) {
                    diffOrderData.removeBid(wrapper.getOrder(message.getBook()));
                } else {
                    diffOrderData.removeAsk(wrapper.getOrder(message.getBook()));
                }

                if (wrapper.status() == OrderStatus.OPEN) {
                    if (wrapper.getOrderSide() == OrderSide.BUY) {
                        diffOrderData.addBid(wrapper.getOrder(message.getBook()));
                    } else {
                        diffOrderData.addAsk(wrapper.getOrder(message.getBook()));
                    }
                }
            });

        applyDiffOrderData();
    }

    /**
     * Stop running the thread
     */
    public void stopRunning() {
        running = false;
    }

    /**
     * Consumer that will process the data produced by this consumer
     */
    public void setDataConsumer(Consumer<DiffOrderData> dataConsumer) {
        this.dataConsumer = dataConsumer;
    }

    /**
     * Makes a copy of the data produced and calls the consumer to process it.<br/> Resets the data
     * state after calling the Platform.runLater(...).
     */
    private void applyDiffOrderData() {
        DiffOrderData copyData = new DiffOrderData(diffOrderData);

        Platform.runLater(() -> dataConsumer.accept(copyData));

        diffOrderData.reset();
    }
}
