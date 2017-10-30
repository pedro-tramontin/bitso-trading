package br.com.pedront.bitsotrading.websocket;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.exception.InvalidJsonMessageException;
import br.com.pedront.bitsotrading.wrapper.DiffOrderJsonWrapper;

/**
 * Consumer for diff-order messages from the WebSocket channel.
 */
public class DiffOrderWebSocketConsumer implements Consumer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrderWebSocketConsumer.class);

    /** Queue to put the new diff-order messages */
    private BlockingQueue<DiffOrderMessage> ordersQueue;

    public DiffOrderWebSocketConsumer (BlockingQueue<DiffOrderMessage> ordersQueue) {
        this.ordersQueue = ordersQueue;
    }

    @Override
    public void accept(final String message) {
        try {
            DiffOrderJsonWrapper wrapper = DiffOrderJsonWrapper.newInstance(message);
            switch (wrapper.getType()) {
            case SUBSCRIBE_ACK:

                // If we were subscribing to more channels, each subscribe ack received should
                // be processed here

                LOGGER.info("Received ACK to Diff-Orders subscribe.");
                break;
            case DIFF_ORDER:
                processDiffOrderMessage(wrapper.getDiffOrderMessage());
                break;
            case KEEP_ALIVE:
                // just ignore keep-alive packages
                break;
            default:
                LOGGER.warn("Don't know how to treat this message, message={}", message);
            }
        } catch (InvalidJsonMessageException e) {
            LOGGER.error("The message is invalid, can't process that");
        }
    }

    private void processDiffOrderMessage(DiffOrderMessage diffOrderMessage) {
        if (!diffOrderMessage.equals(DiffOrderMessage.EMPTY)) {
            if (diffOrderMessage.getPayload() != null) {
                try {
                    LOGGER.debug("Adding new Diff-Order message to queue");

                    ordersQueue.put(diffOrderMessage);
                } catch (InterruptedException e) {
                    LOGGER.error("Received interruption while adding message to queue, message={}",
                            diffOrderMessage);

                    Thread.currentThread().interrupt();
                }
            } else {
                LOGGER.error("The diff-order payload is empty, message={}", diffOrderMessage);
            }
        }
    }
}
