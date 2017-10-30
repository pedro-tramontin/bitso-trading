package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Order;
import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.OrderBook;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ptramontin
 * @version $Revision: $<br/> $Id: $
 * @since 10/27/17 4:07 PM
 */
@org.springframework.stereotype.Service
public class OrderService extends Service<OrderBook> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private static final String BOOK = "btc_mxn";

    @Autowired
    private BitsoService bitsoService;

    private SimpleIntegerProperty xProperty;

    private TableView<Order> bestBidsTableView;

    private TableView<Order> bestAsksTableView;

    private SimpleListProperty<Order> bids;

    private SimpleListProperty<Order> asks;

    private SortedList<Order> sortedBids;
    private FilteredList<Order> filteredBids;

    private SortedList<Order> sortedAsks;
    private FilteredList<Order> filteredAsks;

    /*public OrderService(final BitsoService bitsoService, final SimpleIntegerProperty xProperty,
        final TableView<Order> bestBidsTableView, final TableView<Order> bestAsksTableView) {
        this.bitsoService = bitsoService;
        this.xProperty = xProperty;
        this.bestBidsTableView = bestBidsTableView;
        this.bestAsksTableView = bestAsksTableView;

        this.bids = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));
        this.asks = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

        this.bitsoWebSocketService = new BitsoWebSocketService();

        this.xProperty.addListener((observable, oldValue, newValue) -> {
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < newValue.intValue());
            filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < newValue.intValue());
        });

        //bitsoWebSocketService.start();
        //bitsoWebSocketService.startConsumer(new DiffOrderConsumer(new DiffOrderWrapper(this)));
    }*/

    @Override
    protected Task<OrderBook> createTask() {
        return new Task<OrderBook>() {

            @Override
            protected OrderBook call() throws Exception {
                try {
                    //final OrderBook orderBook = bitsoService.fetchOrders(BOOK);

                    /*bids.addAll(orderBook.getBids());
                    asks.addAll(orderBook.getBids());*/

                    return bitsoService.fetchOrders(BOOK);
                } catch (Exception e) {
                    LOGGER.error("Something wrong happended with OrderService, stack trace:", e);

                    return OrderBook.EMPTY;
                }
            }
        };
    }

    /*@Override
    protected void succeeded() {
        sortedBids = bids.sorted(Comparator.comparing(Order::getPrice).reversed());
        filteredBids = sortedBids.filtered(p -> sortedBids.indexOf(p) < xProperty.get());
        bestBidsTableView.setItems(filteredBids);

        sortedAsks = asks.sorted(Comparator.comparing(Order::getPrice));
        filteredAsks = sortedAsks.filtered(p -> sortedAsks.indexOf(p) < xProperty.get());
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
    }*/

    public Optional<Order> searchBid(String bidId) {
        return bids.stream()
            .filter(o -> bidId.equals(o.getOid()))
            .findFirst();
    }

    public Optional<Order> searchAsk(String bidId) {
        return asks.stream()
            .filter(o -> bidId.equals(o.getOid()))
            .findFirst();
    }

    public Consumer<Order> removeBidConsumer(Order order) {
        return (o) -> {
            bids.remove(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xProperty.get());
        };
    }

    public Consumer<Order> removeAskConsumer(Order order) {
        return (o) -> {
            asks.remove(order);
            filteredAsks.setPredicate(p -> sortedAsks.indexOf(p) < xProperty.get());
        };
    }

    public Consumer<Order> addBidConsumer(Order order) {
        return (o) -> {
            bids.add(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xProperty.get());
        };
    }

    public Consumer<Order> addAskConsumer(Order order) {
        return (o) -> {
            asks.add(order);
            filteredBids.setPredicate(p -> sortedBids.indexOf(p) < xProperty.get());
        };
    }

    @Override
    public String toString() {
        return "OrderService";
    }
}
