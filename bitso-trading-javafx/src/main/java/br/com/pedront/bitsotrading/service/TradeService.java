package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * JavaFX Service to get the trades from the Bitso Public REST API.<br/> It fetches the lastest
 * TRADES_FETCH_DEFAULT by default if no x is informed, and the next trades following x if it is
 * informed.
 */
@org.springframework.stereotype.Service
public class TradeService extends ScheduledService<List<Trade>> {

    private static final String BOOK = "btc_mxn";

    private static final int TRADES_FETCH_DEFAULT = 50;

    /**
     * The lastTid to filter the trades
     */
    private Integer lastTid;

    private SimpleIntegerProperty cacheSize;

    private SimpleIntegerProperty x;

    @Autowired
    private BitsoService bitsoService;

    public TradeService() {
        this.lastTid = 0;
        this.cacheSize = new SimpleIntegerProperty();
        this.x = new SimpleIntegerProperty();
    }

    @Override
    protected Task<List<Trade>> createTask() {
        return new Task<List<Trade>>() {

            @Override
            protected List<Trade> call() throws Exception {
                List<Trade> newTradeList;

                if (lastTid == 0) {
                    newTradeList = TradeDTOConverter
                        .convert(bitsoService.fetchTradesDesc(BOOK, TRADES_FETCH_DEFAULT));
                } else {
                    newTradeList = TradeDTOConverter
                        .convert(bitsoService
                            .fetchTradesAsc(BOOK, lastTid, TRADES_FETCH_DEFAULT));
                }

                return newTradeList;
            }
        };
    }

    public SimpleIntegerProperty cacheSizeProperty() {
        return cacheSize;
    }

    public SimpleIntegerProperty xProperty() {
        return x;
    }

    @Override
    public String toString() {
        return "TradeService";
    }
}
