package br.com.pedront.bitsotrading.comparator;

import java.util.Comparator;

import br.com.pedront.bitsotrading.model.Trade;

/**
 * Comparators for the Trade model.
 */
public interface TradeComparators {

    /**
     * This comparator compares by descending 'created at', then by descending 'tid' and lastly by putting the simulated
     * trade above the last trade just before it.
     */
    static Comparator<Trade> compareDescending() {
        return (trade1, trade2) -> {
            int resultCompare = trade2.getCreatedAt().compareTo(trade1.getCreatedAt());
            if (resultCompare == 0) {
                resultCompare = trade2.getTid().compareTo(trade1.getTid());

                // Only when the trade is simulated, it needs to be above the last trade
                if (resultCompare == 0) {
                    if (trade2.isSimulated() && !trade1.isSimulated()) {
                        resultCompare = 1;
                    } else if (!trade2.isSimulated() && trade1.isSimulated()) {
                        resultCompare = -1;
                    } else {
                        resultCompare = 0;
                    }
                }
            }

            return resultCompare;
        };
    };

    /**
     * This comparator compares by ascending 'created' at then by ascending 'tid'
     */
    static Comparator<Trade> compareAscending() {
        return (trade1, trade2) -> {
            int resultCompare = trade1.getCreatedAt().compareTo(trade2.getCreatedAt());
            if (resultCompare == 0) {
                resultCompare = trade1.getTid().compareTo(trade2.getTid());
            }

            return resultCompare;
        };
    };
}
