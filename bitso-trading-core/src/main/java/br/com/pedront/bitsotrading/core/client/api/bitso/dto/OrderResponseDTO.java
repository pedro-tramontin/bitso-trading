package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 * $Id: $
 * @since 10/18/17 6:13 PM
 */
public class OrderResponseDTO {

    private String success;

    private OrdersDTO payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public OrdersDTO getPayload() {
        return payload;
    }

    public void setPayload(OrdersDTO payload) {
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
