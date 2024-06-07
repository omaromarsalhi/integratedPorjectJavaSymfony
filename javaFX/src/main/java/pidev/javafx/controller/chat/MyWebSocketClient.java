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
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        isConnected = true;
    }

    @Override
    public void onMessage(String message) {
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isConnected = false;
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void send(String text) {
        if (isConnected) {
            System.out.println(text);
            super.send(text);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
