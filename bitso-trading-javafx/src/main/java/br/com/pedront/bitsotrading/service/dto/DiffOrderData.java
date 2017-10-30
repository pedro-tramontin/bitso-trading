package br.com.pedront.bitsotrading.service.dto;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import java.util.ArrayList;
import java.util.List;

public class DiffOrderData implements Cloneable {

    private List<Order> addBidList;

    private List<Order> addAskList;

    private List<Order> removeBidList;

    private List<Order> removeAskList;

    private Boolean newTrades;

    private Boolean reloadOrders;

    public DiffOrderData() {
        reset();
    }

    public void reset() {
        this.addBidList = new ArrayList<>();
        this.addAskList = new ArrayList<>();
        this.removeBidList = new ArrayList<>();
        this.removeAskList = new ArrayList<>();
        this.newTrades = false;
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

    public Boolean hasNewTrades() {
        return newTrades;
    }

    public void setNewTrades(Boolean newTrade) {
        this.newTrades = newTrade;
    }

    public Boolean reloadOrder() {
        return reloadOrders;
    }

    public void setReloadOrders(Boolean value) {
        this.reloadOrders = value;
    }

    public void setReloadLists() {
        reset();
        setNewTrades(true);
        setReloadOrders(true);
    }

    @Override
    public DiffOrderData clone() {
        DiffOrderData cloned = new DiffOrderData();
        cloned.newTrades = newTrades;
        cloned.reloadOrders = reloadOrders;
        cloned.addBidList = new ArrayList<>(addBidList);
        cloned.addAskList = new ArrayList<>(addAskList);
        cloned.removeBidList = new ArrayList<>(removeBidList);
        cloned.removeAskList = new ArrayList<>(removeAskList);

        return cloned;
    }
}
