package br.com.pedront.bitsotrading.service;

import org.springframework.stereotype.Service;

import br.com.pedront.bitsotrading.model.Trade;

/**
 * Simulates a Contrarian Trading Strategy.<br/>
 * It takes three parameters for initialization: number of upticks, number of downticks and last trade price.<br/>
 * After initialization, call the simulate method passing every new trade that is done as a parameter.<br/>
 */
@Service
public class ContrarianTradingService {

    /** The number gets positive for upticks and negative for downticks */
    private Integer counter = 0;

    /** The price for the last trade */
    private Double lastTradePrice = 0.0;

    /** Number of consecutive upticks to simulate a sell Trade */
    private Integer upticks;

    /** Number of consecutive downticks to simulate a buy Trade */
    private Integer downTicks;

    public void init(Integer upticks, Integer downTicks, Double lastTradePrice) {
        this.upticks = upticks;
        this.downTicks = downTicks;
        this.lastTradePrice = -1.0;
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

    /**
     * Call the simulate method passing every new trade that is done as a parameter.<br/>
     * If the number of upticks or downticks is reached, a new simulated Trade is created based on the Trade
     * informed.<br/>
     * If the number of upticks is reached, a new simulated sell Trade is made, with 1.0 as the amount and the value
     * equals to the Trade being processed.<br/>
     * If the number of downticks is reached, a new simulated buy Trade is made, just like for the upticks.
     */
    public Trade simulate(Trade trade) {
        Trade simulatedTrade = null;

        // It is -1.0 on '0 tick'
        if (lastTradePrice != -1.0) {
            if (trade.getPrice() > lastTradePrice) {
                counter++;
            } else if (trade.getPrice() < lastTradePrice) {
                counter--;
            }

            if (counter.equals(upticks)) {
                simulatedTrade = new Trade(trade, "sell", 1.0);
                counter = 0;
            } else if (counter.equals(-downTicks)) {
                simulatedTrade = new Trade(trade, "buy", 1.0);
                counter = 0;
            }
        }

        lastTradePrice = trade.getPrice();

        return simulatedTrade;
    }
}
