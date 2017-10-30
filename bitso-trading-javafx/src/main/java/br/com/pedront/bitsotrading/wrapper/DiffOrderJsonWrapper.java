package br.com.pedront.bitsotrading.wrapper;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.exception.InvalidJsonMessageException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiffOrderJsonWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrderJsonWrapper.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private String message;

    private JsonNode jsonMessage;

    private DiffOrderJsonWrapper(String message) {
        this.message = message;
    }

    public static DiffOrderJsonWrapper newInstance(String message)
        throws InvalidJsonMessageException {
        DiffOrderJsonWrapper msgWrapper = new DiffOrderJsonWrapper(message);
        if (msgWrapper.messageToJsonNode()) {
            return msgWrapper;
        } else {
            throw new InvalidJsonMessageException();
        }
    }

    public BitsoWebSocketMessageType getType() {
        if (jsonMessage.has("action") && jsonMessage.has("response")) {
            String action = jsonMessage.get("action").asText();
            String response = jsonMessage.get("response").asText();

            if ("subscribe".equals(action) && "ok".equals(response)) {
                return BitsoWebSocketMessageType.SUBSCRIBE_ACK;
            }
        } else if (jsonMessage.has("type")) {
            String type = jsonMessage.get("type").asText();

            if ("diff-orders".equals(type)) {
                return BitsoWebSocketMessageType.DIFF_ORDER;
            } else if ("ka".equals(type)) {
                return BitsoWebSocketMessageType.KEEP_ALIVE;
            }
        }

        return BitsoWebSocketMessageType.UNKNOWN;
    }

    public DiffOrderMessage getDiffOrderMessage() {
        try {
            return JSON_MAPPER.readValue(message, DiffOrderMessage.class);
        } catch (IOException e) {
            LOGGER
                .error("Error converting message to DiffOrderMessage object, message={}.", message);
        }

        return DiffOrderMessage.EMPTY;
    }

    private boolean messageToJsonNode() {
        try {
            jsonMessage = JSON_MAPPER.readTree(message);
        } catch (IOException e) {
            LOGGER.error("Error converting message to JsonNode, message={}", message, e);

            return false;
        }

        return true;
    }
}
