package br.com.pedront.bitsotrading.core.service;

import static br.com.pedront.bitsotrading.core.service.FetchOrder.ASC;
import static br.com.pedront.bitsotrading.core.service.FetchOrder.DESC;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBookResponse;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TradeDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TradesResponse;

/**
 * Encapsulates the calls to the Bitso API, with specific functions used by the application.
 */
@Service
public class BitsoService {

    private static final Boolean AGGREGATE_ORDERS_DEFAULT = false;

    private static final String DIFF_ORDER_CHANNEL = "diff-orders";

    private static final Integer FETCH_LIMIT = 200;

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
     *            order book symbol.
     * @param lastOID
     *            last order ID to be filtered.
     * @param quantity
     *            quantity of trades to be returned.
     */
    public List<TradeDTO> fetchTradesAsc(final String book, final Long lastOID, final Integer quantity) {
        if (quantity > FETCH_LIMIT) {
            return fetchMoreThanLimit(book, lastOID, ASC, quantity);
        } else {
            return fetchInsideLimit(book, lastOID, ASC, quantity);
        }
    }

    /**
     * Fetches the trades in descending order from the public REST API.
     *
     * @param book
     *            the order book symbol.
     * @param lastOID
     *            last order ID to be filtered.
     * @param quantity
     *            quantity of trades to be returned.
     */
    public List<TradeDTO> fetchTradesDesc(final String book, final Long lastOID, final Integer quantity) {
        if (quantity > FETCH_LIMIT) {
            return fetchMoreThanLimit(book, lastOID, DESC, quantity);
        } else {
            return fetchInsideLimit(book, lastOID, DESC, quantity);
        }
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

    /**
     * Returns quantity if it is less than FETCH_LIMIT or FETCH_LIMIT otherwise
     * 
     * @param quantity
     *            the quantity to test
     */
    private Integer getFetchMaxOr(Integer quantity) {
        Integer fetchQuantity;
        if (quantity > FETCH_LIMIT) {
            fetchQuantity = FETCH_LIMIT;
        } else {
            fetchQuantity = quantity;
        }

        return fetchQuantity;
    }

    /**
     * Fetches from trades public api more itens than the limit FETCH_LIMIT.
     */
    private List<TradeDTO> fetchMoreThanLimit(final String book, final Long lastOID, final FetchOrder order,
            final Integer quantity) {
        List<TradeDTO> response = new ArrayList<>();

        Integer remaining = quantity;

        Boolean finished = false;
        while (!finished || (remaining > 0)) {
            Integer fetchCallQtd = getFetchMaxOr(remaining);

            TradesResponse tradesResponse = bitsoApiIntegration.trades(book, lastOID, order.toString(), fetchCallQtd);

            remaining -= fetchCallQtd;

            if (tradesResponse.getPayload().size() < fetchCallQtd) {
                finished = true;
            }

            response.addAll(tradesResponse.getPayload());
        }

        return response;
    }

    /**
     * Call this function only when the desired quantity is less than the limit FETCH_LIMIT.
     */
    private List<TradeDTO> fetchInsideLimit(final String book, final Long lastOID, final FetchOrder order,
            final Integer quantity) {
        TradesResponse tradesResponse = bitsoApiIntegration.trades(book, lastOID, order.toString(), quantity);

        return tradesResponse.getPayload();
    }
}
