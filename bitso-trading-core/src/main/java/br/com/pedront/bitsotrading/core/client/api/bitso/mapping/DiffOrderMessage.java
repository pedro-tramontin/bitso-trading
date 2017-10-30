package br.com.pedront.bitsotrading.core.client.api.bitso.mapping;

import java.util.List;

/**
 * Diff-Order message from WebSocket channel
 */
public class DiffOrderMessage {

    public static final DiffOrderMessage EMPTY = new DiffOrderMessage();

    /**
     * The message type, should be diff-orders
     */
    private String type;

    /**
     * Order book symbol
     */
    private String book;

    /**
     * Increasing integer value for each order book update.
     */
    private Long sequence;

    private List<DiffOrder> payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public List<DiffOrder> getPayload() {
        return payload;
    }

    public void setPayload(List<DiffOrder> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "DiffOrderMessage{" +
            "type='" + type + '\'' +
            ", book='" + book + '\'' +
            ", sequence=" + sequence +
            ", payload=" + payload +
            '}';
    }
}
