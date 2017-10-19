package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 6:28 PM
 */
public class TradeResponseDTO {

    private String success;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<TradeDTO> payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

    public List<TradeDTO> getPayload() {
        return payload;
    }

    public void setPayload(final List<TradeDTO> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("TradeResponseDTO [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
