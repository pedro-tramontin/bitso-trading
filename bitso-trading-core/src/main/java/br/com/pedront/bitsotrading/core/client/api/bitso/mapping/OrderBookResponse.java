package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

/**
 * Encapsulates the response from the Bitso public API endpoint: /order_book<br/>
 * Example aggregated response:
 *
 * <pre>
 * {
 *     "success": true,
 *     "payload": {
 *         "asks": [{
 *             "book": "btc_mxn",
 *             "price": "5632.24",
 *             "amount": "1.34491802"
 *         },{
 *             "book": "btc_mxn",
 *             "price": "5633.44",
 *             "amount": "0.4259"
 *         },{
 *             "book": "btc_mxn",
 *             "price": "5642.14",
 *             "amount": "1.21642"
 *         }],
 *         "bids": [{
 *             "book": "btc_mxn",
 *             "price": "6123.55",
 *             "amount": "1.12560000"
 *         },{
 *             "book": "btc_mxn",
 *             "price": "6121.55",
 *             "amount": "2.23976"
 *         }],
 *         "updated_at": "2016-04-08T17:52:31.000+00:00",
 *         "sequence": "27214"
 *     }
 * }
 * </pre>
 * 
 * <br/>
 * Example full response (unaggregated):
 * 
 * <pre>
 * {
 *     "success": true,
 *     "payload": {
 *         "asks": [{
 *             "book": "btc_mxn",
 *             "price": "5632.24",
 *             "amount": "1.34491802",
 *             "oid": "VN5lVpgXf02o6vJ6"
 *         },{
 *             "book": "btc_mxn",
 *             "price": "5633.44",
 *             "amount": "0.4259",
 *             "oid": "RP8lVpgXf04o6vJ6"
 *         },{
 *             "book": "btc_mxn",
 *             "price": "5642.14",
 *             "amount": "1.21642",
 *             "oid": "46efbiv72drbphig"
 *         }],
 *         "bids": [{
 *             "book": "btc_mxn",
 *             "price": "6123.55",
 *             "amount": "1.12560000",
 *             "oid": "11brtiv72drbphig"
 *         },{
 *             "book": "btc_mxn",
 *             "price": "6121.55",
 *             "amount": "2.23976",
 *             "oid": "1ywri0yg8miihs80"
 *         }],
 *         "updated_at": "2016-04-08T17:52:31.000+00:00",
 *         "sequence": "27214"
 *     }
 * }
 * </pre>
 */
public class OrderBookResponse {

    private String success;

    private OrderBook payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public OrderBook getPayload() {
        return payload;
    }

    public void setPayload(OrderBook payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("OrderBookResponse [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
