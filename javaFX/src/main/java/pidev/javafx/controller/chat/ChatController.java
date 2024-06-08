package pidev.javafx.controller.chat;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.json.JSONObject;
import pidev.javafx.controller.login.GlobalSocketConnection;
import pidev.javafx.crud.marketplace.CrudChat;
import pidev.javafx.crud.user.ServiceUser;
import pidev.javafx.model.chat.Chat;
import pidev.javafx.model.user.User;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.ResultHolder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;

import static javafx.scene.layout.HBox.setMargin;

public class ChatController implements Initializable {

    @FXML
    private Button addImageBtn;
    @FXML
    private AnchorPane ancherPaneOfgridPane;
    @FXML
    private AnchorPane ancherPaneOfgridPaneMain;
    @FXML
    private VBox chat;
    @FXML
    private VBox chatContainer;
    @FXML
    private Button clearMsgBtn;
    @FXML
    private Button emojie00;
    @FXML
    private Button emojie01;
    @FXML
    private Button emojie02;
    @FXML
    private Button emojie10;
    @FXML
    private Button emojie11;
    @FXML
    private Button emojie12;
    @FXML
    private Button emojie20;
    @FXML
    private Button emojie21;
    @FXML
    private Button emojie22;
    @FXML
    private VBox itemDeatails;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button moreOptions;
    @FXML
    private Button sendMsgBtn;
    @FXML
    private Button searchBtn;
    @FXML
    private HBox searchHbox;
    @FXML
    private TextField searchTextField;
    @FXML
    private VBox usersBox;
    @FXML
    private ScrollPane scroll;
    @FXML
    private ImageView exitBtn;
    @FXML
    private ImageView userImage;
    @FXML
    private Label userName;
    @FXML
    private ImageView connState;
    @FXML
    private HBox container;


    private boolean amIAReciver;
    private Timer animTimer;
    private User reciver;
    private ResultHolder resultHolder = new ResultHolder();
    private boolean isConnected;
    private MyWebSocketClient webSocketClient;
    private int userID;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlobalSocketConnection.setChatInterface( true );
        userID = UserController.getInstance().getCurrentUser().getId();
        ancherPaneOfgridPaneMain.setVisible( false );
        amIAReciver = false;
        resultHolder.setResult( "false" );
        searchBtn.setStyle( "-fx-border-radius: 0 10 10 0;" +
                "-fx-border-color: transparent ;" );

        searchTextField.setStyle( "-fx-border-radius: 10 0 0 10;" +
                "-fx-border-color: transparent ;" );


