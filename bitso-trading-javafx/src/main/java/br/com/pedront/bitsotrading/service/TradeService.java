package br.com.pedront.bitsotrading.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.model.Trade;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 * JavaFX Service to get the trades from the Bitso Public REST API.<br/>
 * It fetches the lastest TRADES_FETCH_DEFAULT by default if no lastTid is informed, and the next trades following
 * lastTid if it is informed.
 */
@org.springframework.stereotype.Service
public class TradeService extends ScheduledService<List<Trade>> {

    private static final String BOOK = "btc_mxn";

    private static final int TRADES_FETCH_DEFAULT = 50;

    /**
     * The lastTid to filter the trades
     */
    private Integer lastTid;

    @Autowired
    private BitsoService bitsoService;

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

    public void setLastTid(Integer lastTid) {
        this.lastTid = lastTid;
    }

    @Override
    public String toString() {
        return "TradeService";
    }
}
