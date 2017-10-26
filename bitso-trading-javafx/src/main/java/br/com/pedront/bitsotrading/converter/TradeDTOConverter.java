package br.com.pedront.bitsotrading.converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.Trade;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/20/17 4:37 PM
 */
public class TradeDTOConverter {

    /**
     * The Bitso API ISO 8601 time patternz<br/>
     * The predefined formatters DateTimeFormatter.ISO_OFFSET_DATE_TIME cannot be used, because the offset is expected
     * to follow the pattern (+HH:mm:ss) instead of (+ZZZZ)
     */
    private static final String BITSO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]Z";

    private static final DateTimeFormatter utcFormatter = initUtcFormatter();

    private static final DateTimeFormatter localFormatter = initLocalFormatter();

    private static DateTimeFormatter initLocalFormatter() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
    }

    private static DateTimeFormatter initUtcFormatter() {
        return DateTimeFormatter.ofPattern(BITSO_DATE_PATTERN);
    }

    private static String fromISO8601ToRFC1123(String iso8601Timestamp) {
        return localFormatter.format(OffsetDateTime.parse(iso8601Timestamp, utcFormatter));
    }

    private static br.com.pedront.bitsotrading.model.Trade convert(final Trade trade) {
        return new br.com.pedront.bitsotrading.model.Trade(fromISO8601ToRFC1123(trade.getCreatedAt()), trade.getMakerSide(), trade
                .getAmount(),
                trade.getPrice(), trade.getTid());
    }

    public static List<br.com.pedront.bitsotrading.model.Trade> convert(final List<Trade> trades) {
        return trades.stream().map(TradeDTOConverter::convert).collect(Collectors.toList());
    }

}
