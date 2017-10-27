package br.com.pedront.bitsotrading;

import br.com.pedront.bitsotrading.model.Trade;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.util.converter.NumberStringConverter;

/**
 * @author ptramontin
 * @version $Revision: $<br/> $Id: $
 * @since 10/26/17 6:13 PM
 */
public class ContrarianTrading {

    private Integer simulatorCount = 0;

    private Double lastTradePrice = 0.0;

    private SimpleIntegerProperty upticks = new SimpleIntegerProperty();

    private SimpleIntegerProperty downTicks = new SimpleIntegerProperty();

    private SimpleBooleanProperty enabled = new SimpleBooleanProperty();

    public ContrarianTrading(SimpleIntegerProperty upticks, SimpleIntegerProperty downTicks,
        SimpleBooleanProperty enabled) {

        this.upticks = upticks;
        this.downTicks = downTicks;
        this.enabled = enabled;
    }

    public Boolean isEnabled() {
        return enabled.get();
    }

    public Trade simulate(Trade newTrade) {
        Trade simulatedTrade = null;

        if (newTrade.getPrice() > lastTradePrice) {
            simulatorCount++;
        } else if (newTrade.getPrice() < lastTradePrice) {
            simulatorCount--;
        }

        if (simulatorCount == upticks.get()) {
            simulatedTrade = new Trade(newTrade, "sell", 1.0);
            simulatorCount = 0;
        } else if (simulatorCount == -downTicks.get()) {
            simulatedTrade = new Trade(newTrade, "buy", 1.0);
            simulatorCount = 0;
        }

        lastTradePrice = newTrade.getPrice();

        return simulatedTrade;
    }
}
