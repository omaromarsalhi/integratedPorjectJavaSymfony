package pidev.javafx.controller.mainWindow;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pidev.javafx.controller.login.GlobalSocketConnection;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.CustomMouseEvent;
import pidev.javafx.tools.marketPlace.EventBus;
import pidev.javafx.tools.marketPlace.MyTools;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private Button accountBtn;
    @FXML
    private Button agrendirBtn;
    @FXML
    private Button blogBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private Button dashbordBtn;
    @FXML
    private HBox header;
    @FXML
    private Button marketplacebtn;
    @FXML
    private Button newsBtn;
    @FXML
    private Button reduireBtn;
    @FXML
    private VBox sideBar;
    @FXML
    private Button stationBtn;
    @FXML
    private Button transportBtn;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private AnchorPane centerContainer;
    @FXML
    private ImageView accountImg;
    @FXML
    private HBox notifHbox;
    @FXML
    private Label imageNotif;
    @FXML
    private Label textNotif;
    @FXML
    private AnchorPane accountDeleted;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        notifHbox.setVisible( false );
        MyTools.getInstance().setImageNotif( imageNotif );
        MyTools.getInstance().setNotifHbox( notifHbox );
        MyTools.getInstance().setTextNotif( textNotif );
        MyTools.getInstance().showAndHideAnimation( MyTools.getInstance().getNotifHbox(), 0, 0 );
        notifHbox.setVisible( true );
        accountDeleted.setVisible( false );

        StackPane dashbord = null;

        try {
            dashbord = FXMLLoader.load( getClass().getResource( "/fxml/user/newAccountOmar.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( dashbord );
        accountImg.setImage( new Image( GlobalVariables.IMAGEPATH4USER+UserController.getInstance().getCurrentUser().getPhotos(),24,24,true,true ) );
        accountBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );

        EventBus.getInstance().subscribe( "setUserImage", this::setUserImage );
        EventBus.getInstance().subscribe( "accountDeleted", event -> {
            accountDeleted.setVisible( true );
            sleepThread().start();
        } );
    }

    public void setUserImage(MouseEvent event){
        accountImg.setImage( new Image( GlobalVariables.IMAGEPATH4USER+UserController.getInstance().getCurrentUser().getPhotos(),24,24,true,true ) );
    }

    private Thread sleepThread() {
        Task<Void> myTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep( 1000*60 );
                return null;
            }
        };

        myTask.setOnSucceeded(event -> {
            Platform.runLater( ()->{
                System.exit( 0 );
            } );
        });

        return new Thread( myTask );
    }



    @FXML
    public void onBlogBtnClicked(ActionEvent event) throws IOException {
        btns();
        GlobalSocketConnection.setBlogInterface( true );
        blogBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );

        centerContainer.getChildren().clear();
        AnchorPane blog = null;
        try {
            blog = FXMLLoader.load( getClass().getResource( "/fxml/blog/blog.fxml" ) );
            EventBus.getInstance().publish( "loadPosts", new CustomMouseEvent<>( "/fxml/blog/post.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( blog );

    }

    @FXML
    public void onAbonnementBtnClicked(ActionEvent event) {
        btns();
        transportBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );
        centerContainer.getChildren().clear();
        StackPane stations = null;
        try {
            stations = FXMLLoader.load( getClass().getResource( "/fxml/Transport/Gui_Abonnement/AbonnementClient.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( stations );

    }


    @FXML
    public void onStationsBtnClicked(ActionEvent event) {
        btns();
        stationBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );
        centerContainer.getChildren().clear();
        AnchorPane stations = null;
        try {
            stations = FXMLLoader.load( getClass().getResource( "/fxml/Transport/Gui_Station/TransportClient.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( stations );
    }


    @FXML
    public void onNewsBtnClicked(ActionEvent event) throws IOException {
        btns();
        newsBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );
        centerContainer.getChildren().clear();
        StackPane stations = null;
        try {
            stations = FXMLLoader.load( getClass().getResource( "/fxml/blog/newsPage.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( stations );
    }


    @FXML
    public void onMarketPlaceBtnClicked(ActionEvent event) {
        btns();
        GlobalSocketConnection.setMarketInterface( true );
        marketplacebtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );
        centerContainer.getChildren().clear();
        StackPane marketplace;
        try {
            marketplace = FXMLLoader.load( getClass().getResource( "/fxml/marketPlace/myMarket.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( marketplace );
    }


    @FXML
    public void onMarketplaceDashbordBtnClicked(ActionEvent event) {
        btns();
        dashbordBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );
        centerContainer.getChildren().clear();
        StackPane dashbord = null;
        try {
            dashbord = FXMLLoader.load( getClass().getResource( "/fxml/userMarketDashbord/userMainDashbord.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( dashbord );
    }

    @FXML
    public void onAccountBtnClicked(ActionEvent event) {
        btns();
        accountBtn.setStyle( " -fx-border-color: #fdc847;" +
                "    -fx-border-width: 0 0 0 2px ;" +
                "    -fx-border-radius: 0;" );
        centerContainer.getChildren().clear();
        StackPane account = null;
        try {
            account = FXMLLoader.load( getClass().getResource( "/fxml/user/newAccountOmar.fxml" ) );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        centerContainer.getChildren().add( account );
    }


    @FXML
    void onCloseBtnClicked() {
//        ChatClient.getInstance().closeConnection(UserController.getInstance().getCurrentUser().getId());
        System.exit( 0 );
    }

    @FXML
    void onReduireBtnClicked(ActionEvent event) {
        Stage currentStage = (Stage) reduireBtn.getScene().getWindow();
        currentStage.setIconified( true );
    }

    @FXML
    void onAgrendirBtnClicked(ActionEvent event) {
        Stage currentStage = (Stage) agrendirBtn.getScene().getWindow();
        boolean etatFenetre = currentStage.isMaximized();
        currentStage.setMaximized( !etatFenetre );
    }

//    @FXML
//    void onBlogClicked(MouseEvent event) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/blog/blog.fxml"));
//        AnchorPane blogPane = loader.load();
//        mainBorderPane.setCenter(blogPane);
//    }

    public void btns(){
        marketplacebtn.setStyle( "");
        transportBtn.setStyle( "" );
        stationBtn.setStyle( "" );
        newsBtn.setStyle( "" );
        blogBtn.setStyle( "" );
        accountBtn.setStyle( "" );
        dashbordBtn.setStyle( "" );
    }

}
