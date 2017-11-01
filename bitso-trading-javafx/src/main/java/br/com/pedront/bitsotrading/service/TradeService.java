package br.com.pedront.bitsotrading.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 * JavaFX Service to get the trades from the Bitso Public REST API.<br/>
 * It fetches the lastest TRADES_FETCH_DEFAULT by default if no x is informed, and the next trades following x if it is
 * informed.
 */
@org.springframework.stereotype.Service
public class TradeService extends ScheduledService<List<Trade>> {

    private static final String BOOK = "btc_mxn";

    private Long lastTid;

    private SimpleIntegerProperty cacheSize;

    private SimpleIntegerProperty x;

    @Autowired
    private BitsoService bitsoService;

    public TradeService() {
        this.lastTid = 0L;
        this.cacheSize = new SimpleIntegerProperty();
        this.x = new SimpleIntegerProperty();
    }

    @Override
    protected Task<List<Trade>> createTask() {
        return new Task<List<Trade>>() {

            @Override
            protected List<Trade> call() throws Exception {
                List<Trade> tradeList;

                if (lastTid == 0) {
                    tradeList = bitsoService.fetchTradesDesc(BOOK, lastTid, x.intValue()).stream()
                            .map(TradeDTOConverter::convert)
                            .collect(Collectors.toList());

                    lastTid = tradeList.get(0).getTid();
                } else {
                    tradeList = bitsoService.fetchTradesAsc(BOOK, lastTid, 2000).stream()
                            .map(TradeDTOConverter::convert)
                            .collect(Collectors.toList());

                    lastTid = tradeList.get(tradeList.size() - 1).getTid();

                    // Need to get older trades because the cache does not have enough itens
                    if (cacheSize.intValue() + tradeList.size() < x.intValue()) {
                        final List<Trade> olderTrades = bitsoService.fetchTradesDesc(BOOK, lastTid, x.intValue())
                                .stream()
                                .map(TradeDTOConverter::convert)
                                .collect(Collectors.toList());

                        tradeList.addAll(olderTrades);
                    }
                }

                return tradeList;
            }
        };
    }

    public SimpleIntegerProperty cacheSizeProperty() {
        return cacheSize;
    }

    public SimpleIntegerProperty xProperty() {
        return x;
    }

    public void restart() {
        reset();
        start();
    }

    @Override
    public String toString() {
        return "TradeService";
    }
}
