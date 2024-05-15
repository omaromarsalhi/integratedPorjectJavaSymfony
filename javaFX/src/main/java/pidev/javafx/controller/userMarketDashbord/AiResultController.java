package pidev.javafx.controller.userMarketDashbord;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class AiResultController implements Initializable {


    @FXML
    private TextArea categoryDesc;

    @FXML
    private VBox categorySection;

    @FXML
    private VBox formBox;

    @FXML
    private Label imageNumber;

    @FXML
    private TextArea titleDesc;

    @FXML
    private VBox titleSection;

    @FXML
    private HBox detailsBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.detailsBox.setVisible(false);
    }

    public void setData(JSONObject jsonObject) {
        if ((boolean) jsonObject.get( "doesItExist" )) {
            var data = jsonObject.getJSONArray( "data" );
//            for (int i = 0; i < data.length(); i++) {
            for (int i = 0; i < 3; i++) {

//                String titleData = (String) data.getJSONObject( i ).get( "titleData" );
//                String categoryData = (String) data.getJSONObject( i ).get( "categoryData" );
//                titleDesc.setText( titleData );
//                categoryDesc.setText( categoryData );


                HBox detailsBox = new HBox();
//                detailsBox.setId( STR."detailsBox_\{i}" );
                detailsBox.getChildren().addAll( this.detailsBox.getChildren() );
                detailsBox.setStyle( "-fx-background-color: blue;" );
                detailsBox.setMinSize( 100, 400 );
                formBox.getChildren().add( detailsBox );
            }
        }
    }


}
































































