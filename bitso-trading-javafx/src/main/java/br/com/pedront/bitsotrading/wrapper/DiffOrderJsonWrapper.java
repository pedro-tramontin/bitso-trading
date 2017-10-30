package br.com.pedront.bitsotrading.wrapper;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pedront.bitsotrading.core.client.api.bitso.mapping.DiffOrderMessage;
import br.com.pedront.bitsotrading.exception.InvalidJsonMessageException;

/**
 * Wrapper for the Diff-Order JSON Object.
 */
public class DiffOrderJsonWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffOrderJsonWrapper.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /* Constants for the JSON parsing */
    private static final String JSON_ACTION_FIELD = "action";

    private static final String JSON_ACTION_SUBSCRIBE = "subscribe";

    private static final String JSON_RESPONSE_FIELD = "response";

    private static final String JSON_RESPONSE_OK = "ok";

    private static final String JSON_TYPE_FIELD = "type";

    private static final String JSON_TYPE_DIFF_ORDER = "diff-orders";

    private static final String JSON_TYPE_KEEP_ALIVE = "ka";

    /** The message received from the WebSocket */
    private String message;

    /** The message converted to a JSON Object */
    private JsonNode jsonMessage;

    private DiffOrderJsonWrapper(String message) {
        this.message = message;
    }

    /**
     * Tries to convert the JSON string to a JSON Object
     * 
     * @throws InvalidJsonMessageException
     *             when a error occurs while converting the JSON String to a JSON Object
     */
    public static DiffOrderJsonWrapper newInstance(String message)
            throws InvalidJsonMessageException {
        DiffOrderJsonWrapper msgWrapper = new DiffOrderJsonWrapper(message);
        if (msgWrapper.messageToJsonNode()) {
            return msgWrapper;
        } else {
            throw new InvalidJsonMessageException();
        }
    }

    /**
     * Parses the type of the message.
     */
    public WebSocketMessageType getType() {
        if (jsonMessage.has(JSON_ACTION_FIELD) && jsonMessage.has(JSON_RESPONSE_FIELD)) {
            String action = jsonMessage.get(JSON_ACTION_FIELD).asText();
            String response = jsonMessage.get(JSON_RESPONSE_FIELD).asText();

            if (JSON_ACTION_SUBSCRIBE.equals(action) && JSON_RESPONSE_OK.equals(response)) {
                return WebSocketMessageType.SUBSCRIBE_ACK;
            }
        } else if (jsonMessage.has(JSON_TYPE_FIELD)) {
            String type = jsonMessage.get(JSON_TYPE_FIELD).asText();

            if (JSON_TYPE_DIFF_ORDER.equals(type)) {
                return WebSocketMessageType.DIFF_ORDER;
            } else if (JSON_TYPE_KEEP_ALIVE.equals(type)) {
                return WebSocketMessageType.KEEP_ALIVE;
            }
        }

        return WebSocketMessageType.UNKNOWN;
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
