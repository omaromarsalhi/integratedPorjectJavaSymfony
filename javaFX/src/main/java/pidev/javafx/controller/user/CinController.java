package pidev.javafx.controller.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import pidev.javafx.tools.marketPlace.MyTools;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CinController implements Initializable {


    @FXML
    private ImageView backCin;

    @FXML
    private ImageView frontCin;

    @FXML
    private VBox idInfoInterface;

    @FXML
    private Button inserBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    public void handelDragOver(DragEvent dragEvent){
        if(dragEvent.getDragboard().hasFiles())
            dragEvent.acceptTransferModes( TransferMode.ANY );
    }

    @FXML
    public void handelDrag(DragEvent dragEvent){
        for (File file : dragEvent.getDragboard().getFiles()){
            frontCin.setImage(new Image("file:"+file.getAbsolutePath(),250,254,true,true ) );
        }
    }


    @FXML
    public void handelDragOver2(DragEvent dragEvent){
        if(dragEvent.getDragboard().hasFiles())
            dragEvent.acceptTransferModes( TransferMode.ANY );
    }

    @FXML
    public void handelDrag2(DragEvent dragEvent){
        for (File file : dragEvent.getDragboard().getFiles()){
            backCin.setImage(new Image("file:"+file.getAbsolutePath(),250,254,true,true ) );
        }
    }

}
