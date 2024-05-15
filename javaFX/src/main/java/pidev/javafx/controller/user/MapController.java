package pidev.javafx.controller.user;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import pidev.javafx.tools.marketPlace.CustomMouseEvent;
import pidev.javafx.tools.marketPlace.EventBus;
import pidev.javafx.tools.marketPlace.MyTools;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;


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
        Thread thread = sleepThread();;
        thread.start();

        webEngine = mapWebView.getEngine();
        MapController mapController = new MapController();
        webEngine.load( getClass().getResource( "/mapAssets/map.html" ).toString() );
    }

    @FXML
    public void storeLatLng() {
        JSObject result = (JSObject) webEngine.executeScript("getLatLng()");
        if(result.getMember("lat")==null || result.getMember("lng")==null){
            MyTools.getInstance().getTextNotif().setText( "You need to select a place" );
            MyTools.getInstance().showErrorNotif();
        }else {
            double latitude = (double) result.getMember( "lat" );
            double longitude = (double) result.getMember( "lng" );
            String municipality = (String) result.getMember( "municipality" );
            String municpalityGoverment = (String) result.getMember( "government" );
            String municipalityAddress = (String) result.getMember( "municipalityAddress" );
//            System.out.println( STR. "Received lat: \{latitude}, lng: \{longitude}" );
//            System.out.println( STR."Received lat: \{municipality}, lng: \{municpalityGoverment}" );
//            System.out.println( STR."Received lat: \{municipalityAddress}" );
        }
    }

    private Thread sleepThread() {
        Task<Void> myTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(4000);
                return null;
            }
        };

        myTask.setOnSucceeded(e -> {
            mapInterface.setVisible( true );
            loadingPane.setVisible( false );
        });
        return new Thread(myTask);
    }

}
