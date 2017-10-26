package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import java.util.List;

/**
 * Encapsulates the response from the Bitso public API endpoint: /available_books<br/>
 * Example response:
 * 
 * <pre>
 * {
 *     "success": true,
 *     "payload": [{
 *         "book": "btc_mxn",
 *         "minimum_amount": ".003",
 *         "maximum_amount": "1000.00",
 *         "minimum_price": "100.00",
 *         "maximum_price": "1000000.00",
 *         "minimum_value": "25.00",
 *         "maximum_value": "1000000.00"
 *     }, {
 *         "book": "eth_mxn",
 *         "minimum_amount": ".003",
 *         "maximum_amount": "1000.00",
 *         "minimum_price": "100.0",
 *         "maximum_price": "1000000.0",
 *         "minimum_value": "25.0",
 *         "maximum_value": "1000000.0"
 *     }]
 * }
 * </pre>
 */
public class AvailableBooksResponse {

    private String success;

    private List<AvailableBook> payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

    public List<AvailableBook> getPayload() {
        return payload;
    }

    public void setPayload(final List<AvailableBook> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("AvailableBooksResponse [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
