package pidev.javafx.controller.chat;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class MyWebSocketClient extends WebSocketClient {

    private boolean isConnected = false;
    private Consumer<String> messageHandler;

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        System.out.println("waaa");
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to the server");
        isConnected = true;
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
        isConnected = false;
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void send(String text) {
        if (isConnected) {
            super.send(text);
        } else {
            // You might want to handle this case, e.g., queue messages
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
