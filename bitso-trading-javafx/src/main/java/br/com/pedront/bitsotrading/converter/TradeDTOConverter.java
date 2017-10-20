package br.com.pedront.bitsotrading.converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pedront.bitsotrading.core.client.api.bitso.dto.TradeDTO;
import br.com.pedront.bitsotrading.model.Trade;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/20/17 4:37 PM
 */
public class TradeDTOConverter {

    private static final String BITSO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final DateTimeFormatter utcFormatter = initUtcFormatter();

    private static final DateTimeFormatter localFormatter = initLocalFormatter();

    private static DateTimeFormatter initLocalFormatter() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
    }

    private static DateTimeFormatter initUtcFormatter() {
        return DateTimeFormatter.ofPattern(BITSO_DATE_PATTERN).withZone(ZoneId.of("UTC"));
    }

    private static String fromISO8601ToRFC1123(String iso8601Timestamp) {
        return localFormatter.format(OffsetDateTime.parse(iso8601Timestamp, utcFormatter));
    }

    private static Trade convert(final TradeDTO tradeDTO) {
        return new Trade(fromISO8601ToRFC1123(tradeDTO.getCreatedAt()), tradeDTO.getMakerSide(), tradeDTO.getAmount(),
                tradeDTO.getPrice());
    }

    public static List<Trade> convert(final List<TradeDTO> tradeDTOs) {
        return tradeDTOs.stream().map(TradeDTOConverter::convert).collect(Collectors.toList());
    }

}
