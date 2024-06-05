package pidev.javafx.controller.chat;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketApp extends Application {

    private MyWebSocketClient webSocketClient;
    private Label messageLabel;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        // Connect to the WebSocket server
        int userID = 195;
        URI uri = new URI("ws://localhost:8091?userId=" + userID);
        webSocketClient = new MyWebSocketClient(uri);

        // Set the message handler
        webSocketClient.setMessageHandler(this::handleMessage);

        webSocketClient.connect();

        // Create a button to send a message
        Button sendButton = new Button("Send Message");
        sendButton.setOnAction(event -> {
            String message = "{\"action\":\"chat\", \"senderId\":" + userID + ", \"recipientId\":114, \"message\":\"Hello from JavaFX\"}";
            webSocketClient.send(message);
        });

        // Label to display received messages
        messageLabel = new Label("No messages received yet.");

        VBox root = new VBox(sendButton, messageLabel);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("WebSocket Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleMessage(String message) {
        // Update the UI on the JavaFX Application Thread
        Platform.runLater(() -> messageLabel.setText("Received: " + message));
    }

    @Override
    public void stop() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
