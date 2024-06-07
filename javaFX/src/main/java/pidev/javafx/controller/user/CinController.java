package pidev.javafx.controller.user;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.AiVerification;
import pidev.javafx.tools.marketPlace.MyTools;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    @FXML
    private AnchorPane loadingPage;

    @FXML
    private StackPane test;

    @FXML
    private ImageView infoLabel;

    private File[] files;
    private Popup popup;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        popup = new Popup();

        files = new File[]{null, null};
        inserBtn.setOnMouseClicked( event -> {
            handleCinSubmission();
        } );

        loadingPage.setVisible( false );
    }


    private void handleCinSubmission() {
        if (files[0] == null || files[1] == null) {
            MyTools.getInstance().getTextNotif().setText( "You need to add the cin images" );
            MyTools.getInstance().showErrorNotif();
            return;
        }

        idInfoInterface.setOpacity( 0.4 );
        loadingPage.setVisible( true );
        handleCinData().start();
    }

    private Thread handleCinData() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                Thread.sleep( 2000 );
                int id = UserController.getInstance().getCurrentUser().getId();
                String frontPath = MyTools.getInstance().getPathAndSaveIMG( files[0].getAbsolutePath() );
                String backPath = MyTools.getInstance().getPathAndSaveIMG( files[1].getAbsolutePath() );
                String cinTest=AiVerification.HttpCinVerification( id, frontPath, backPath );
                if(cinTest.equals( "please insure that you added the right images" ))
                    return cinTest;
                return AiVerification.analyseEditedData( id );
            }
        };


        task.setOnSucceeded( event -> {
            Platform.runLater( () -> {
                idInfoInterface.setOpacity( 1 );
                loadingPage.setVisible( false );
                if (task.getValue().equals( "please insure that you added the right images" )) {
                    MyTools.getInstance().getTextNotif().setText( "please insure that you added the right images" );
                    MyTools.getInstance().showErrorNotif();
                    frontCin.setImage( new Image( String.valueOf( getClass().getResource( "/icons/marketPlace/driver-license.png" ) ) ) );
                    backCin.setImage( new Image( String.valueOf( getClass().getResource( "/icons/marketPlace/driver-license.png" ) ) ) );
                    files[0] = null;
                    files[1] = null;
                } else {
                    if(task.getValue().equals( "success" )) {
                        MyTools.getInstance().getTextNotif().setText( "data has been successfully updated" );
                        MyTools.getInstance().showNotif();
                    }
                    else{
                        MyTools.getInstance().getTextNotif().setText( task.getValue() );
                        MyTools.getInstance().showErrorNotif();
                    }
                }
            } );
        } );
        return new Thread( task );
    }


    @FXML
    private void onStateMouseEntered(MouseEvent event) throws ParseException {
        Label mainContainer;

        if (UserController.getInstance().getCurrentUser().getIsVerified() == 0) {
            String date = UserController.getInstance().getCurrentUser().getDate();
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            Date date1 = format.parse( date );
            String now = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ) );
            Date date2 = format.parse( now );

            long timeDifferenceMillis = 48 * 3600 * 1000 - (date2.getTime() - date1.getTime());
            long hours = timeDifferenceMillis / (60 * 60 * 1000);
            long minutes = (timeDifferenceMillis % (60 * 60 * 1000)) / (60 * 1000);


            mainContainer = new Label( "you need to add your cin images (front and back) so that we can verify you info " +
                    "otherwise your account will be deleted within: " + hours + "H and " + minutes + "m" );
        } else {
            mainContainer = new Label( "your account is verified" );
        }
        mainContainer.setMaxWidth( 400 );
        mainContainer.setWrapText( true );
        mainContainer.setAlignment( Pos.CENTER );
        mainContainer.setStyle( "-fx-background-color: #5f7470; -fx-border-color: #5f7470; -fx-border-width: 1px; -fx-padding: 5px; -fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 15px; " );


        popup.getContent().clear();
        popup.getContent().addAll( mainContainer );
        popup.show( Stage.getWindows().get( 0 ), event.getScreenX() + 20, event.getScreenY() + 20 );
    }


    @FXML
    private void onStateMouseExited(MouseEvent event) {
        popup.hide();
    }

    @FXML
    public void handelDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles())
            dragEvent.acceptTransferModes( TransferMode.ANY );
    }

    @FXML
    public void handelDrag(DragEvent dragEvent) {
        for (File file : dragEvent.getDragboard().getFiles()) {
            files[0] = file;
            frontCin.setImage( new Image( "file:" + file.getAbsolutePath(), 250, 254, true, true ) );
        }
    }


    @FXML
    public void handelDragOver2(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles())
            dragEvent.acceptTransferModes( TransferMode.ANY );
    }

    @FXML
    public void handelDrag2(DragEvent dragEvent) {
        for (File file : dragEvent.getDragboard().getFiles()) {
            files[1] = file;
            backCin.setImage( new Image( "file:" + file.getAbsolutePath(), 250, 254, true, true ) );
        }
    }

}
