package br.com.pedront.bitsotrading.comparator;

import br.com.pedront.bitsotrading.model.Trade;
import java.util.Comparator;

public class TradeComparator implements Comparator<Trade> {

    @Override
    public int compare(final Trade trade1, final Trade trade2) {

        int resultCompare = trade2.getCreatedAt().compareTo(trade1.getCreatedAt());
        if (resultCompare == 0) {
            resultCompare = trade2.getTid().compareTo(trade1.getTid());

            // Only when the trade is simulated and needs to be above the other trade
            if (resultCompare == 0) {
                if (trade2.isSimulated()) {
                    resultCompare = 1;
                } else {
                    resultCompare = -1;
                }
            }
        }

        return resultCompare;
    }
}
