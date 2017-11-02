package br.com.pedront.bitsotrading.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.converter.TradeDTOConverter;
import br.com.pedront.bitsotrading.core.service.BitsoService;
import br.com.pedront.bitsotrading.core.utils.ListUtils;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeService.class);

    private static final String BOOK = "btc_mxn";

    private Long newestTid;

    private Long oldestTid;

    private SimpleIntegerProperty cacheSize;

    private SimpleIntegerProperty x;

    @Autowired
    private BitsoService bitsoService;

    public TradeService() {
        this.newestTid = Long.MIN_VALUE;
        this.oldestTid = Long.MAX_VALUE;
        this.cacheSize = new SimpleIntegerProperty();
        this.x = new SimpleIntegerProperty();
    }

    @Override
    protected Task<List<Trade>> createTask() {
        return new Task<List<Trade>>() {

            @Override
            protected List<Trade> call() throws Exception {
                List<Trade> ascList = new ArrayList<>();
                List<Trade> descList = new ArrayList<>();
                List<Trade> resultList = new ArrayList<>();

                // We already have some trading data, get more after the last one saved
                if (hasLastTidSaved()) {
                    ascList.addAll(
                            bitsoService.fetchTradesAsc(BOOK, newestTid, x.intValue()).stream()
                                    .map(TradeDTOConverter::convert)
                                    .collect(Collectors.toList()));

                    ListUtils.getLastItem(ascList).ifPresent(trade -> newestTid = Math.max(trade.getTid(), newestTid));
                    ListUtils.getFirstItem(ascList).ifPresent(trade -> oldestTid = Math.min(trade.getTid(), oldestTid));
                }

                // We don't have any data or the cached one is not enough
                if (hasEnoughDataInCache(ascList)) {
                    descList = bitsoService.fetchTradesDesc(BOOK, oldestTid, x.intValue())
                            .stream()
                            .map(TradeDTOConverter::convert)
                            .collect(Collectors.toList());

                    ListUtils.getFirstItem(descList)
                            .ifPresent(trade -> newestTid = Math.max(trade.getTid(), newestTid));
                    ListUtils.getLastItem(descList).ifPresent(trade -> oldestTid = Math.min(trade.getTid(), oldestTid));
                }

                resultList.addAll(ascList);
                resultList.addAll(descList);

                LOGGER.debug("TradeService returning {}", resultList.size());

                return resultList;
            }
        };
    }

    private boolean hasEnoughDataInCache(final List<Trade> ascList) {
        return newestTid.equals(Long.MIN_VALUE) || (cacheSize.intValue() + ascList.size()) < x.intValue();
    }

    private boolean hasLastTidSaved() {
        return !newestTid.equals(Long.MIN_VALUE);
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
