package pidev.javafx.controller.login;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;
import pidev.javafx.crud.user.ServiceUser;
import pidev.javafx.model.user.Role;
import pidev.javafx.model.user.User;
import pidev.javafx.test.Main;
import pidev.javafx.tools.UserController;
import pidev.javafx.tools.marketPlace.AiVerification;
import pidev.javafx.tools.marketPlace.MyTools;
import pidev.javafx.tools.user.EmailController;
import pidev.javafx.tools.user.GoogleApi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class NewLogInController implements Initializable {

    @FXML
    private TextField alert;

    @FXML
    private TextField alertLogIn;

    @FXML
    private StackPane blackSide;

    @FXML
    private TextField code;

    @FXML
    private TextField email;

    @FXML
    private TextField emailReset;

    @FXML
    private TextField emailSignUp;

    @FXML
    private AnchorPane firstLayout;

    @FXML
    private TextField firstname;

    @FXML
    private Button google;


    @FXML
    private Button googleSignup;

    @FXML
    private TextField lastName;

    @FXML
    private AnchorPane layoutCode;

    @FXML
    private AnchorPane layoutReset;

    @FXML
    private AnchorPane layoutSignin;

    @FXML
    private AnchorPane layoutSignup;

    @FXML
    private VBox mailCode;

    @FXML
    private AnchorPane opsPage;

    @FXML
    private TextField password;

    @FXML
    private TextField passwordSignUp;

    @FXML
    private AnchorPane preloadingPage;

    @FXML
    private Button resetBtn;

    @FXML
    private VBox resetLoading;

    @FXML
    private Label resetPassword;

    @FXML
    private Button signin;

    @FXML
    private Button signinBtn;

    @FXML
    private Button signup;

    @FXML
    private Button signupBtn;

    @FXML
    private VBox vboxSignup;

    @FXML
    private Button verifier;

    @FXML
    private TextField alertSignUp;

    @FXML
    private AnchorPane yellowSide;

    @FXML
    private WebView googleWebView;


    private boolean btnState;
    private boolean[] loginInputValidation;
    private boolean[] signUpInputValidation;
    private boolean resetPasswordInputValidation;
    private int elapsedTime = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//        firstname.setText( "omar" );
//        emailSignUp.setText( "aaaaaaaaaaa@gmail.com" );
//        lastName.setText( "salhi" );
//        passwordSignUp.setText( "Latifa123@l" );
//
        email.setText( "salhiomar362@gmail.com" );
        password.setText( "Latifa123@l" );

        layoutCode.setVisible( false );
        signinBtn.setVisible( false );
        signinBtn.setVisible( false );
        layoutSignup.setVisible( false );
        layoutReset.setVisible( false );
        opsPage.setVisible( false );
        preloadingPage.setVisible( false );
        resetLoading.setVisible( false );
        btnState = true;

        signinBtn.setOnMouseClicked( event -> {
            translate( -400, 300 );
            btnState = !btnState;
        } );
        signupBtn.setOnMouseClicked( event -> {
            translate( -400, 300 );
            btnState = !btnState;
        } );

        setRegEx();

//        loginInputValidation = new boolean[]{false, false};
        loginInputValidation = new boolean[]{true, true};
        signUpInputValidation = new boolean[]{false, false, false, false};
        resetPasswordInputValidation = false;

        resetPassword.setOnMouseClicked( event -> showResetPassword() );

        hideAlertDanger( alert );
        hideAlertDanger( alertLogIn );
        hideAlertDanger( alertSignUp );
    }


    private void showAlertDanger(TextField node, String message) {
        node.setText( message );
        node.setVisible( true );
        node.setStyle( "-fx-background-color: rgba(224,55,55,0.48);" +
                "-fx-border-color: red;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px" );
    }


    private void hideAlertDanger(TextField node) {
        node.setVisible( false );
    }

    public void showResetPassword() {
        signin.setDisable( true );
        signup.setDisable( true );
        layoutReset.setVisible( true );
        layoutSignin.setVisible( false );
        layoutSignup.setVisible( false );
        resetBtn.setOnMouseClicked( event1 ->
                resetPassword()
        );
    }


    public void resetPassword() {

        if (emailReset.getText().isEmpty()) {
            showAlertDanger( alert, "this mail does not existe !!" );
            return;
        }

        ServiceUser serviceUser = new ServiceUser();
        User user = serviceUser.findParEmail( emailReset.getText() );

        if (user == null) {
            showAlertDanger( alert, "this mail does not existe !!" );
            return;
        }

        EmailController.sendEmail( emailReset.getText(), "this is a generated code to reset your password", user.generateVerificationCode() );

        firstLayout.setVisible( false );
        layoutCode.setVisible( true );

        verifier.setOnMouseClicked( event2 -> {
            if (code.getText().equals( user.getVerificationCode() )) {
                resetLoading.setVisible( true );
                setDataUser( user );
                UserController.setUser( user );
                UserController.getInstance().getCurrentUser().setPassReseted( true );
                loginThread( "reset" ).start();
            } else {
                layoutCode.setVisible( false );
                firstLayout.setVisible( false );
                opsPage.setVisible( true );
                sleepThread().start();
            }
        } );
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
            System.exit( 0 );
        } );

        return new Thread( myTask );
    }


    @FXML
    public void logIn(ActionEvent actionEvent) {
//        MyTools.HttpSaveImage();
        preloadingPage.setVisible( true );
        loginThread( "login" ).start();
    }

    private Thread loginThread(String usage) {
        Task<Integer> myTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                Thread.sleep( 1000 );

                if (usage.equals( "reset" ))
                    return 4;

                if (!loginInputValidation[0] || !loginInputValidation[1])
                    return 1;

                ServiceUser serviceUser = new ServiceUser();
                User user = serviceUser.findParEmail( email.getText() );

                if (user == null)
                    return 2;

                BCrypt.gensalt( 15 );
                if (BCrypt.checkpw( password.getText(), user.getPassword().replace( "$2y$", "$2a$" ) )) {
                    UserController.setUser( user );
                    return 4;
                }

                return 3;
            }
        };

        myTask.setOnSucceeded( e -> {
            Platform.runLater( () -> {
                if (myTask.getValue() == 1) {
                    preloadingPage.setVisible( false );
                    showAlertDanger( alertLogIn, "some data is not well formatted !!" );
                } else if (myTask.getValue() == 2) {
                    preloadingPage.setVisible( false );
                    showAlertDanger( alertLogIn, " this email is wrong  !!" );
                    email.setStyle( "-fx-border-color: red;" +
                            "-fx-border-width: 0 0 1px 0" );
                    email.setText( "" );
                } else if (myTask.getValue() == 3) {
                    preloadingPage.setVisible( false );
                    showAlertDanger( alertLogIn, " this password is wrong !!" );
                    password.setStyle( "-fx-border-color: red;" +
                            "-fx-border-width: 0 0 1px 0" );
                    password.setText( "" );
                } else if (myTask.getValue() == 4) {
                    loadManWindow();
                }
            } );
        } );

        return new Thread( myTask );
    }


    @FXML
    public void signUp(ActionEvent actionEvent) {
        preloadingPage.setVisible( true );
        signUpThread().start();
    }


    private Thread signUpThread() {
        Task<Integer> myTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                Thread.sleep( 1000 );

                if (!signUpInputValidation[0] || !signUpInputValidation[1] || !signUpInputValidation[2] || !signUpInputValidation[3])
                    return 1;

                ServiceUser serviceUser = new ServiceUser();
                User user = serviceUser.findParEmail( email.getText() );

                if (user != null)
                    return 2;

                return 3;
            }
        };

        myTask.setOnSucceeded( e -> {
            Platform.runLater( () -> {
                if (myTask.getValue() == 1) {
                    preloadingPage.setVisible( false );
                    showAlertDanger( alertSignUp, "some data is not well formatted !!" );
                } else if (myTask.getValue() == 2) {
                    preloadingPage.setVisible( false );
                    showAlertDanger( alertSignUp, " this account already exists  !!" );
                } else if (myTask.getValue() == 3) {
                    User user = new User();
                    layoutSignup.setStyle( String.valueOf( getClass().getResource( "/style/user/newLogIn.css" ) ) );
                    firstLayout.setVisible( false );
                    layoutCode.setVisible( true );
                    MyTools.getInstance().showAnimation( layoutCode );
                    System.out.println( user.generateVerificationCode() );
                    timer( 60, 1000, user );
                }
            } );
        } );
        return new Thread( myTask );
    }

    private void timer(int timeToWait, int period, User user) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (elapsedTime < timeToWait) {
                    verifier.setOnAction( event -> {
                        if (code.getText().equals( user.getVerificationCode() )) {
                            resetLoading.setVisible( true );
                            ServiceUser serviceUser = new ServiceUser();
                            setDataUser( user );
                            serviceUser.ajouter( user );
                            UserController.setUser( serviceUser.findParEmail( user.getEmail() ) );
                            AiVerification.counter( UserController.getInstance().getCurrentUser().getId());
                            loginThread( "reset" ).start();
                            timer.cancel();
                        } else {
                            Platform.runLater( () -> {
                                firstLayout.setVisible( false );
                                opsPage.setVisible( true );
                                sleepThread().start();
                            } );
                            elapsedTime = timeToWait * 2;
                        }
                    } );
                    elapsedTime++;
                } else {
                    timer.cancel();
                }
            }
        };
        timer.schedule( task, 0, period );
    }


    public void loadManWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader( Main.class.getResource( "/fxml/mainWindow/mainWindow.fxml" ) );
        Scene scene = null;
        try {
            scene = new Scene( fxmlLoader.load(), Color.TRANSPARENT );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
        Stage stage = new Stage();
        stage.initStyle( StageStyle.TRANSPARENT );
        stage.setTitle( "CitizenHub" );
        stage.setResizable( true );
        stage = (Stage) signinBtn.getScene().getWindow();
        stage.close();
        stage.setScene( scene );
        stage.show();
        GlobalSocketConnection.initiateConnection();
    }


    public void clean() {
        firstname.clear();
        lastName.clear();
        emailSignUp.clear();
        passwordSignUp.clear();
        code.clear();
    }

    public void setDataUser(User user) {
        user.setFirstname( firstname.getText() );
        user.setEmail( emailSignUp.getText() );
        user.setLastname( lastName.getText() );
        user.setPassword( BCrypt.hashpw( passwordSignUp.getText(), BCrypt.gensalt( 15 ) ) );
        user.setRole( Role.Citoyen );
        user.setAdresse( "" );
        user.setAge( 0 );
        user.setCin( "0" );
        user.setStatus( "0" );
        user.setGender( "" );
    }


    public void signupWithGoogle(ActionEvent actionEvent) {
        GoogleApi googleApi = new GoogleApi();
        WebView webView = googleApi.AccessTokenFetcher();
        AnchorPane anchorPane = new AnchorPane( webView );
        anchorPane.setMaxWidth( 150 );
        anchorPane.setMaxHeight( 100 );
        WebEngine webEngine = webView.getEngine();
        Scene scene = new Scene( anchorPane );
        Stage stage;
        stage = (Stage) google.getScene().getWindow();
        stage.setScene( scene );
        stage.close();
        stage.show();
    }

    public void signInWithGoogle(ActionEvent actionEvent) {
//        GoogleApi googleApi = new GoogleApi();
//        WebView webView = googleApi.AccessTokenFetcher();
//        AnchorPane anchorPane = new AnchorPane( webView );
//        anchorPane.setMaxWidth( 150 );
//        anchorPane.setMaxHeight( 100 );
//        WebEngine webEngine = webView.getEngine();
//        Scene scene = new Scene( anchorPane );
//        Stage stage;
//        stage = (Stage) google.getScene().getWindow();
//        stage.setScene( scene );
//        stage.close();
//        stage.show();
        System.setProperty( "sun.net.http.allowRestrictedHeaders", "true" );
        GoogleApi googleApi = new GoogleApi();
        WebView webView = googleApi.AccessTokenFetcher();
    }

    public void display(String email, String name, String lastname) {
        this.emailSignUp.setText( email );
        this.firstname.setText( name );
        //this.lastname=lastname;
    }


    public void translate(int yellowX, int blackX) {
        TranslateTransition translateTransitionYellowPart = new TranslateTransition( Duration.seconds( 0.6 ), yellowSide );
        FadeTransition fadeTransition = new FadeTransition( Duration.seconds( 0.3 ), blackSide );
        TranslateTransition translateTransitionBlackPart = new TranslateTransition( Duration.seconds( 0.6 ), blackSide );

        translateTransitionYellowPart.setToX( (btnState) ? yellowX : 0 );
        translateTransitionBlackPart.setToX( (btnState) ? blackX : 0 );

        fadeTransition.setToValue( 0 );
        fadeTransition.play();

        translateTransitionYellowPart.play();
        translateTransitionBlackPart.play();

        if (btnState)
            yellowSide.setStyle( "-fx-background-radius: 10 0 0 10;" +
                    "  -fx-border-radius: 10 0 0 10;" );
        else
            yellowSide.setStyle( "" );

        translateTransitionBlackPart.setOnFinished( event -> {

            layoutSignin.setVisible( btnState );
            layoutSignup.setVisible( !btnState );

            signinBtn.setVisible( !btnState );
            signupBtn.setVisible( btnState );


            fadeTransition.setToValue( 1 );
            fadeTransition.play();
        } );
    }

    public void setRegEx() {

        String firstnameRegex = "[a-zA-Z\\s]{3,}+";
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        firstname.setOnKeyTyped( event -> {
            String color = (firstname.getText().matches( firstnameRegex )) ? "green" : "#ff4343";
            signUpInputValidation[0] = color.equals( "green" );
            if (firstname.getText().isEmpty()) {
                firstname.setStyle( "" );
            } else {
                firstname.setStyle( "-fx-border-color:" + color + ";" +
                        "-fx-border-width:0 0 2 0;" );
            }
        } );

        lastName.setOnKeyTyped( event -> {
            String color = (lastName.getText().matches( firstnameRegex )) ? "green" : "#ff4343";
            signUpInputValidation[1] = color.equals( "green" );
            if (lastName.getText().isEmpty()) {
                lastName.setStyle( "" );
            } else {
                lastName.setStyle( "-fx-border-color:" + color + ";-fx-border-width:0 0 2 0;" );
            }
        } );

        emailSignUp.setOnKeyTyped( event -> {
            String color = (emailSignUp.getText().matches( emailRegex )) ? "green" : "#ff4343";
            signUpInputValidation[2] = color.equals( "green" );
            if (emailSignUp.getText().isEmpty()) {
                emailSignUp.setStyle( "" );
            } else {
                emailSignUp.setStyle( "-fx-border-color:" + color + ";-fx-border-width:0 0 2 0;" );
            }
        } );

        passwordSignUp.setOnKeyTyped( event -> {
            String color = (passwordSignUp.getText().matches( passwordRegex )) ? "green" : "#ff4343";
            signUpInputValidation[3] = color.equals( "green" );
            if (passwordSignUp.getText().isEmpty()) {
                passwordSignUp.setStyle( "" );
            } else {
                passwordSignUp.setStyle( "-fx-border-color:" + color + ";-fx-border-width:0 0 2 0;" );
            }
        } );


        email.setOnKeyTyped( event -> {
            String color = (email.getText().matches( emailRegex )) ? "green" : "#ff4343";
            loginInputValidation[0] = color.equals( "green" );
            if (email.getText().isEmpty()) {
                email.setStyle( "" );
            } else {
                email.setStyle( "-fx-border-color:" + color + ";-fx-border-width:0 0 2 0;" );
            }
        } );


        password.setOnKeyTyped( event -> {
            String color = (password.getText().matches( passwordRegex )) ? "green" : "#ff4343";
            loginInputValidation[1] = color.equals( "green" );
            if (password.getText().isEmpty()) {
                password.setStyle( "" );
            } else {
                password.setStyle( "-fx-border-color:" + color + ";-fx-border-width:0 0 2 0;" );
            }
        } );


        emailReset.setOnKeyTyped( event -> {
            String color = (emailReset.getText().matches( emailRegex )) ? "green" : "#ff4343";
            resetPasswordInputValidation = color.equals( "green" );
            if (emailReset.getText().isEmpty()) {
                emailReset.setStyle( "" );
            } else {
                emailReset.setStyle( "-fx-border-color:" + color + ";-fx-border-width:0 0 2 0;" );
            }
        } );
    }


}

