package pidev.javafx.controller.userMarketDashbord;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import pidev.javafx.tools.marketPlace.EventBus;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AiResultController implements Initializable {


    @FXML
    private Button exitBtn;

    @FXML
    private VBox formBox;


    @FXML
    private Button restartAiVerifBtn;

    @FXML
    private ScrollPane scrollobalArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exitBtn.setOnMouseClicked( event -> EventBus.getInstance().publish( "onExitForm", event ) );
    }

    public void setData(JSONObject jsonObject) {
        if ((boolean) jsonObject.get( "doesItExist" )) {
            var data = jsonObject.getJSONArray( "data" );
            for (int i = 0; i < data.length(); i++) {
                if (!(boolean) data.getJSONObject( i ).get( "title" ) && !(boolean) data.getJSONObject( i ).get( "category" )) {

                    HBox detailsBox = deepCopy();
                    TextArea lookupTitle = (TextArea) detailsBox.lookup( "#titleDesc" );
                    TextArea lookupCategory = (TextArea) detailsBox.lookup( "#categoryDesc" );

                    if (!(boolean) data.getJSONObject( i ).get( "title" )) {
                        String titleData = (String) data.getJSONObject( i ).get( "titleData" );
                        lookupTitle.setText( titleData );
                    } else
                        detailsBox.lookup( "#titleSection" ).setVisible( false );


                    if (!(boolean) data.getJSONObject( i ).get( "category" )) {
                        String categoryData = (String) data.getJSONObject( i ).get( "categoryData" );
                        lookupCategory.setText( categoryData );
                    } else
                        detailsBox.lookup( "#categorySection" ).setVisible( false );


                    VBox vBox = (VBox) detailsBox.lookup( "#titleCat" );
                    vBox.setAlignment( Pos.CENTER );

                    Label lookupImageNumber = (Label) detailsBox.lookup( "#imageNumber" );
                    lookupImageNumber.setText( "Image " + (i + 1) + ":" );
                    formBox.getChildren().add( detailsBox );

                }
            }
        }
    }


    private HBox deepCopy() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation( getClass().getResource( "/fxml/userMarketDashbord/detailsBox.fxml" ) );
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    public Button getRestartAiVerifBtn() {
        return restartAiVerifBtn;
    }

}
































































