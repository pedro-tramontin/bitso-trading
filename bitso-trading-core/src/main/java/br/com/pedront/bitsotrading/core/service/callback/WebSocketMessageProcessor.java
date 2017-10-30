package br.com.pedront.bitsotrading.core.service.callback;

/**
 * Processor for diff-order messages from the Bitso WebSocket API.
 */
public interface WebSocketMessageProcessor {

    /**
     * Process the received message.
     */
    void process(String message);

}
