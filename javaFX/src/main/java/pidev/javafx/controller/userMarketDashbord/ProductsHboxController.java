package pidev.javafx.controller.userMarketDashbord;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import pidev.javafx.crud.marketplace.CrudBien;
import pidev.javafx.model.MarketPlace.Bien;
import pidev.javafx.model.MarketPlace.Product;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.marketPlace.*;
import pidev.javafx.tools.marketPlace.AiVerification;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductsHboxController implements Initializable {
    @FXML
    private HBox actions;
    @FXML
    private Label category;
    @FXML
    private Label creationDate;
    @FXML
    private Label descreption;
    @FXML
    private ImageView image;
    @FXML
    private Label name;
    @FXML
    private Label price;
    @FXML
    private Label quantity;
    @FXML
    private Label state;
    @FXML
    private Label id;
    @FXML
    private ImageView stateImage;
    @FXML
    private Button aiResultBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deleteBtn;


    private Bien bien;
    private Popup popup = new Popup();
    private Timeline fiveSecondsWonder = new Timeline();


    private JSONObject jsonObject;

    private String verificationState = "unverified";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aiResultBtn.setVisible( false );
    }


    public void setData(Product product) {
        if (product instanceof Bien bien) {
            System.setProperty( "javax.net.ssl.trustAll", "true" );
            this.bien = bien;
            id.setText( Integer.toString( bien.getId() ) );
            id.setVisible( false );
//            image.setImage( new Image( GlobalVariables.IMAGEPATH + bien.getImageSourceByIndex( 0 ),40,40,false,false ) );
//            image.setImage( new Image( "file:src/main/resources/usersImg/0a82a778-9301-4cd1-a535-63a1df7e18c7.png",40,40,false,false ) );
            image.setImage( new Image( GlobalVariables.IMAGEPATH4USER+ bien.getImageSourceByIndex( 0 ), 40, 40, false, false ) );
            name.setText( bien.getName() );
            descreption.setText( bien.getDescreption() );
            price.setText( bien.getPrice().toString() );
            quantity.setText( bien.getQuantity().toString() );
            state.setText( bien.getState().toString() );
            creationDate.setText( bien.getTimestamp().toString() );
            category.setText( bien.getCategorie().toString() );
            if (product.getState().equals( "verified" )) {
                stateImage.setImage( new Image( "file:src/main/resources/icons/marketPlace/approve24C.png", 24, 24, true, true ) );
                verificationState = "verified";
                aiResultBtn.setVisible( false );
            } else if (testStateAiVerification()) {
                verificationState = "half-verified";
                stateImage.setImage( new Image( "file:src/main/resources/icons/marketPlace/mark.png" ) );
                stateImage.setFitWidth( 16 ); // Set desired width
                stateImage.setFitHeight( 16 );
                aiResultBtn.setVisible( true );
            }
        }
    }


    @FXML
    private void onMouseEntered(MouseEvent event) {

        final VBox mainContainer = new VBox();
        mainContainer.setMinSize( 100, 100 );
        mainContainer.setAlignment( Pos.CENTER );
        mainContainer.setStyle( "-fx-background-color: #5f7470; -fx-border-color: #5f7470; -fx-border-width: 1px; -fx-padding: 5px; -fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" );
        final ImageView imageView = new ImageView();
        imageView.setImage( new Image( GlobalVariables.IMAGEPATH4USER+ bien.getImageSourceByIndex( 0 ), 90, 90, true, true ) );
        mainContainer.getChildren().add( imageView );

        if (bien.getAllImagesSources().size() > 1) {
            AtomicInteger indexOfImage = new AtomicInteger( 1 );
            fiveSecondsWonder.getKeyFrames().add( new KeyFrame( Duration.seconds( 0.8 ), event1 -> {
                imageView.setImage( new Image( GlobalVariables.IMAGEPATH4USER+  bien.getImageSourceByIndex( indexOfImage.get() ), 90, 90, true, true ) );
                indexOfImage.getAndIncrement();
                if (indexOfImage.get() >= bien.getAllImagesSources().size())
                    indexOfImage.set( 0 );
            } ) );
            fiveSecondsWonder.setCycleCount( Timeline.INDEFINITE );
            fiveSecondsWonder.play();
        }

        popup.getContent().clear();
        popup.getContent().add( mainContainer );
        popup.show( Stage.getWindows().get( 0 ), event.getScreenX() + 20, event.getScreenY() - 30 );
    }


    @FXML
    private void onMouseExited(MouseEvent event) {
        fiveSecondsWonder.stop();
        popup.hide();
    }


    @FXML
    private void onStateMouseEntered(MouseEvent event) {
        Label mainContainer;
        if (verificationState.equals( "unverified" )) {
            mainContainer = new Label( "WE ARE VERIFYING THIS PRODUCT SO THAT IF IT IS COMPATIBLE OTHERWISE IT WILL BE DELETED" );
        } else if (verificationState.equals( "half-verified" )) {
            mainContainer = new Label( "CLICK ON THIS Ai BUTTON FOR FURTHER DETAILS" );
        } else
            mainContainer = new Label( "Verified" );
        mainContainer.setMaxWidth( 200 );
        mainContainer.setWrapText( true );
        mainContainer.setAlignment( Pos.CENTER );
        mainContainer.setStyle( "-fx-background-color: #5f7470; -fx-border-color: #5f7470; -fx-border-width: 1px; -fx-padding: 5px; -fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" );

        popup.getContent().clear();
        popup.getContent().add( mainContainer );
        popup.show( Stage.getWindows().get( 0 ), event.getScreenX() + 20, event.getScreenY() - 30 );
    }


    @FXML
    private void onStateMouseExited(MouseEvent event) {
        popup.hide();
    }


    private boolean testStateAiVerification() {
        var ai = new AiVerification();
        var result = ai.HttpAiResultState( Integer.parseInt( id.getText() ) );
        jsonObject = new JSONObject( result );
        return (boolean) jsonObject.get( "doesItExist" );
    }

    public String getVerificationState() {
        return verificationState;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }



}
