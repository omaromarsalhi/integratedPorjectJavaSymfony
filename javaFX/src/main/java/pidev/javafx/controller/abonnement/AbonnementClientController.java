package pidev.javafx.controller.abonnement;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import pidev.javafx.crud.transport.ServicesAbonnement;
import pidev.javafx.model.Transport.Abonnement;
import pidev.javafx.model.Transport.Transport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import okhttp3.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.marketPlace.Flouci;
import pidev.javafx.tools.marketPlace.MyTools;

public class AbonnementClientController implements Initializable {

    ObservableList<Transport> dataList = FXCollections.observableArrayList();
    List<Abonnement> abonnementList = new ArrayList<>();
    ServicesAbonnement sa = new ServicesAbonnement();
    @FXML
    Pane paneToAnnimate;

    private Stage primaryStage;

    @FXML
    private Label DebutLabel;

    @FXML
    private Label FinLabel;

    @FXML
    private Label IdLabel;

    @FXML
    private Label NomLabel;
    @FXML
    private Pane pane;

    @FXML
    private Label PrenomLabel;
    @FXML
    private Button nextBtn;
    @FXML
    private Button previousBtn;
    @FXML
    private TextField NomText;
    @FXML
    private TextField PrenomText;
    @FXML
    private ComboBox<String> TypeAbonnementBox;
    @FXML
    private ImageView imageAbonne;
    @FXML
    private ImageView imageAbn;

    @FXML
    private AnchorPane loadinPage;
    @FXML
    private ImageView imageAbonnes;
    @FXML
    private Pane form;
    @FXML
    private Pane visiolScan;
    @FXML
    private ImageView imgAbn;









