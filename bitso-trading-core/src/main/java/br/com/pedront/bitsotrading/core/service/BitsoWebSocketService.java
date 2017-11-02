package br.com.pedront.bitsotrading.core.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates the WebSocket protocol.
 */
@ClientEndpoint
public class BitsoWebSocketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitsoWebSocketService.class);

    private static final String BOOK = "btc_mxn";

    private static final String BITSO_WSS_URL = "wss://ws.bitso.com";

    private static final String SUBSCRIBE_JSON = "{\"action\":\"subscribe\",\"book\":\"%s\",\"type\":\"%s\"}";

    private Integer tries;

    private String channel;

    private Consumer<String> messageProcessor;

    private Session currentSession;

    private BitsoWebSocketService(String channel) {
        this.channel = channel;
        this.tries = 3;
    }

    /**
     * Subscribe to the channel
     */
    static BitsoWebSocketService subscribe(final String channel) {
        return new BitsoWebSocketService(channel);
    }

    /**
     * Defines the processor for the messages
     */
    BitsoWebSocketService with(Consumer<String> messageProcessor) {
        this.messageProcessor = messageProcessor;

        start();

        return this;
    }

    /**
     * Starts the connection to the WebSocket server.
     */
    private void start() {
        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(this, new URI(BITSO_WSS_URL));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            LOGGER.error("Unexpected error with BitsoWebSocketService, stack trace", e);

            if (tries > 0) {
                tries--;
                start();
            }
        }

    }

    /**
     * Stops the current session
     */
    public void stop() {
        try {
            currentSession.close();
        } catch (IOException e) {
            LOGGER.error("Error closing current session.", e);
        }
    }

    /**
     * This callback gets executed when a new session is open.<br/>
     * The connection to the WebSocket is done in {@link #start()}
     */
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.debug("Connected to Bitso WebSocket server, sessionId={}", session.getId());

        this.currentSession = session;

        try {
            LOGGER.debug("Subscribing to {} channel", channel);

            session.getBasicRemote().sendText(String.format(SUBSCRIBE_JSON, BOOK, channel));
        } catch (IOException e) {
            LOGGER.error("Error sending message do Bitso WebSocket server.", e);

            // Closes the session the execute the onClose callback and try again if possible
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException e1) {
                    LOGGER.error("Error closing Bitso session {} to restart.", session.getId(), e1);
                }
            }
        }
    }

    /**
     * The callback that is called when a new message arrives via WebSocket.<br/>
     * The channel registration is done in {@link #onOpen(Session)}
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.debug("Processing message {} from session {}", message, session.getId());

        messageProcessor.accept(message);
    }

    /**
     * Callback when the session is closed.
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info("Session {} close because of {}", session.getId(), closeReason);

        if (tries > 0) {
            tries--;
            start();
        }
    }
}
