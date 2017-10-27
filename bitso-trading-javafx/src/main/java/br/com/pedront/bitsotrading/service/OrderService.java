package br.com.pedront.bitsotrading.service;

import java.util.ArrayList;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/27/17 4:07 PM
 */
public class OrderService extends Service<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private static final String BOOK = "btc_mxn";

    private final BitsoService bitsoService;

    private final SimpleIntegerProperty xInteger;

    private final TableView<Order> bestBidsTableView;

    private final TableView<Order> bestAsksTableView;

    private final SimpleListProperty<Order> bids;

    private final SimpleListProperty<Order> asks;

    private SortedList<Order> sortedBids;
    private FilteredList<Order> filteredBids;

    private SortedList<Order> sortedAsks;
    private FilteredList<Order> filteredAsks;

    public OrderService(final BitsoService bitsoService, final SimpleIntegerProperty xInteger,
            final TableView<Order> bestBidsTableView, final TableView<Order> bestAsksTableView) {
        this.bitsoService = bitsoService;
        this.xInteger = xInteger;
        this.bestBidsTableView = bestBidsTableView;
        this.bestAsksTableView = bestAsksTableView;

        this.bids = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        this.asks = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {

            @Override
            protected Boolean call() throws Exception {
                try {
                    final OrderBook orderBook = bitsoService.fetchOrders(BOOK);

                    bids.addAll(orderBook.getBids());
                    asks.addAll(orderBook.getBids());
                } catch (Exception e) {
                    LOGGER.error("Something wrong happended with OrderService, stack trace:", e);

                    return false;
                }

                return true;
            }
        };
    }

    @Override
    protected void succeeded() {
        sortedBids = bids.sorted(Comparator.comparing(Order::getPrice).reversed());
        filteredBids = sortedBids.filtered(p -> sortedBids.indexOf(p) < xInteger.get());
        bestBidsTableView.setItems(filteredBids);

        sortedAsks = asks.sorted(Comparator.comparing(Order::getPrice));
        filteredAsks = sortedAsks.filtered(p -> sortedAsks.indexOf(p) < xInteger.get());
        bestAsksTableView.setItems(filteredAsks);

        reset();
    }

    @Override
    protected void failed() {
        LOGGER.info("Something went wrong, gonna try {} more times.", 3);

        reset();

        // TODO Change to [tries > 0]
        if (true) {
            // TODO [tries--]

            start();
        }
    }
}
