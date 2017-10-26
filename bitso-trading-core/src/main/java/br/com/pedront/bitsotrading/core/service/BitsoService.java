package br.com.pedront.bitsotrading.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBookResponse;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Trade;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TradesResponse;

@Service
public class BitsoService {

    private static final Boolean AGGREGATE_ORDERS_DEFAULT = false;

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    public OrderBook fetchOrders(String book) {
        OrderBookResponse orderBookResponse = bitsoApiIntegration
                .orderBook(book, AGGREGATE_ORDERS_DEFAULT.toString());

        return orderBookResponse.getPayload();
    }

    public List<Trade> fetchTradesAsc(String book, Integer lastOID, Integer limit) {
        TradesResponse tradesResponse = bitsoApiIntegration.trades(book, lastOID, "asc", limit);

        return tradesResponse.getPayload();
    }

    public List<Trade> fetchTradesDesc(String book, Integer limit) {
        TradesResponse tradesResponse = bitsoApiIntegration.trades(book, null, "desc", limit);

        return tradesResponse.getPayload();
    }

}
