package br.com.pedront.bitsotrading.service;

import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.core.service.BitsoWebSocketService;
import br.com.pedront.bitsotrading.websocket.DiffOrderWebSocketConsumer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Service to connect with Bitso WebSocket
 */
@org.springframework.stereotype.Service
public class OrderWebSocketService extends Service<BitsoWebSocketService> {

    @Autowired
    private BitsoService bitsoService;

    private BlockingQueue<DiffOrderMessage> ordersQueue;

    @Override
    protected Task<BitsoWebSocketService> createTask() {
        return new Task<BitsoWebSocketService>() {

            @Override
            protected BitsoWebSocketService call() throws Exception {
                return bitsoService.subscribeToDiffOrders(new DiffOrderWebSocketConsumer(ordersQueue));
            }
        };
    }

    public void setOrdersQueue(
            final BlockingQueue<DiffOrderMessage> ordersQueue) {
        this.ordersQueue = ordersQueue;
    }
}
