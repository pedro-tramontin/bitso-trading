package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/18/17 5:44 PM
 */
public class TickerResponseDTO {

    private String success;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<TickerDTO> payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

    public List<TickerDTO> getPayload() {
        return payload;
    }

    public void setPayload(final List<TickerDTO> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("TickerResponseDTO [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
