package pidev.javafx.controller.abonnement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import pidev.javafx.crud.transport.ServicesAbonnement;
import pidev.javafx.model.Transport.Abonnement;
import pidev.javafx.model.Transport.Station;
import pidev.javafx.tools.GlobalVariables;
import pidev.javafx.tools.transport.allStat;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class abonnementAdminController implements Initializable {

    @FXML
    private ListView<Abonnement> abonnementListView;
    private final ServicesAbonnement sa = new ServicesAbonnement();

    @FXML
    private TextField NomText;
    @FXML
    private TextField PrenomText;
    @FXML
    private TextField SearchText;
    @FXML
    private ComboBox<String> TypeAbonnementBox;
    @FXML
    private ComboBox<Station> Depart;
    @FXML
    private ComboBox<Station> Arrive;
    @FXML
    private Button UpdateBtn;
    @FXML
    private Pane displayTransport;
    @FXML
    private Pane displayTransport1;
    @FXML
    private Button expandBtn;
    @FXML
    private Button insertStation;
    @FXML
    private Pane statsPane;
    @FXML
    private VBox statsPannel;
    @FXML
    private Pane UpdatePane;
    @FXML
    private BarChart<String, Number> series1;

    private List<Abonnement> abonnementList = new ArrayList<>();
    private ObservableList<Abonnement> data;
    private Abonnement selectedItem = new Abonnement();
    private Abonnement selectedItem_1 = new Abonnement();
    private final String destinationString = "src/main/resources/";
    private final allStat stat = new allStat();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TypeAbonnementBox.getItems().addAll("Annuel", "mensuel");
        afficher();
    }

    public void LoadUpdate() {
        Abonnement abn = sa.findById(abonnementListView.getSelectionModel().getSelectedItem().getIdAboonnement());

        UpdateBtn.setVisible(true);
        NomText.setText(abn.getNom());
        PrenomText.setText(abn.getPrenom());
        TypeAbonnementBox.setValue(abn.getType());
        UpdatePane.setOpacity(0.85);
        UpdatePane.toFront();
    }

    public void onListViewClicked(MouseEvent event) {
        selectedItem_1 = abonnementListView.getSelectionModel().getSelectedItem();
        if (selectedItem_1 != null && !selectedItem_1.equals(selectedItem)) {
            selectedItem = selectedItem_1;
        }
    }

    public void afficher() {
        Set<Abonnement> dataList = sa.getAll();
        data = FXCollections.observableArrayList(dataList);
        abonnementListView.setItems(data);

        abonnementListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Abonnement> call(ListView<Abonnement> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Abonnement abs, boolean empty) {
                        super.updateItem(abs, empty);
                        if (empty || abs == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(abs.getNom() + " " + abs.getPrenom());
                            ImageView imageView = new ImageView();
                            String imagePath =  GlobalVariables.IMAGEPATH + "usersImg/" + abs.getImage();
                            try {
                                Image image = new Image(imagePath);
                                imageView.setImage(new Image(GlobalVariables.IMAGEPATH +"usersImg/"+ abs.getImage()));
                                imageView.setFitWidth(70);
                                imageView.setFitHeight(70);
                            } catch (Exception e) {
                                e.printStackTrace();
                                imageView.setImage(null); // Handle missing images gracefully
                            }
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });
    }

    public void delete() {
        sa.deleteItem(abonnementListView.getSelectionModel().getSelectedItem().getIdAboonnement());
        afficher();
    }

    public void searchAbonnement() {
        if (SearchText.getText().isEmpty()) {
            abonnementListView.setItems(data);
        } else {
            ObservableList<Abonnement> filteredAbonnements = FXCollections.observableArrayList();
            for (Abonnement abonnement : data) {
                if (abonnement.getPrenom().toLowerCase().contains(SearchText.getText().toLowerCase())) {
                    filteredAbonnements.add(abonnement);
                }
            }
            abonnementListView.setItems(filteredAbonnements);
        }
    }
}