        chatContainer.heightProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scroll.setVvalue( (Double) newValue );
            }
        } );
    }



    public void handleMessage(String message) {
        Platform.runLater( () -> {
            JSONObject jsonObject = new JSONObject( message );
            int othersenderId = (Integer) jsonObject.get( "senderId" );
            if(othersenderId==UserController.getInstance().getCurrentUser().getId()){
                chatContainer.getChildren().add( createTextChatBox( (String) jsonObject.get( "message" ), false, "" ) );
            }else {
                HBox hBox = (HBox) usersBox.lookup( "#" + othersenderId );
                if (hBox == null) {
                    var crud = new ServiceUser();
                    hBox = createUserForAdd( crud.getUserById( othersenderId ) );
                    usersBox.getChildren().addFirst( hBox );
                }
                if (othersenderId != reciver.getId()) {
                    Label label = (Label) hBox.getChildren().get( 2 );
                    label.setText( "+" + (CrudChat.getInstance().count( othersenderId ) + 1) );
                } else {
                    String message2 = (String) jsonObject.get( "message" );
                    chatContainer.getChildren().add( createTextChatBox( message2, true, "" ) );
                }
            }
        } );
    }


    @FXML
    public void onSendMsgBtnClicked() {
        String messageText = messageTextField.getText();
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                .add( "action", "chat" )
                .add( "senderId", userID )
                .add( "recipientId", reciver.getId() )
                .add( "message", messageText );

        JsonObject jsonMessage = jsonObjectBuilder.build();
        String jsonString = jsonMessage.toString();
        GlobalSocketConnection.send( jsonString );
        chatContainer.getChildren().add( createTextChatBox( messageTextField.getText(), false, "" ) );
        CrudChat.getInstance().addItem( new Chat( 0, UserController.getInstance().getCurrentUser(), reciver, messageTextField.getText(), isConnected, null ) );
        resultHolder.setResult( null );
        scroll.setVvalue( 1 );
        messageTextField.clear();
    }


    public HBox createUserForAdd(User user) {
        HBox hBox = new HBox();
        hBox.setId( String.valueOf( user.getId() ) );
        hBox.setMinHeight( 40 );


        ImageView userImage = new ImageView( new Image( GlobalVariables.IMAGEPATH4USER + user.getPhotos(), 32, 32, true, true ) );
        Label userName = new Label( user.getFirstname() + " " + user.getLastname() );
        userName.setFont( Font.font( "System", FontWeight.LIGHT, FontPosture.ITALIC, 14 ) );
        userName.setMinWidth( 80 );


        hBox.setStyle( "-fx-background-color:  #ced4da;" +
                "-fx-background-radius : 10;" );
        hBox.setAlignment( Pos.CENTER_LEFT );
        hBox.setSpacing( 6 );
        hBox.setPadding( new Insets( 0, 0, 0, 10 ) );


        hBox.getChildren().addAll( userImage, userName, createNotif() );


        hBox.hoverProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue)
                hBox.setStyle( "-fx-background-color: #fdc847;" +
                        "-fx-background-radius : 10;" );
            else
                hBox.setStyle( "-fx-background-color: #ced4da;" +
                        "-fx-background-radius : 10;" );
        } );

        hBox.setOnMouseClicked( event -> {
            if (CrudChat.getInstance().count( user.getId() ) > 0 && hBox.getChildren().size() > 2) {
                Label label = (Label) hBox.getChildren().get( 2 );
                label.setText( "" );
            }
            setSelectedUserData( user );
        } );
        return hBox;
    }


    public void loadUsers(ObservableList<User> users) {
        for (int i = 0; i < users.size(); i++)
            if (users.get( i ).getId() != UserController.getInstance().getCurrentUser().getId())
                usersBox.getChildren().add( createUserForAdd( users.get( i ) ) );
        setSelectedUserData( users.get( 0 ) );
    }

    public void setUserToChatWith(User user) {
        if (user.getId() != UserController.getInstance().getCurrentUser().getId())
            setSelectedUserData( user );
    }

    public Label createNotif() {
        Label notif = new Label();
        notif.setFont( Font.font( "System", FontWeight.BOLD, FontPosture.ITALIC, 14 ) );
        notif.setStyle( "-fx-font-size: 16;" );
        notif.setAlignment( Pos.CENTER );
        notif.setMinSize( 28, 24 );
        notif.setStyle( "-fx-text-fill: red;" +
                "-fx-border-radius: 50;" +
                "-fx-background-radius: 50" );
        return notif;
    }


    public void setSelectedUserData(User user) {
        this.reciver = user;
        userImage.setImage( new Image( GlobalVariables.IMAGEPATH4USER + user.getPhotos(), 46, 46, true, true ) );
        userName.setText( user.getFirstname().toUpperCase() + " " + user.getLastname().toUpperCase() );
        userName.setMinHeight( Region.USE_PREF_SIZE );
        connState.setImage( new Image( "file:src/main/resources/namedIcons/button.png", 12, 12, true, true ) );
        rettriveConversation();
    }


    public void rettriveConversation() {
        LocalDate date = LocalDate.of( 0, 1, 1 );
        for (Chat chat : CrudChat.getInstance().selectItems( reciver.getId() )) {
            chatContainer.getChildren().add( createTextChatBox( chat.getMessage(), UserController.getInstance().getCurrentUser().getId() != chat.getUserSender().getId(), chat.getTimestamp().toLocalDateTime().format( DateTimeFormatter.ofPattern( "hh:mm" ) ) ) );
            LocalDate firstDate = LocalDate.parse( chat.getTimestamp().toLocalDateTime().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ) ); // Your first timestamp
            if (firstDate.isBefore( LocalDate.now() ) && firstDate.isAfter( date )) {
                Label label = new Label( firstDate.toString() );
                label.setMinHeight( 20 );
                chatContainer.getChildren().add( label );
                date = firstDate;
            }
        }
        CrudChat.getInstance().updateMsgState( reciver.getId() );
    }


    public HBox createTextChatBox(String text, boolean changeOrder, String time) {
        HBox msgBox = new HBox();

        msgBox.setSpacing( 6 );


        Label msgLabel = new Label();
        msgLabel.setStyle( "-fx-background-color:  #D9D9D9;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" );
        msgLabel.setMinHeight( 40 );
        msgLabel.setText( text );
        msgLabel.setMinHeight( Region.USE_PREF_SIZE );
        msgLabel.setWrapText( true );
        msgLabel.setPadding( new Insets( 6 ) );

        Label timeLabel = new Label();
        timeLabel.setStyle( "-fx-font-size: 10;" );
        timeLabel.setMinSize( 25, 15 );

        timeLabel.setText( (time.isEmpty()) ? LocalTime.now().format( DateTimeFormatter.ofPattern( "hh:mm" ) ) : time );
        ImageView usernIcon;
        if (changeOrder)
            usernIcon = new ImageView( new Image( GlobalVariables.IMAGEPATH4USER + UserController.getInstance().getCurrentUser().getPhotos(), 16, 16, false, false ) );
        else
            usernIcon = new ImageView( new Image( GlobalVariables.IMAGEPATH4USER + reciver.getPhotos(), 16, 16, false, false ) );

        setMargin( usernIcon, new Insets( 0, 0, 2, 0 ) );

        if (!changeOrder) {
            msgBox.setAlignment( Pos.BOTTOM_RIGHT );
            msgBox.getChildren().addAll( msgLabel, timeLabel, usernIcon );
        } else {
            msgBox.setAlignment( Pos.BOTTOM_LEFT );
            msgBox.getChildren().addAll( usernIcon, timeLabel, msgLabel );
        }
        return msgBox;
    }


    @FXML
    public void onMoreOptionsClick() {
        FadeTransition fadeTransition = new FadeTransition( Duration.seconds( 0.4 ), ancherPaneOfgridPaneMain );
        if (ancherPaneOfgridPaneMain.isVisible()) {
            fadeTransition.setFromValue( 1 );
            fadeTransition.setToValue( 0 );
            emojie00.setOnMouseClicked( null );
            emojie01.setOnMouseClicked( null );
            emojie10.setOnMouseClicked( null );
            emojie02.setOnMouseClicked( null );
            emojie20.setOnMouseClicked( null );
            emojie21.setOnMouseClicked( null );
            emojie22.setOnMouseClicked( null );
            emojie12.setOnMouseClicked( null );
            emojie11.setOnMouseClicked( null );
            fadeTransition.setOnFinished( event -> ancherPaneOfgridPaneMain.setVisible( false ) );
            fadeTransition.play();
        } else if (!ancherPaneOfgridPaneMain.isVisible()) {
            ancherPaneOfgridPaneMain.setVisible( true );
            fadeTransition.setFromValue( 0 );
            fadeTransition.setToValue( 1 );
//            emojie00.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/angry.png" ,amIAReciver));
//                scroll.setVvalue( 1 );
//            });
//            emojie01.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/in-love.png",amIAReciver ));
//                scroll.setVvalue( 1 );
//            } );
//            emojie10.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/angry.png"  ,amIAReciver));
//                scroll.setVvalue( 1 );
//            });
//            emojie02.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/party.png"  ,amIAReciver));
//                scroll.setVvalue( 1 );
//            });
//            emojie20.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/smiling.png" ,amIAReciver ));
//                scroll.setVvalue( 1 );
//            });
//            emojie21.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/nerd.png"  ,amIAReciver));
//                scroll.setVvalue( 1 );
//            });
//            emojie22.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/surprised.png" ,amIAReciver ));
//                scroll.setVvalue( 1 );
//            });
//            emojie12.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/sad.png" ,amIAReciver ));
//                scroll.setVvalue( 1 );
//            });
//            emojie11.setOnMouseClicked( event -> {
//                chatContainer.getChildren().add(createImageChatBox( "file:src/main/resources/emojies/laugh.png" ,amIAReciver ));
//                scroll.setVvalue( 1 );
//            });
            fadeTransition.play();
        }

    }


    public ImageView getExitBtn() {
        return exitBtn;
    }


    public HBox getContainer() {
        return container;
    }


}

