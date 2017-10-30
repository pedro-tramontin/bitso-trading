package br.com.pedront.bitsotrading.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrder;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;

public class DiffOrderWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrderWrapper.class);

    private DiffOrder diffOrder;

    private Order order;

    private DiffOrderWrapper(DiffOrder diffOrder) {
        this.diffOrder = diffOrder;
    }

    public static DiffOrderWrapper newWrapper(DiffOrder diffOrder) {
        return new DiffOrderWrapper(diffOrder);
    }

    public OrderStatus status() {

        if ("open".equals(diffOrder.getStatus())) {
            return OrderStatus.OPEN;
        } else if ("completed".equals(diffOrder.getStatus())) {
            return OrderStatus.COMPLETED;
        } else if ("cancelled".equals(diffOrder.getStatus())) {
            return OrderStatus.CANCELED;
        }

        return OrderStatus.UNKNOWN;
    }

    public Boolean isValid() {
        if (status() == OrderStatus.UNKNOWN) {
            LOGGER.error("Can't process message, order status unknown, message={}", diffOrder);

            return false;
        }

        if (getOrderSide() == OrderSide.UNKNOWN) {
            LOGGER.error("Can't process message, order side unknown, message={}", diffOrder);

            return false;
        }

        return true;
    }

    public OrderSide getOrderSide() {
        if (diffOrder.getMakerSide() == 0) {
            return OrderSide.BUY;
        } else if (diffOrder.getMakerSide() == 1) {
            return OrderSide.SELL;
        }

        return OrderSide.UNKNOWN;
    }

    public Order getOrder(String book) {
        if (order == null) {
            order = new Order(book, diffOrder.getRate(), diffOrder.getAmount(),
                    diffOrder.getOid());
        }

        return order;
    }
}
