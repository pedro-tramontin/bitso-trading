package br.com.pedront.bitsotrading.service;

import br.com.pedront.bitsotrading.model.Trade;
import org.springframework.stereotype.Service;

@Service
public class ContrarianTradingService {

    private Integer counter = 0;

    private Double lastTradePrice = 0.0;

    private Integer upticks;

    private Integer downTicks;

    public void init(Integer upticks, Integer downTicks, Double lastTradePrice) {
        this.upticks = upticks;
        this.downTicks = downTicks;
        this.lastTradePrice = lastTradePrice;
    }

    public void setUpticks(Integer upticks) {
        resetCounter();

        this.upticks = upticks;
    }

    public void setDownTicks(Integer downTicks) {
        resetCounter();

        this.downTicks = downTicks;
    }

    private void resetCounter() {
        counter = 0;
    }

    public Trade simulate(Trade newTrade) {
        Trade simulatedTrade = null;

        if (newTrade.getPrice() > lastTradePrice) {
            counter++;
        } else if (newTrade.getPrice() < lastTradePrice) {
            counter--;
        }

        if (counter == upticks) {
            simulatedTrade = new Trade(newTrade, "sell", 1.0);
            counter = 0;
        } else if (counter == -downTicks) {
            simulatedTrade = new Trade(newTrade, "buy", 1.0);
            counter = 0;
        }

        lastTradePrice = newTrade.getPrice();

        return simulatedTrade;
    }
}
