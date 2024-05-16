package pidev.javafx.controller.user;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pidev.javafx.controller.reclamation.ReclamationBoxController;
import pidev.javafx.crud.reclamation.ServiceReclamation;
import pidev.javafx.crud.user.ServiceUser;
import pidev.javafx.model.reclamation.Reclamation;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.EventBus;
import pidev.javafx.tools.marketPlace.MyTools;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewAccountController implements Initializable {


    @FXML
    private VBox accountBtnBox;

    @FXML
    private Button consultInfoBtn;

    @FXML
    private VBox firstinterface;

    @FXML
    private Button editInfoBtn;

    @FXML
    private Button editLocationBtn;

    @FXML
    private Button insertCinBtn;

    @FXML
    private AnchorPane loadingPage;

    @FXML
    private VBox mainInterface;

    @FXML
    private MenuBar menuBar;

    @FXML
    private HBox toolBar;
    @FXML
    private VBox secondInterface;


    //BlogController blogController;
    //List<Post> posts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadingPage.setVisible( false );
//        HBox reclamations = null;

//        for (Reclamation reclamationData: ServiceReclamation.getInstance().getAll()){
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(getClass().getResource("/fxml/reclamation/reclamation.fxml"));
//            try {
//                reclamations = fxmlLoader.load( );
//                ReclamationBoxController reclamationBoxController=fxmlLoader.getController();
//                reclamationBoxController.setData( reclamationData );
//                HBox finalReclamations = reclamations;
//                reclamationBoxController.getDelete().setOnMouseClicked( event1 -> {
//                    ServiceReclamation.getInstance().supprimer(reclamationData.getIdReclamation());
//                    MyTools.getInstance().deleteAnimation( finalReclamations,reclamsSection );
//                } );
//            } catch (IOException e) {
//                throw new RuntimeException( e );
//            }
//            reclamsSection.getChildren().add(reclamations);
//        }
        setMenueBar();

//        EventBus.getInstance().subscribe( "showReclamation", this::showDetailsReclamation );
        EventBus.getInstance().subscribe( "exitFormUser", this::onExitFormBtnClicked );
//        EventBus.getInstance().subscribe( "showReponse", this::showFormReclamationReponse );
//        EventBus.getInstance().subscribe( "refresh", this::refresh );
        showFormUser( "showDetails" );

        initializeButtons();
    }


    public void setMenueBar() {

        var addReclamation = new MenuItem( "Add Reclamation", new ImageView( new Image( getClass().getResourceAsStream( "/icons/newicons/add.png" ) ) ) );
        var showReclamation = new MenuItem( "Show Reclamation", new ImageView( new Image( getClass().getResourceAsStream( "/icons/newicons/poster.png" ) ) ) );
        addReclamation.setOnAction( event -> showFormReclamation() );
        showReclamation.setOnAction( event -> showReclamations() );
        menuBar.getMenus().get( 0 ).getItems().addAll( addReclamation, showReclamation );


        Menu advancedSettings = new Menu( "Advanced Settings", new ImageView( new Image( getClass().getResourceAsStream( "/icons/marketPlace/more.png" ) ) ) );

        // Create submenu for "File" menu
        Menu updateCredentials = new Menu( "Update Credentials" );
        Menu account = new Menu( "Account" );

        MenuItem updatePassword = new MenuItem( "Update Password" );
        MenuItem updateEmail = new MenuItem( "Update Email" );
        updateCredentials.getItems().addAll( updatePassword, updateEmail );

        MenuItem disconnect = new MenuItem( "Disconnect" );
        MenuItem deleteAccount = new MenuItem( "Delete Account" );
        account.getItems().addAll( disconnect, deleteAccount );

        // Add submenus to "File" menu
        advancedSettings.getItems().addAll( updateCredentials, account );


        disconnect.setOnAction( event -> disconnetcThread().start() );
        deleteAccount.setOnAction( event -> {
            ServiceUser serviceUser = new ServiceUser();
            serviceUser.supprimer( Integer.parseInt( UserController.getInstance().getCurrentUser().getCin() ) );
            disconnetcThread().start();
        } );

        updatePassword.setOnAction( event -> showFormadvancedSettings( "updatePassword" ) );
        updateEmail.setOnAction( event -> showFormadvancedSettings( "updateEmail" ) );

        menuBar.getMenus().get( 1 ).getItems().addAll( advancedSettings );

        var openChat = new MenuItem( "Open Chat", new ImageView( new Image( getClass().getResourceAsStream( "/icons/marketPlace/more.png" ) ) ) );

        menuBar.getMenus().get( 2 ).getItems().addAll( openChat );
        openChat.setOnAction( event -> EventBus.getInstance().publish( "showChat", event ) );

        if (UserController.getInstance().getCurrentUser().getLastname() == null)
            showFormUser( "editDetails" );
        if (UserController.getInstance().getCurrentUser().getPassReseted()) {
            StackPane form;
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation( getClass().getResource( "/fxml/user/advancedSettings.fxml" ) );
            try {
                form = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
            AdvancedSettingsController advancedSettingsController = fxmlLoader.getController();
            advancedSettingsController.setUsageOfThisForm( "updatePassword" );
            advancedSettingsController.setData( UserController.getInstance().getCurrentUser() );
            MyTools.getInstance().showAnimation( form );
        }

    }


    @FXML
    public void initializeButtons() {
        consultInfoBtn.setOnMouseClicked( event -> {
            editInfoBtn.setStyle( "" );
            editLocationBtn.setStyle( "" );
            insertCinBtn.setStyle( "" );
            consultInfoBtn.setStyle( "-fx-border-color: #563f05;" +
                    "    -fx-border-width: 0 0 0 2px ;" +
                    "    -fx-border-radius: 0;" );
            showFormUser( "showDetails" );
        } );
        editInfoBtn.setOnMouseClicked( event -> {
            consultInfoBtn.setStyle( "" );
            editLocationBtn.setStyle( "" );
            insertCinBtn.setStyle( "" );
            editInfoBtn.setStyle( "-fx-border-color: #563f05;" +
                    "    -fx-border-width: 0 0 0 2px ;" +
                    "    -fx-border-radius: 0;" );
            showFormUser( "editDetails" );
        } );
        editLocationBtn.setOnMouseClicked( event -> {
            consultInfoBtn.setStyle( "" );
            editInfoBtn.setStyle( "" );
            insertCinBtn.setStyle( "" );
            editLocationBtn.setStyle( "-fx-border-color: #563f05;" +
                    "    -fx-border-width: 0 0 0 2px ;" +
                    "    -fx-border-radius: 0;" );
            showMap();
        } );
        insertCinBtn.setOnMouseClicked( event -> {
            consultInfoBtn.setStyle( "" );
            editInfoBtn.setStyle( "" );
            editLocationBtn.setStyle( "" );
            insertCinBtn.setStyle( "-fx-border-color: #563f05;" +
                    "    -fx-border-width: 0 0 0 2px ;" +
                    "    -fx-border-radius: 0;" );
            showCin();
        } );
    }


    public void showCin() {
        VBox cin;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/user/insertingCin.fxml" ) );
        try {
            cin = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        mainInterface.getChildren().clear();
        mainInterface.getChildren().add( cin );
        MyTools.getInstance().showAnimation( cin );
    }

    public void showMap() {
        StackPane map;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/user/map.fxml" ) );
        try {
            map = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        mainInterface.getChildren().clear();
        mainInterface.getChildren().add( map );
        MyTools.getInstance().showAnimation( map );
    }


    public void onExitFormBtnClicked(MouseEvent event) {
        firstinterface.setOpacity( 1 );
        loadingPage.setVisible( false );
        loadingPage.getChildren().clear();
        showFormUser( "showDetails" );
    }

    public void showFormUser(String usage) {
        StackPane form = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/user/form.fxml" ) );
        try {
            form = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        UserFormController userFormController = fxmlLoader.getController();
        userFormController.setUsageOfThisForm( usage );
        userFormController.setDataUser( UserController.getInstance().getCurrentUser() );
        mainInterface.setVisible( true );
        mainInterface.getChildren().clear();
        mainInterface.getChildren().add( form );
        MyTools.getInstance().showAnimation( form );
    }

    public void showFormReclamation() {
        StackPane form = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/reclamation/reclamationForm.fxml" ) );
        try {
            form = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        mainInterface.setVisible( true );
        mainInterface.getChildren().clear();
        mainInterface.getChildren().add( form );
        MyTools.getInstance().showAnimation( form );
    }


    public HBox refresh(Reclamation reclamation) {
        HBox reclamations = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/reclamation/reclamation.fxml" ) );
        try {
            reclamations = fxmlLoader.load();
            ReclamationBoxController reclamationBoxController = fxmlLoader.getController();
            reclamationBoxController.setData( reclamation );
            HBox finalReclamations = reclamations;
            reclamationBoxController.getDelete().setOnMouseClicked( event1 -> {
                ServiceReclamation.getInstance().supprimer( reclamation.getIdReclamation() );
                MyTools.getInstance().deleteAnimation( finalReclamations, mainInterface );
            } );
            return reclamations;
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
//        MyTools.getInstance().showAnimation( reclamations );
    }




    public void showReclamations() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxWidth( 600 );
        scrollPane.setPadding( new Insets( 20 ) );


        mainInterface.getChildren().clear();
        mainInterface.getChildren().add( scrollPane );

        VBox vBox = new VBox();
        vBox.setPrefWidth( scrollPane.getPrefWidth() );
        vBox.setSpacing( 20 );
        vBox.setAlignment( Pos.CENTER );
        vBox.setPadding( new Insets( 20 ) );

        var reclamations = ServiceReclamation.getInstance().getAllbyid( UserController.getInstance().getCurrentUser().getId() );

        for (Reclamation reclamation : reclamations) {
            vBox.getChildren().add( refresh( reclamation ) );
        }

        scrollPane.setContent( vBox );
    }

//    public void showDetailsReclamation(MouseEvent event) {
//        StackPane form = null;
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation( getClass().getResource( "/fxml/reclamation/reclamationFormShow.fxml" ) );
//        try {
//            form = fxmlLoader.load();
//        } catch (IOException e) {
//            throw new RuntimeException( e );
//        }
////        ReclamationFormController reclamationBoxController=fxmlLoader.getController();
////        StackPane finalForm = form;
//        firstinterface.setOpacity( 0.4 );
//        secondInterface.setVisible( true );
//        secondInterface.getChildren().add( form );
//        MyTools.getInstance().showAnimation( form );
//    }


//    public void showFormReclamationReponse(MouseEvent event) {
//        StackPane form = null;
//        secondInterface.getChildren().clear();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation( getClass().getResource( "/fxml/reclamation/reclamationReponse.fxml" ) );
//        try {
//            form = fxmlLoader.load();
//        } catch (IOException e) {
//            throw new RuntimeException( e );
//        }
//
//        firstinterface.setOpacity( 0.4 );
//        secondInterface.setVisible( true );
//        secondInterface.getChildren().add( form );
//        MyTools.getInstance().showAnimation( form );
//    }


    public void showFormadvancedSettings(String usage) {
        StackPane form = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/user/advancedSettings.fxml" ) );
        try {
            form = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        AdvancedSettingsController advancedSettingsController = fxmlLoader.getController();
        advancedSettingsController.setUsageOfThisForm( usage );
        advancedSettingsController.setData( UserController.getInstance().getCurrentUser() );
        mainInterface.setVisible( true );
        mainInterface.getChildren().clear();
        mainInterface.getChildren().add( form );
        MyTools.getInstance().showAnimation( form );
    }


    public void showDetailsReclamation(MouseEvent event) {
        StackPane form = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/reclamation/reclamationFormShow.fxml" ) );
        try {
            form = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
//        ReclamationFormController reclamationBoxController=fxmlLoader.getController();
//        StackPane finalForm = form;
        firstinterface.setOpacity( 0.4 );
        secondInterface.setVisible( true );
        secondInterface.getChildren().add( form );
        MyTools.getInstance().showAnimation( form );
    }


    private Thread disconnetcThread() {
        Task<Void> myTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                firstinterface.setOpacity( 0.6 );
                loadingPage.setVisible( true );
                Thread.sleep( 3000 );
                return null;
            }
        };
        myTask.setOnSucceeded( e -> {
            Platform.runLater( () -> {
//        ChatClient.getInstance().closeConnection(UserController.getInstance().getCurrentUser().getId());
                Stage stage = null;
//                stage=(Stage) blogSection.getScene().getWindow();
                stage.close();
            } );
        } );
        return new Thread( myTask );
    }


    public void showFormReclamationReponse(MouseEvent event) {
        StackPane form = null;
        secondInterface.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation( getClass().getResource( "/fxml/reclamation/reclamationReponse.fxml" ) );
        try {
            form = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        firstinterface.setOpacity( 0.4 );
        secondInterface.setVisible( true );
        secondInterface.getChildren().add( form );
        MyTools.getInstance().showAnimation( form );
    }


}