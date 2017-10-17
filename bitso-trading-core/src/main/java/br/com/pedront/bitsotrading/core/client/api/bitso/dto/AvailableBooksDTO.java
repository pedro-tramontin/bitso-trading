package br.com.pedront.bitsotrading.core.client.api.bitso.dto;

import java.util.List;

/**
 * @author ptramontin
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 10/17/17 5:18 PM
 */
public class AvailableBooksDTO {

    private String success;

    private List<Book> payload;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

    public List<Book> getPayload() {
        return payload;
    }

    public void setPayload(final List<Book> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()//
                .append("AvailableBooksDTO [")//
                .append("success=\"")//
                .append(success).append("\"")//
                .append(",payload=")//
                .append(payload)//
                .append("]");
        return builder.toString();
    }
}
