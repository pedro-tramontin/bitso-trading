package br.com.pedront.bitsotrading.core.service;

/**
 * The fetch order for the trades public api
 */
public enum FetchOrder {
    ASC("asc"), DESC("desc"),;

    private String order;

    FetchOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return order;
    }
}
