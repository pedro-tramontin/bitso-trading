package br.com.pedront.bitsotrading.wrapper;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrder;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;

public class DiffOrderWrapper {

    private DiffOrder diffOrder;

    private DiffOrderWrapper(DiffOrder diffOrder) {
        this.diffOrder = diffOrder;
    }

    public static DiffOrderWrapper newWrapper(DiffOrder diffOrder) {
        return new DiffOrderWrapper(diffOrder);
    }

    public DiffOrderStatus status() {

        if ("open".equals(diffOrder.getStatus())) {
            return DiffOrderStatus.OPEN;
        } else if ("completed".equals(diffOrder.getStatus())) {
            return DiffOrderStatus.COMPLETED;
        } else if ("cancelled".equals(diffOrder.getStatus())) {
            return DiffOrderStatus.CANCELED;
        }

        return DiffOrderStatus.UNKNOWN;
    }

    public DiffOrder getDiffOrder() {
        return diffOrder;
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
        return new Order(book, diffOrder.getRate(), diffOrder.getAmount(),
            diffOrder.getOid());
    }
}
