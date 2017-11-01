package br.com.pedront.bitsotrading.converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.TradeDTO;
import br.com.pedront.bitsotrading.model.Trade;

/**
 * Converts the TradeDTO mapping from the Bitso API to the TradeDTO model used in the TableView.
 */
public class TradeDTOConverter {

    /**
     * The Bitso API ISO 8601 time patternz<br/>
     * The predefined formatters DateTimeFormatter.ISO_OFFSET_DATE_TIME cannot be used, because the offset is expected
     * to follow the pattern (+HH:mm:ss) instead of (+ZZZZ)
     */
    private static final String BITSO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]Z";

    /**
     * Formatter for UTC
     */
    private static final DateTimeFormatter utcFormatter = initUtcFormatter();

    /**
     * Local date/time formatter
     */
    private static final DateTimeFormatter localFormatter = initLocalFormatter();

    private TradeDTOConverter() {
    }

    private static DateTimeFormatter initLocalFormatter() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(ZoneId.systemDefault());
    }

    private static DateTimeFormatter initUtcFormatter() {
        return DateTimeFormatter.ofPattern(BITSO_DATE_PATTERN);
    }

    private static String fromISO8601ToRFC1123(String iso8601Timestamp) {
        return localFormatter.format(OffsetDateTime.parse(iso8601Timestamp, utcFormatter));
    }

    /**
     * Converts a single TradeDTO to a Trade model
     */
    public static Trade convert(final TradeDTO tradeDTO) {
        return new Trade(fromISO8601ToRFC1123(tradeDTO.getCreatedAt()), tradeDTO.getMakerSide(),
                tradeDTO.getAmount(), tradeDTO.getPrice(), tradeDTO.getTid());
    }
}
