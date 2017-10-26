package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Encapsulates the response from the Bitso public API endpoint: /ticker<br/>
 * Example response:
 *
 * <pre>
 * {
 *     "success": true,
 *     "payload": {
 *         "book": "btc_mxn",
 *         "volume": "22.31349615",
 *         "high": "5750.00",
 *         "last": "5633.98",
 *         "low": "5450.00",
 *         "vwap": "5393.45",
 *         "ask": "5632.24",
 *         "bid": "5520.01",
 *         "created_at": "2016-04-08T17:52:31.000+00:00"
 *     }
 * }
 * </pre>
 */
public class TickerResponse {

    private String success;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Ticker> payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

    public List<Ticker> getPayload() {
        return payload;
    }

    public void setPayload(final List<Ticker> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("TickerResponse [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
