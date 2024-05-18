package pidev.javafx.controller.user;

import pidev.javafx.model.user.Role;
import pidev.javafx.model.user.User;

public class UserController {

    private User user;
    private static UserController instance;

    private UserController() {
    }

    public static UserController getInstance() {
        if (instance == null)
            instance = new UserController();
        return instance;
    }

    public User getCurrentUser(){
        return this.user;
    }
}
