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

    /**
     * The x most recent trades
     */
    private SimpleIntegerProperty x;

    @Autowired
    private BitsoService bitsoService;

    @Override
    protected Task<List<Trade>> createTask() {
        return new Task<List<Trade>>() {

            @Override
            protected List<Trade> call() throws Exception {
                return TradeDTOConverter
                    .convert(bitsoService.fetchTradesDesc(BOOK, x.intValue()));
            }
        };
    }

    public SimpleIntegerProperty xProperty() {
        return x;
    }

    @Override
    public String toString() {
        return "TradeService";
    }
}
