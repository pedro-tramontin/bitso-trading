package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import java.util.Collections;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class TradeService extends Service<List<Trade>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    private static final String BOOK = "btc_mxn";

    private static final int TRADES_FETCH_DEFAULT = 50;

    private Integer lastTid;

    @Autowired
    private BitsoService bitsoService;

    @Override
    protected Task<List<Trade>> createTask() {
        return new Task<List<Trade>>() {

            @Override
            protected List<Trade> call() throws Exception {
                try {
                    List<Trade> newTradeList;

                    if (lastTid == 0) {
                        newTradeList = TradeDTOConverter
                            .convert(bitsoService.fetchTradesDesc(BOOK, TRADES_FETCH_DEFAULT));
                    } else {
                        newTradeList = TradeDTOConverter
                            .convert(bitsoService
                                .fetchTradesAsc(BOOK, lastTid, TRADES_FETCH_DEFAULT));
                    }

                    /*if (simulator.isEnabled()) {
                        final List<Trade> simTrades = newTradeList.stream()
                            .map(simulator::simulate)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                        tradesProperty().addAll(simTrades);
                    }*/

                    return newTradeList;
                } catch (Exception e) {
                    LOGGER.error("Something wrong happended with TradeService, stack trace:", e);

                    return Collections.emptyList();
                }
            }
        };
    }

    public void setLastTid(Integer lastTid) {
        this.lastTid = lastTid;
    }

    @Override
    public String toString() {
        return "TradeService";
    }
}
