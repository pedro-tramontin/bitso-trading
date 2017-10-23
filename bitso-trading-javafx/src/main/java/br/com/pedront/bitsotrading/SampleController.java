package br.com.pedront.bitsotrading;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.pedront.bitsotrading.core.client.api.bitso.BitsoApiIntegration;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.AvailableBooksResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.BookDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderPayloadDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.OrderResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TickerDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TickerResponseDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeDTO;
import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeResponseDTO;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@FXMLController
public class SampleController implements Initializable {

    @FXML
    private Label helloLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea availableBooksTextarea;

    @FXML
    private TextArea tickerTextarea;

    @FXML
    private TextArea orderBookTextarea;

    @FXML
    private TextArea tradesTextarea;

    // Be aware: This is a Spring bean. So we can do the following:
    @Autowired
    private BitsoApiIntegration bitsoApiIntegration;

    @FXML
    private void setHelloText(final Event event) {
        final String textToBeShown = bitsoApiIntegration.getAvailableBooks().toString();
        helloLabel.setText(textToBeShown);
    }

    @PostConstruct
    private void init() {
        System.out.println("init");
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        getAvailableBooks();
        getTicker("btc_mxn");
        getOrder("btc_mxn");
        getTrade("btc_mxn");
    }

    private void getAvailableBooks() {
        final AvailableBooksResponseDTO availableBooks = bitsoApiIntegration.getAvailableBooks();

        StringBuilder strBuilder = new StringBuilder();
        strBuilder
                .append(String.format("success: %s\n", availableBooks.getSuccess()));

        final List<BookDTO> books = availableBooks.getPayload();
        for (BookDTO book : books) {
            strBuilder
                    .append("----------\n")
                    .append(String.format("Book: %s\n", book.getBook()))
                    .append("Amount:\n")
                    .append(String.format("  Minimum: %s\n", book.getMinimumAmount()))
                    .append(String.format("  Maximum: %s\n", book.getMaximumAmount()))
                    .append("Price:\n")
                    .append(String.format("  Minimum: %s\n", book.getMinimumPrice()))
                    .append(String.format("  Maximum: %s\n", book.getMaximumPrice()))
                    .append("Value:\n")
                    .append(String.format("  Minimum: %s\n", book.getMinimumValue()))
                    .append(String.format("  Maximum: %s\n", book.getMaximumValue()));
        }

        availableBooksTextarea.setText(strBuilder.toString());
    }

    private void getTicker(String book) {
        final TickerResponseDTO tickerResponse = bitsoApiIntegration.getTicker(book);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder
                .append(String.format("success: %s\n", tickerResponse.getSuccess()));

        final List<TickerDTO> tickers = tickerResponse.getPayload();
        for (TickerDTO ticker : tickers) {
            strBuilder
                    .append("----------\n")
                    .append(String.format("Book: %s\n", ticker.getBook()))
                    .append(String.format("Volume: %s\n", ticker.getVolume()))
                    .append(String.format("High: %s\n", ticker.getHigh()))
                    .append(String.format("Last: %s\n", ticker.getLast()))
                    .append(String.format("Low: %s\n", ticker.getLow()))
                    .append(String.format("Vwap: %s\n", ticker.getVwap()))
                    .append(String.format("Ask: %s\n", ticker.getAsk()))
                    .append(String.format("Bid: %s\n", ticker.getBid()))
                    .append(String.format("Created At: %s\n", ticker.getCreatedAt()));
        }

        tickerTextarea.setText(strBuilder.toString());
    }

    private void getOrder(String book) {
        final OrderResponseDTO orderResponse = bitsoApiIntegration.getOrder(book);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder
                .append(String.format("success: %s\n", orderResponse.getSuccess()));

        final OrderPayloadDTO orderPayload = orderResponse.getPayload();
        strBuilder
                .append("----------\n")
                .append(String.format("Updated At: %s\n", orderPayload.getUpdatedAt()))
                .append(String.format("Sequence: %s\n", orderPayload.getSequence()))
                .append("Asks\n");

        for (OrderDTO ask : orderPayload.getAsks()) {
            strBuilder
                    .append(String.format("  Book: %s\n", ask.getBook()))
                    .append(String.format("  Price: %s\n", ask.getPrice()))
                    .append(String.format("  Amount: %s\n", ask.getAmount()))
                    .append(String.format("  OID: %s\n", ask.getOid()))
                    .append("--\n");
        }

        strBuilder.append("Bids\n");

        for (OrderDTO bid : orderPayload.getBids()) {
            strBuilder
                    .append(String.format("  Book: %s\n", bid.getBook()))
                    .append(String.format("  Price: %s\n", bid.getPrice()))
                    .append(String.format("  Amount: %s\n", bid.getAmount()))
                    .append(String.format("  OID: %s\n", bid.getOid()))
                    .append("--\n");
        }

        orderBookTextarea.setText(strBuilder.toString());
    }

    private void getTrade(String book) {
        final TradeResponseDTO tradeResponse = bitsoApiIntegration.getTrade(book, null);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder
                .append(String.format("success: %s\n", tradeResponse.getSuccess()));

        final List<TradeDTO> trades = tradeResponse.getPayload();
        for (TradeDTO trade : trades) {
            strBuilder
                    .append("----------\n")
                    .append(String.format("Book: %s\n", trade.getBook()))
                    .append(String.format("Created At: %s\n", trade.getCreatedAt()))
                    .append(String.format("Amount: %s\n", trade.getAmount()))
                    .append(String.format("Marker Side: %s\n", trade.getMakerSide()))
                    .append(String.format("Price: %s\n", trade.getPrice()))
                    .append(String.format("TID: %s\n", trade.getTid()));
        }

        tradesTextarea.setText(strBuilder.toString());
    }
}
