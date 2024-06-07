package pidev.javafx.controller.login;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;
import pidev.javafx.controller.chat.MyWebSocketClient;
import pidev.javafx.controller.userMarketDashbord.FormController;
import pidev.javafx.crud.marketplace.CrudBien;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.CustomMouseEvent;
import pidev.javafx.tools.marketPlace.EventBus;
import pidev.javafx.tools.marketPlace.MyTools;

import java.net.URI;
import java.net.URISyntaxException;

public class GlobalSocketConnection {

    private static MyWebSocketClient webSocketClient;
    private static boolean chatInterface;
    private static boolean marketInterface;

    public static void initiateConnection() {
        int userID = UserController.getInstance().getCurrentUser().getId();
        URI uri = null;
        try {
            uri = new URI( "ws://localhost:8091?userId=" + userID );
        } catch (URISyntaxException e) {
            throw new RuntimeException( e );
        }
        webSocketClient = new MyWebSocketClient( uri );
        webSocketClient.setMessageHandler( GlobalSocketConnection::handleMessage );
        webSocketClient.connect();
    }

    private static void handleMessage(String message) {
        JSONObject jsonObject = new JSONObject( message );
        String action = (String) jsonObject.get( "action" );
        if (action.equals( "chat" ) && chatInterface)
            dealWithChat( message );
        else if (action.equals( "chat" ) && !chatInterface) {
            Platform.runLater( () -> {
                MyTools.getInstance().getImageNotif().setGraphic( new ImageView( new Image( "file:src/main/resources/namedIcons/new-message.png", 16, 16, false, false ) ) );
                MyTools.getInstance().getTextNotif().setText( "New message is received" );
                MyTools.getInstance().showNotif();
            } );
        }
        if (action.equals( "productEvent" ) && marketInterface)
            dealWithMarket( jsonObject );

        if (action.equals( "aiTermination" )) {
            dealWithSystemMessage( jsonObject );
        }

    }


    private static void dealWithSystemMessage(JSONObject jsonObject) {
        Platform.runLater( () -> {
            int idProduct = Integer.parseInt( (String) jsonObject.get( "idProduct" ) );
            var customMouseEvent = new CustomMouseEvent<>( idProduct );
            EventBus.getInstance().publish( "refreshProdContainerAi_"+idProduct, customMouseEvent );
        } );
    }


    private static void dealWithChat(String message) {
        GlobalVariables.chatController.handleMessage( message );
    }

    private static void dealWithMarket(JSONObject jsonObject) {
        String subAction = (String) jsonObject.get( "subAction" );
        switch (subAction) {
            case "ADD" -> {
                Platform.runLater( () -> {
                    MyTools.getInstance().getTextNotif().setText( "New Product Has Been Added" );
                    MyTools.getInstance().showNotif();
                    var customMouseEvent = new CustomMouseEvent<>( (Integer) jsonObject.getJSONObject( "Data" ).get( "idProduct" ) );
                    EventBus.getInstance().publish( "addProductInRealTime", customMouseEvent );
                } );
            }
            case "UPDATE" -> {
                Platform.runLater( () -> {
                    var customMouseEvent = new CustomMouseEvent<>( (Integer) jsonObject.getJSONObject( "Data" ).get( "idProduct" ) );
                    EventBus.getInstance().publish( "updateProductInRealTime_" + customMouseEvent.getEventData(), customMouseEvent );
                } );
            }
            case "DELETE" -> {
                Platform.runLater( () -> {
                    var customMouseEvent = new CustomMouseEvent<>( Integer.parseInt( (String) jsonObject.getJSONObject( "Data" ).get( "idProduct" ) ) );
                    EventBus.getInstance().publish( "deleteProductInRealTime", customMouseEvent );
                } );
            }
        }

    }

    public static void send(String message) {
        webSocketClient.send( message );
    }


    public static void setChatInterface(boolean chatInterface) {
        GlobalSocketConnection.chatInterface = chatInterface;
    }


    public static void setMarketInterface(boolean marketInterface) {
        GlobalSocketConnection.marketInterface = marketInterface;
    }

    private static void resetAllVarsToFalse() {
        GlobalSocketConnection.marketInterface = false;
    }
}
