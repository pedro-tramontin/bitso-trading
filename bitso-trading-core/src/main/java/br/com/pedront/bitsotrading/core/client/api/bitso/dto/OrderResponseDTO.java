package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 * $Id: $
 * @since 10/18/17 6:13 PM
 */
public class OrderResponseDTO {

    private String success;

    private OrderPayloadDTO payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public OrderPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(OrderPayloadDTO payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "OrderResponseDTO{" +
                "success='" + success + '\'' +
                ", payload=" + payload +
                '}';
    }
}
