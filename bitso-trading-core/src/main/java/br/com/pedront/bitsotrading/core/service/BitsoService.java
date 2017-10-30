package br.com.pedront.bitsotrading.core.service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBookResponse;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Trade;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TradesResponse;

/**
 * Encapsulates the calls to the Bitso API, with specific functions used by the application.
 */
@Service
public class BitsoService {

    private static final Boolean AGGREGATE_ORDERS_DEFAULT = false;

    public static final String DIFF_ORDER_CHANNEL = "diff-orders";

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    /**
     * Fetches the Order Book from the public REST API.
     *
     * @param book
     *            the order book symbol.
     */
    public OrderBook fetchOrders(String book) {
        OrderBookResponse orderBookResponse = bitsoApiIntegration
                .orderBook(book, AGGREGATE_ORDERS_DEFAULT.toString());

        return orderBookResponse.getPayload();
    }

    /**
     * Fetches the trades in ascending order from the public REST API.
     *
     * @param book
     *            the order book symbol.
     * @param lastOID
     *            the last order ID to be filtered.
     * @param limit
     *            the limit number of trades to be returned.
     */
    public List<Trade> fetchTradesAsc(String book, Integer lastOID, Integer limit) {
        TradesResponse tradesResponse = bitsoApiIntegration.trades(book, lastOID, "asc", limit);

        return tradesResponse.getPayload();
    }

    /**
     * Fetches the trades in descending order from the public REST API.
     *
     * @param book
     *            the order book symbol.
     * @param limit
     *            the limit number of trades to be returned.
     */
    public List<Trade> fetchTradesDesc(String book, Integer limit) {
        TradesResponse tradesResponse = bitsoApiIntegration.trades(book, null, "desc", limit);

        return tradesResponse.getPayload();
    }

    /**
     * Subscribes to the diff-orders WebSocket channel.
     *
     * @param messageConsumer
     *            a consumer for the channel's messages
     */
    public BitsoWebSocketService subscribeToDiffOrders(
            Consumer<String> messageConsumer) {

        return BitsoWebSocketService
                .subscribe(DIFF_ORDER_CHANNEL)
                .with(messageConsumer);
    }
}
