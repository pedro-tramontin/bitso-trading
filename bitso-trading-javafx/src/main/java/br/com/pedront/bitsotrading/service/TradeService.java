package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.ContrarianTrading;
import br.com.pedront.bitsotrading.TradeComparator;
import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeService extends Service<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    private static final String BOOK = "btc_mxn";

    public static final int TRADES_FETCH_DEFAULT = 50;

    private SimpleIntegerProperty xProperty;
    private SimpleIntegerProperty lastTid;
    private SimpleListProperty<Trade> newTrades;

    private SortedList<Trade> sortedTrades;
    private FilteredList<Trade> filteredTrades;
    private TableView<Trade> tradeTableView;

    private BitsoService bitsoService;

    private ContrarianTrading simulator;

    public TradeService(BitsoService bitsoService, SimpleIntegerProperty xProperty,
        SimpleIntegerProperty mProperty, SimpleIntegerProperty nProperty,
        SimpleBooleanProperty enableSimulation, TableView<Trade> tradeTableView) {

        this.bitsoService = bitsoService;
        this.xProperty = xProperty;
        this.tradeTableView = tradeTableView;

        this.simulator = new ContrarianTrading(mProperty, nProperty, enableSimulation);

        this.newTrades = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

        this.lastTid = new SimpleIntegerProperty();

        this.xProperty.addListener((observable, oldValue, newValue) -> {
            filteredTrades.setPredicate(p -> sortedTrades.indexOf(p) < newValue.intValue());
        });
    }

    public SimpleListProperty<Trade> newTradesProperty() {
        return newTrades;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {

            @Override
            protected Boolean call() throws Exception {
                try {
                    List<Trade> newTradeList;

                    if (lastTid.get() == 0) {
                        newTradeList = TradeDTOConverter
                            .convert(bitsoService.fetchTradesDesc(BOOK, TRADES_FETCH_DEFAULT));
                    } else {
                        newTradeList = TradeDTOConverter
                            .convert(bitsoService
                                .fetchTradesAsc(BOOK, lastTid.get(), TRADES_FETCH_DEFAULT));
                    }

                    if (simulator.isEnabled()) {
                        final List<Trade> simTrades = newTradeList.stream()
                            .map(simulator::simulate)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                        newTradesProperty().addAll(simTrades);
                    }

                    newTradesProperty().addAll(newTradeList);
                } catch (Exception e) {
                    LOGGER.error("Something wrong happended with TradeService, exception={}", e);
                    e.printStackTrace();

                    return false;
                }

                return true;
            }
        };
    }

    @Override
    protected void succeeded() {
        sortedTrades = newTradesProperty().sorted(new TradeComparator());
        filteredTrades = sortedTrades.filtered(p -> sortedTrades.indexOf(p) < xProperty.get());
        tradeTableView.setItems(filteredTrades);

        if (sortedTrades.size() > 0) {
            lastTid.set(sortedTrades.get(0).getTid());
        }

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
