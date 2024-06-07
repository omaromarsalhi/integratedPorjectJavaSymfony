package pidev.javafx.controller.user;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import pidev.javafx.crud.user.ServiceUser;
import pidev.javafx.model.user.User;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.AiVerification;
import pidev.javafx.tools.marketPlace.MyTools;

import java.net.URL;
import java.util.ResourceBundle;


public class MapController implements Initializable {


    @FXML
    private VBox mapInterface;

    @FXML
    private WebView mapWebView;

    @FXML
    private Button testBtn;

    private WebEngine webEngine;

    @FXML
    private AnchorPane loadingPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapInterface.setVisible( false );
        loadingPane.setVisible( true );
        Thread thread = sleepThread();
        ;
        thread.start();

        webEngine = mapWebView.getEngine();
        MapController mapController = new MapController();
        webEngine.load( getClass().getResource( "/mapAssets/map.html" ).toString() );
    }

    @FXML
    public void storeLatLng() {
        JSObject result = (JSObject) webEngine.executeScript( "getLatLng()" );
        if (result.getMember( "lat" ) == null || result.getMember( "lng" ) == null) {
            MyTools.getInstance().getTextNotif().setText( "You need to select a place" );
            MyTools.getInstance().showErrorNotif();
        } else {
            double latitude = (double) result.getMember( "lat" );
            double longitude = (double) result.getMember( "lng" );
            String municipality = (String) result.getMember( "municipality" );
            String municpalityGoverment = (String) result.getMember( "government" );
            String municipalityAddress = (String) result.getMember( "municipalityAddress" );
            String address = (String) result.getMember( "address" );
            User user = UserController.getInstance().getCurrentUser();
            user.setAdresse( address );
            ServiceUser service = new ServiceUser();
            service.modifier( user );
            UserController.setUser( user );
            testThread().start();
        }
    }


    private Thread testThread() {
        Task<String> myTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return AiVerification.analyseEditedData( UserController.getInstance().getCurrentUser().getId() );
            }
        };

        myTask.setOnSucceeded( event -> {
            Platform.runLater( () -> {
                if (myTask.getValue().equals( "success" )) {
                    MyTools.getInstance().getTextNotif().setText( "data has been successfully updated" );
                    MyTools.getInstance().showNotif();
                } else {
                    MyTools.getInstance().getTextNotif().setText( myTask.getValue() );
                    MyTools.getInstance().showErrorNotif();
                }
            } );
        } );
        return new Thread( myTask );
    }


    private Thread sleepThread() {
        Task<Void> myTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep( 4000 );
                return null;
            }
        };

        myTask.setOnSucceeded( e -> {
            mapInterface.setVisible( true );
            loadingPane.setVisible( false );
        } );
        return new Thread( myTask );
    }

}
