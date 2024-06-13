package pidev.javafx.controller.login;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;
import pidev.javafx.controller.chat.MyWebSocketClient;
import pidev.javafx.controller.userMarketDashbord.FormController;
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
    private static boolean blogInterface;

    public static void initiateConnection() {
        int userID = UserController.getInstance().getCurrentUser().getId();
        URI uri = null;
        try {
            uri = new URI( "ws://" + GlobalVariables.IP + ":8091?userId=" + userID + "&app=java" );
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
        System.out.println( jsonObject );
        if (action.equals( "chat" ) && chatInterface)
            dealWithChat( message );
        else if (action.equals( "chat" ) && !chatInterface) {
            Platform.runLater( () -> {
                if ((Integer) jsonObject.get( "senderId" ) != UserController.getInstance().getCurrentUser().getId()) {
                    MyTools.getInstance().getImageNotif().setGraphic( new ImageView( new Image( "file:src/main/resources/namedIcons/new-message.png", 16, 16, false, false ) ) );
                    MyTools.getInstance().getTextNotif().setText( "New message is received" );
                    MyTools.getInstance().showNotif();
                }
            } );
        }
        if (action.equals( "productEvent" ) && marketInterface)
            dealWithMarket( jsonObject );

        if (action.equals( "postEvent" ) && blogInterface)
            dealWithPost( jsonObject );

        if (action.equals( "aiTermination" )) {
            dealWithSystemMessage( jsonObject );
        }

        if (action.equals( "accountDeletion" )) {
            dealWithSystemMessageForClosingAccount( jsonObject );
        }

    }


    private static void dealWithPost(JSONObject jsonObject) {
        String subAction = (String) jsonObject.get( "subAction" );
        switch (subAction) {
            case "ADD" -> {
                Platform.runLater( () -> {
                    MyTools.getInstance().getTextNotif().setText( "New Post Has Been Added" );
                    MyTools.getInstance().showNotif();
                    var customMouseEvent = new CustomMouseEvent<>( (Integer) jsonObject.getJSONObject( "Data" ).get( "idPost" ) );
                    EventBus.getInstance().publish( "loadPostInRealTime", customMouseEvent );
                } );
            }
            case "UPDATE" -> {
                Platform.runLater( () -> {
                    MyTools.getInstance().getTextNotif().setText( "Post Has Been Updated" );
                    MyTools.getInstance().showNotif();
                    var customMouseEvent = new CustomMouseEvent<>( (Integer) jsonObject.getJSONObject( "Data" ).get( "idPost" ) );
                    EventBus.getInstance().publish( "updatePostInRealTime", customMouseEvent );
                } );
            }
            case "DELETE" -> {
                Platform.runLater( () -> {
                    var customMouseEvent = new CustomMouseEvent<>( Integer.parseInt( (String) jsonObject.getJSONObject( "Data" ).get( "idPost" ) ) );
                    EventBus.getInstance().publish( "deletePostInRealTime", customMouseEvent );
                } );
            }
        }
    }


    private static void dealWithSystemMessageForClosingAccount(JSONObject jsonObject) {
        Platform.runLater( () -> {
            var customMouseEvent = new CustomMouseEvent<>( 10 );
            EventBus.getInstance().publish( "accountDeleted", customMouseEvent );
            MyTools.getInstance().getTextNotif().setText( "Account deleted" );
            MyTools.getInstance().showErrorNotif2( 120 * 1000 );
        } );
    }


    private static void dealWithSystemMessage(JSONObject jsonObject) {
        Platform.runLater( () -> {
            int idProduct = Integer.parseInt( (String) jsonObject.get( "idProduct" ) );
            var customMouseEvent = new CustomMouseEvent<>( idProduct );
            EventBus.getInstance().publish( "refreshProdContainerAi_" + idProduct, customMouseEvent );
            if (((String) jsonObject.get( "message" )).equals( "verified" ))
                send( FormController.createMessage( idProduct,  "ADD" ) );
        } );
    }


    private static void dealWithChat(String message) {
        GlobalVariables.chatController.handleMessage( message );
    }

    private static void dealWithMarket(JSONObject jsonObject) {
        String subAction = (String) jsonObject.get( "subAction" );
        System.out.println(subAction);
        switch (subAction) {
            case "ADD" -> {
                Platform.runLater( () -> {
                    System.out.println("1");
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
        resetAllVarsToFalse();
        GlobalSocketConnection.marketInterface = marketInterface;
    }

    public static void setBlogInterface(boolean blogInterface) {
        resetAllVarsToFalse();
        GlobalSocketConnection.blogInterface = blogInterface;
    }

    private static void resetAllVarsToFalse() {
        GlobalSocketConnection.marketInterface = false;
        GlobalSocketConnection.blogInterface = false;
    }
}
