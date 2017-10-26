package br.com.pedront.bitsotrading.core.service;

import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrdersDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeResponseDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BitsoService {

    private static final Boolean AGGREGATE_ORDERS_DEFAULT = false;

    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    public OrdersDTO fetchOrders(String book) {
        OrderResponseDTO responseDTO = bitsoApiIntegration
            .getOrder(book, AGGREGATE_ORDERS_DEFAULT.toString());

        return responseDTO.getPayload();
    }

    public List<TradeDTO> fetchTradesAsc(String book, Integer lastOID, Integer limit) {
        TradeResponseDTO responseDTO = bitsoApiIntegration.getTrade(book, lastOID, "asc", limit);

        return responseDTO.getPayload();
    }

    public List<TradeDTO> fetchTradesDesc(String book, Integer limit) {
        TradeResponseDTO responseDTO = bitsoApiIntegration.getTrade(book, null, "desc", limit);

        return responseDTO.getPayload();
    }

}
