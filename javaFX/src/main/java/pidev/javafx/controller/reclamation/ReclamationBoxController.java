package pidev.javafx.controller.reclamation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import pidev.javafx.model.reclamation.Reclamation;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.marketPlace.CustomMouseEvent;
import pidev.javafx.tools.marketPlace.EventBus;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ReclamationBoxController {

    @FXML
    private Label date;
    @FXML
    private ImageView img;
    @FXML
    private Label subject;
    @FXML
    private Button Popup;


    @FXML
    private Button delete;


private Reclamation rec;
    public void setData(Reclamation reclamation){
        rec=reclamation;
        img.setImage( new Image( GlobalVariables.IMAGEPATH+reclamation.getImagePath(),70,70,true,true) );
        subject.setText( reclamation.getSubject() );
        date.setText( reclamation.getDate() );
    }

    public void showDetailsReclamation(MouseEvent event) {
        // Publish events as before
        EventBus.getInstance().publish("showReclamation", event);
        System.out.println(rec);
        EventBus.getInstance().publish("senddata", new CustomMouseEvent<Reclamation>(rec));

        // Retrieve details from the reclamation object
        String details = getReclamationDetails(rec);

        // Create and show the alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reclamation Details");
        alert.setContentText(details);

        alert.showAndWait();
    }

    // Helper method to construct the details string
    private String getReclamationDetails(Reclamation rec) {
        StringBuilder details = new StringBuilder();
        details.append("Private Key: ").append(rec.getPrivateKey()).append("\n");
        details.append("Description: ").append(rec.getDescription()).append("\n");
        details.append("Date: ").append(rec.getDate()).append("\n");
        // Add more fields as necessary

        return details.toString();
    }



    public Button getDelete() {
        return delete;
    }


    public Button getPopup() {
        return Popup;
    }
}
