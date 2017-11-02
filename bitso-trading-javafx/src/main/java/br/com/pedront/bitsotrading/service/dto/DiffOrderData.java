package br.com.pedront.bitsotrading.service.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;

/**
 * The data produced by the {@link br.com.pedront.bitsotrading.service.DiffOrderConsumer}.
 */
public class DiffOrderData {

    private List<Order> addBidList;

    private List<Order> addAskList;

    private List<Order> removeBidList;

    private List<Order> removeAskList;

    private Boolean reloadTrades;

    private Boolean reloadOrders;

    public DiffOrderData() {
        reset();
    }

    /**
     * Copy constructor
     */
    public DiffOrderData(DiffOrderData original) {
        this.addBidList = new ArrayList<>(original.addBidList);
        this.addAskList = new ArrayList<>(original.addAskList);
        this.removeBidList = new ArrayList<>(original.removeBidList);
        this.removeAskList = new ArrayList<>(original.removeAskList);
        this.reloadTrades = original.reloadTrades;
        this.reloadOrders = original.reloadOrders;
    }

    public void reset() {
        this.addBidList = new ArrayList<>();
        this.addAskList = new ArrayList<>();
        this.removeBidList = new ArrayList<>();
        this.removeAskList = new ArrayList<>();
        this.reloadTrades = false;
        this.reloadOrders = false;
    }

    public List<Order> getAddBidList() {
        return addBidList;
    }

    public void addBid(Order order) {
        this.addBidList.add(order);
    }

    public List<Order> getAddAskList() {
        return addAskList;
    }

    public void addAsk(Order order) {
        this.addAskList.add(order);
    }

    public List<Order> getRemoveBidList() {
        return removeBidList;
    }

    public void removeBid(Order order) {
        this.removeBidList.add(order);
    }

    public List<Order> getRemoveAskList() {
        return removeAskList;
    }

    public void removeAsk(Order order) {
        this.removeAskList.add(order);
    }

    public Boolean reloadTrades () {
        return reloadTrades;
    }

    public void setReloadTrades (Boolean newTrade) {
        this.reloadTrades = newTrade;
    }

    public Boolean reloadOrder() {
        return reloadOrders;
    }

    /** @noinspection WeakerAccess */
    public void setReloadOrders(Boolean value) {
        this.reloadOrders = value;
    }

    public void setReloadLists() {
        reset();
        setReloadTrades(true);
        setReloadOrders(true);
    }
}
