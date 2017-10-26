package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Encapsulates the response from the Bitso public API endpoint: /trades<br/>
 * Example response:
 *
 * <pre>
 * {
 *     "success": true,
 *     "payload": [{
 *         "book": "btc_mxn",
 *         "created_at": "2016-04-08T17:52:31.000+00:00",
 *         "amount": "0.02000000",
 *         "maker_side": "buy",
 *         "price": "5545.01",
 *         "tid": 55845
 *     }, {
 *         "book": "btc_mxn",
 *         "created_at": "2016-04-08T17:52:31.000+00:00",
 *         "amount": "0.33723939",
 *         "maker_side": "sell",
 *         "price": "5633.98",
 *         "tid": 55844
 *     }]
 * }
 * </pre>
 */
public class TradesResponse {

    private String success;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Trade> payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

    public List<Trade> getPayload() {
        return payload;
    }

    public void setPayload(final List<Trade> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("TradesResponse [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
