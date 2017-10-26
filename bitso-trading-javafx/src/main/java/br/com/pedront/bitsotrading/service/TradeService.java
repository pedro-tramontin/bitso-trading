package br.com.pedront.bitsotrading.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.ContrarianTrading;
import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

@org.springframework.stereotype.Service
public class TradeService extends Service<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    private static final String BOOK = "btc_mxn";

    public static final int TRADES_FETCH_DEFAULT = 50;

    private SimpleListProperty<Trade> newTrades;

    private SimpleIntegerProperty lastTid;

    private ContrarianTrading simulator;

    @Autowired
    private BitsoService bitsoService;

    public TradeService() {
        this.newTrades = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

        this.lastTid = new SimpleIntegerProperty();
    }

    public ObservableList<Trade> getNewTrades() {
        return newTrades.get();
    }

    public SimpleListProperty<Trade> newTradesProperty() {
        return newTrades;
    }

    public void setNewTrades(final ObservableList<Trade> newTrades) {
        this.newTrades.set(newTrades);
    }

    public void bindLastTidTo(SimpleIntegerProperty lastTid) {
        this.lastTid.bind(lastTid);
    }

    public void setSimulator(ContrarianTrading simulator) {
        this.simulator = simulator;
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
                                .convert(bitsoService.fetchTradesAsc(BOOK, lastTid.get(), TRADES_FETCH_DEFAULT));
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
}
