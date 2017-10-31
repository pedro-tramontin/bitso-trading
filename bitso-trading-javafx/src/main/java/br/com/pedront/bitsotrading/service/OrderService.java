package br.com.pedront.bitsotrading.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * JavaFX Service to get the Order Book from the Bitso Public REST API.
 */
@org.springframework.stereotype.Service
public class OrderService extends Service<OrderBook> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private static final String BOOK = "btc_mxn";

    @Autowired
    private BitsoService bitsoService;

    @Override
    protected Task<OrderBook> createTask() {
        return new Task<OrderBook>() {

            @Override
            protected OrderBook call() throws Exception {
                try {
                    return bitsoService.fetchOrders(BOOK);
                } catch (Exception e) {
                    LOGGER.error("Something wrong happended with OrderService, stack trace:", e);

                    return OrderBook.EMPTY;
                }
            }
        };
    }

    @Override
    public String toString() {
        return "OrderService";
    }
}