    private static final String API_KEY = "acc_94dd4f1769c190a";
    private static final String API_SECRET = "5a56d117d922cf4da9488e1349dd7c09";
    int i;
    Set<Abonnement> abonnementSet;
    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), paneToAnnimate);
    String imagePath;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        form.setVisible( false );
        visiolScan.setVisible( false );
        TypeAbonnementBox.getItems().addAll("Annuel", "mensuel");
        afficher();
        if (abonnementList.size() > 0)
            remplir_abonnement();
        translateTransition.setNode(paneToAnnimate);

        VBox root = new VBox();
        loadinPage.setVisible( false );
    }

    @FXML
    private VBox mainContainer;

    Map<String, Double> tagMap = new HashMap<>();

    @FXML
    public void afficher() {

        abonnementSet = sa.getAll();
        abonnementList = List.copyOf(abonnementSet);

    }

    public void insert_Image() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png")
        );
        var selectedFile = fileChooser.showOpenDialog(primaryStage);


        if (selectedFile != null) {
            Task<Map> task = new Task<Map>() {
                @Override
                protected Map call() throws Exception {
                    Platform.runLater(() -> {
                        visiolScan.setVisible( true );
                    });
                    return image_api(imagePath);
                }
            };

            task.setOnSucceeded(workerStateEvent -> {
                if (!task.getValue().isEmpty() && task.getValue().containsKey("man")) {
                    Platform.runLater(() -> {
                        visiolScan.setVisible( false );
                        imageAbonnes.setImage(new Image("file:///"+imagePath));
                    });
                }

                else {
                    visiolScan.setVisible( false );


                }
            });


            new Thread(task).start();


            imagePath = selectedFile.getAbsolutePath();
        }
    }


    @FXML
    public void expand() {
        BoxBlur blur = new BoxBlur();
        blur.setWidth(10);
        blur.setHeight(10);
        blur.setIterations(3);
        mainContainer.setEffect(blur);
        form.setOpacity(0.85);
        mainContainer.setOpacity(0.85);
        form.setVisible( true );
    }

    @FXML
    public void unexpand() {
        form.setVisible( false );
        mainContainer.setVisible( true );
        mainContainer.setEffect( null );
    }


    public void ajouter() {

        imagePath = MyTools.getInstance().getPathAndSaveIMG( String.valueOf( Paths.get(imagePath).toAbsolutePath() ) );

        Abonnement p = new Abonnement(NomText.getText(), PrenomText.getText(), TypeAbonnementBox.getValue().toString(), imagePath);
        pay(p);

    }

    private Thread sleepThread() {
        Task<Void> myTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                loadinPage.setVisible( true );
                Thread.sleep(2000);
                return null;
            }
        };

        myTask.setOnSucceeded(e -> {
            loadinPage.setVisible( false );
            unexpand();
        });
        return new Thread(myTask);
    }

    ;

    public void DeleteAbonnement() {

        int id = abonnementList.get(i).getIdAboonnement();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Vous voules vraiment effacer ce fichier ?");
        alert.showAndWait();
        sa.deleteItem(id);
        afficher();
        remplir_abonnement();
    }

    ;

    @FXML
    protected void onTextChanged() {
        String[] text = new String[10];

        text[1] = NomText.getText();
        text[2] = PrenomText.getText();

        if (text[1].matches("[a-zA-Z ]*"))
            NomText.setStyle("-fx-text-fill: #25c12c;");
        else
            NomText.setStyle("-fx-text-fill: #bb2020;");

        if (text[2].matches("[a-zA-Z ]+"))
            PrenomText.setStyle("-fx-text-fill: #25c12c");
        else
            PrenomText.setStyle("-fx-text-fill: #bb2020 ");
    }

    public void remplir_abonnement() {
        if (i == 0)
            previousBtn.setVisible(false);
        else if (i == abonnementList.size() - 1) {
            nextBtn.setVisible(false);
        }
        if(abonnementList.get(i).getType().equals("Annuel"))
        {
            Image image = new Image( "file:src/main/resources/img_transport/Abonnement.png");
            imgAbn.setImage(image);
        }
        else
        {
            Image image = new Image( "file:src/main/resources/img_transport/Abonnement1.png" );
            imgAbn.setImage(image);
        }
        String[] time = abonnementList.get(i).getDateDebut().toLocalDateTime().toString().split("T");
        String id = Integer.toString(abonnementList.get(i).getIdAboonnement());
        DebutLabel.setText(time[0]);
        FinLabel.setText(abonnementList.get(i).getDateFin().toString());
        NomLabel.setText(abonnementList.get(i).getNom());
        PrenomLabel.setText(abonnementList.get(i).getPrenom());
        IdLabel.setText("000" + id);
        imagePath = abonnementList.get(i).getImage();
        Image image = new Image( GlobalVariables.IMAGEPATH +"usersImg/"+ abonnementList.get(i).getImage());
        imageAbn.setImage(image);
        imageAbn.setStyle("-fx-background-radius: 50%;  ");
        imageAbn.getStyleClass().add("rounded-image");


    }

    @FXML
    public void nextAb() {

        if (i < abonnementList.size() - 1) {
            nextBtn.setVisible(true);
            previousBtn.setVisible(true);
            i = i + 1;
            remplir_abonnement();
        } else {
            nextBtn.setVisible(false);
            previousBtn.setVisible(true);
        }
    }


    @FXML
    public void previousAb() {

        if (i > 0) {

            previousBtn.setVisible(true);
            nextBtn.setVisible(true);
            i = i - 1;

            remplir_abonnement();
        } else if (i == 0) {
            previousBtn.setVisible(false);
            nextBtn.setVisible(true);
        }
    }

    public void LoadUpdate() {
        expand();
        UpdateBtn.setVisible(true);
        NomText.setText(abonnementList.get(i).getNom());
        PrenomText.setText(abonnementList.get(i).getPrenom());
        TypeAbonnementBox.setValue(abonnementList.get(i).getType());
        imagePath = abonnementList.get(i).getImage();

    }


    @FXML
    Button UpdateBtn;

    public void Update() {

        Abonnement A = new Abonnement(NomText.getText(), PrenomText.getText(), TypeAbonnementBox.getValue(), imagePath);
        A.setIdAboonnement(abonnementList.get(i).getIdAboonnement());
        sa.updateItem(A);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Vous voules vraiment effacer ce fichier ?");
        alert.showAndWait();
        UpdateBtn.setVisible(false);
        afficher();
        remplir_abonnement();
        unexpand();
    }

    @FXML
    VBox statsPannel;
    @FXML
    Pane statsPane;
    @FXML
    Button expandBtn;
    @FXML
    Button openBtn;


    public void exportPaneToImage() {
        WritableImage writableImage = paneToAnnimate.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                BufferedImage bufferedImage = new BufferedImage(
                        (int) writableImage.getWidth(),
                        (int) writableImage.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

                PixelReader pixelReader = writableImage.getPixelReader();
                for (int x = 0; x < writableImage.getWidth(); x++) {
                    for (int y = 0; y < writableImage.getHeight(); y++) {
                        bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
                    }
                }

                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    public Map<String, Double> image_api(String s) {
        imagePath = s; // Replace with your local image path

        OkHttpClient client = new OkHttpClient();
        Request request = buildRequest(imagePath);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();


            // Extract tags and confidence from the JSON response
            JsonArray tagsArray = json.getAsJsonObject("result").getAsJsonArray("tags");

            // Create Map to store the first 5 tags and their confidence

            tagMap.clear();
            for (int i = 0; i < Math.min(tagsArray.size(), 5); i++) {
                JsonObject tagObject = tagsArray.get(i).getAsJsonObject();
                String tag = tagObject.getAsJsonObject("tag").get("en").getAsString();
                double confidence = tagObject.get("confidence").getAsDouble();
                tagMap.put(tag, confidence);
            }

            // Print the contents of the map
            tagMap.forEach((tag, confidence) -> System.out.println("Tag: " + tag + ", Confidence: " + confidence));
            return tagMap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }





    public void loadPayment(Abonnement p, URL link) {
        WebView webView = new WebView();
        webView.setStyle("-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 20;" +
                "-fx-background-color: red");

        WebEngine webEngine = webView.getEngine();
        System.out.println(p.toString());

        // Ensure the URL is not null
        if (link != null) {
            webEngine.load(link.toString());
        } else {
            System.out.println("The URL link provided is null.");
            return;
        }

        webEngine.locationProperty().addListener((observableValue, s, t1) -> {
            if (t1.contains("Paymentsuccessful21")) {
                Platform.runLater(() -> {
                    pane.getChildren().remove(webView);
                 //   pane.getChildren().remove(2);
                    sa.addItem(p);
                    afficher();
                    remplir_abonnement();
                    sleepThread().start();
                });
            }
        });

        pane.getChildren().add(webView);
    }


    private static Request buildRequest(String imagePath) {
        File imageFile = new File(imagePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile))
                .build();

        return new Request.Builder()
                .url("https://api.imagga.com/v2/tags")
                .header("Authorization", "Basic " + getBasicAuth())
                .post(requestBody)
                .build();
    }

    private static String getBasicAuth() {
        String credentials = API_KEY + ":" + API_SECRET;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public void pay(Abonnement p){

        JSONObject jsonResponse = new JSONObject( Flouci.PayFlouci( 1010d ) );
        JSONObject result = jsonResponse.getJSONObject("result");
        String linkString = result.getString("link");
        URL link = null;
        try {
            link = new URL(linkString);
        } catch (MalformedURLException e) {
            throw new RuntimeException( e );
        }
        loadPayment( p, link);
    }
}
