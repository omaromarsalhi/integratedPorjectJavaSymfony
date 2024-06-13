package pidev.javafx.controller.marketPlace;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pidev.javafx.model.MarketPlace.Bien;
import pidev.javafx.model.MarketPlace.Product;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.CustomMouseEvent;
import pidev.javafx.tools.marketPlace.EventBus;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemController implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Label categoryLable;
    @FXML
    private Label priceLable;
    @FXML
    private ImageView img;
    @FXML
    private HBox itemInfo;
    @FXML
    private AnchorPane anchorPaneItem;
    @FXML
    private VBox bascInfoItems;
    @FXML
    private VBox vboxItem;


    private Bien bien;
    private HBox hbox;
    private int imageIndex;
    private TranslateTransition translateTransition;
    private FadeTransition fadeTransition;
    private KeyFrame animateImagesKeyFrame;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        translateTransition = new TranslateTransition( Duration.seconds( 0.4 ), img );
        fadeTransition = new FadeTransition( Duration.seconds( 0.15 ), img );
    }


    public void setData(Bien bien) {
        this.imageIndex = 1;
        this.bien = bien;
        nameLabel.setText( bien.getName() );
        priceLable.setText( "$" + bien.getPrice() );
//        stateLabel.setText((bien.getState())?"In Stock":"Out Of Stock");
        categoryLable.setText( bien.getCategorie().name() );
        Image image = new Image( GlobalVariables.IMAGEPATH4USER + bien.getImgSource() );
        img.setImage( image );
        hbox = createItemsBtns();
    }


    public void animateImages(Timeline fiveSecondsWonder, boolean state) {
        if (bien.getAllImagesSources().size() > 1 || state) {
            animateImagesKeyFrame = new KeyFrame( Duration.seconds( 5 ), event -> {
                fadeTransition.setDelay( Duration.seconds( 0 ) );
                translateTransition.setByX( -100 );
                fadeTransition.setFromValue( 1 );
                fadeTransition.setToValue( 0 );
                fadeTransition.play();
                translateTransition.play();
                translateTransition.setOnFinished( event1 -> {
                    if (bien.getAllImagesSources().size() == (++imageIndex))
                        imageIndex = 0;
                    img.setImage( new Image( GlobalVariables.IMAGEPATH4USER + bien.getImageSourceByIndex( imageIndex ) ) );
                    translateTransition.setByX( 100 );
                    fadeTransition.setFromValue( 0 );
                    fadeTransition.setDelay( Duration.seconds( 0.2 ) );
                    fadeTransition.setToValue( 1 );
                    translateTransition.play();
                    fadeTransition.play();
                    if (state) {
                        System.out.println( "image index " + imageIndex );
                        System.out.println( "image size " + bien.getAllImagesSources().size() );
                    }
                    translateTransition.setOnFinished( null );
                } );

            } );
            fiveSecondsWonder.getKeyFrames().add(  animateImagesKeyFrame );
            fiveSecondsWonder.setCycleCount( Timeline.INDEFINITE );
            fiveSecondsWonder.play();
        }
    }


    public void showTransitionInfo(Boolean state) {
        if (state) {
            vboxItem.getChildren().remove( hbox );
            vboxItem.getChildren().add( hbox );
            vboxItem.setMargin( hbox, new Insets( 0, 5, 5, 5 ) );
        } else
            vboxItem.getChildren().remove( hbox );
    }


    public HBox createItemsBtns() {
        Button add2Card = new Button();
        Button info = new Button();

        HBox hbox = new HBox();

        info.setPrefWidth( 42 );
        info.setMinHeight( 32 );

        Image img3 = new Image( String.valueOf( getClass().getResource( "/icons/newicons/info (1).png" ) ), 24, 24, true, true );
        info.setGraphic( new ImageView( img3 ) );

        info.setOnMouseClicked( event -> {
            CustomMouseEvent<Product> customMouseEvent = new CustomMouseEvent<>( bien );
            EventBus.getInstance().publish( "showAndSetItemInfo", customMouseEvent );
        } );

        if (UserController.getInstance().getCurrentUser().getId() != bien.getIdUser()) {
            add2Card.setMinHeight( 32 );
            add2Card.setPrefWidth( 42 );
            Image img1 = new Image( String.valueOf( getClass().getResource( "/icons/marketPlace/newBuy.png" ) ), 24, 24, true, true );
            add2Card.setGraphic( new ImageView( img1 ) );
            add2Card.setOnMouseClicked( event -> {
                CustomMouseEvent<Product> customEvent = new CustomMouseEvent<>( bien );
                EventBus.getInstance().publish( "loadCheckout", customEvent );
            } );
            hbox.getChildren().addAll( add2Card, info );
        } else
            hbox.getChildren().add( info );


        hbox.setSpacing( 25 );
        hbox.setAlignment( Pos.CENTER );
        hbox.setId( "itemInfo" );
        hbox.getStylesheets().add( String.valueOf( getClass().getResource( "/style/marketPlace/Buttons.css" ) ) );
        return hbox;
    }

}
