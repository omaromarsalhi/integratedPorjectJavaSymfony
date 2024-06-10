package pidev.javafx.crud.user;

import pidev.javafx.crud.ConnectionDB;
import pidev.javafx.model.user.Role;
import pidev.javafx.model.user.User;
import pidev.javafx.tools.UserController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IserviceUser<User> {

    @Override
    public void ajouter(User user) { //ajouter citoyen
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "INSERT INTO `user`(`firstName`,`lastName`,`email`,`password`,`date`,`role`,isVerified) VALUES (?,?,?,?,?,?,?)";
        try {

            PreparedStatement ps = cnx.prepareStatement( req );

            ps.setString( 1, user.getFirstname() );
            ps.setString( 2, user.getLastname() );
            ps.setString( 3, user.getEmail() );
            ps.setString( 4, user.getPassword() );
            ps.setString( 5, String.valueOf( LocalDate.now() ) );
            ps.setString( 6, String.valueOf( user.getRole() ) );
            ps.setInt( 7, user.getIsVerified() );

            ps.executeUpdate();

            System.out.println( "Personne added !" );
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
    }

    public User getUserById(int idUser) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "SELECT * FROM user WHERE idUser=?";
        User user = null;
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setInt( 1, idUser );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt( "idUser" );
                String firstname = rs.getString( "firstName" );
                String lastName = rs.getString( "lastname" );
                int age = rs.getInt( "age" );
                int num = rs.getInt( "phoneNumber" );
                String adresse = rs.getString( "address" );
                String dob = rs.getString( "dob" );
                String cin = rs.getString( "cin" );
                String status = rs.getString( "status" );
                String date = rs.getString( "date" );
                String photos = rs.getString( "image" );
                String gender = rs.getString( "gender" );
                String email = rs.getString( "email" );
                int idMun = rs.getInt( "idMunicipalite" );
                String password = rs.getString( "password" );
                int isVerified = rs.getInt( "isVerified" );
                user = new User( id, firstname, email, cin, age, num, adresse, dob, lastName, status, date, Role.valueOf( "Citoyen" ), photos, gender, password, idMun, isVerified );
            }
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
        return user;
    }


    public void ajouterResponsable(User user) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "INSERT INTO `user`(`firstName`,`lastname`,`email`,`age`,`phoneNumber`,`password`,`address`,`date`,`role`,`cin`,`status`,`idMunicipalite`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );

            ps.setString( 1, user.getFirstname() );
            ps.setString( 2, user.getLastname() );
            ps.setString( 3, user.getEmail() );
            ps.setString( 4, String.valueOf( user.getAge() ) );
            ps.setString( 5, String.valueOf( user.getNum() ) );
            ps.setString( 6, user.getPassword() );
            ps.setString( 7, user.getAdresse() );
            ps.setString( 8, String.valueOf( LocalDate.now() ) );
            ps.setString( 9, String.valueOf( user.getRole() ) );
            ps.setString( 10, user.getCin() );
            ps.setString( 11, user.getStatus() );
            ps.setString( 12, String.valueOf( user.getIdMunicipalite() ) );
            System.out.println( user.getIdMunicipalite() );

            ps.executeUpdate();
            System.out.println( "Personne added !" );
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }

    }


    @Override
    public void modifier(User user) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "UPDATE `user` SET `firstname` = ?,`lastname` = ?, `age` = ?, `cin` = ?, `dob` = ?, `phoneNumber` = ?, `status` = ?, `image` = ?, `gender` = ? ,`address`=? WHERE `email` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setString( 1, user.getFirstname() );
            ps.setString( 2, user.getLastname() );
            ps.setInt( 3, user.getAge() );
            ps.setString( 4, user.getCin() );
            ps.setString( 5, user.getDob() );
            ps.setInt( 6, user.getNum() );
            ps.setString( 7, user.getStatus() );
            ps.setString( 8, user.getPhotos() );
            ps.setString( 9, user.getGender() );
            ps.setString( 10, user.getAdresse() );
            ps.setString( 11, user.getEmail() );
            ps.executeUpdate();
            System.out.println( "User updated !" );
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }

    }

    public void isVerified(User user) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "UPDATE `user` SET `isVerified`= ? WHERE  `email`= ?";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setInt( 1, user.getIsConnected() );
            ps.setString( 2, user.getEmail() );
            System.out.println( user.getIsConnected() );
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
    }


    @Override
    public void supprimer(int id) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "DELETE FROM `user` WHERE `cin`= ?";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setInt( 1, id );
            ps.executeUpdate();
            System.out.println( "User deleted !" );
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }


    }

    @Override
    public void supprimerByEmail(String email) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "DELETE FROM `user` WHERE `email`= ?";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setString( 1, email );
            ps.executeUpdate();
            System.out.println( "User deleted !" );
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
    }

    @Override
    public User getOneById(int id) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        return null;
    }


    @Override
    public List<User> getAll() {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        List<User> users = new ArrayList<>();
        int userId = UserController.getInstance().getCurrentUser().getId();
        String req = "SELECT * FROM `user` where idUser in (select idSender from chat where idReciver=? )";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setInt( 1, userId );
            var result = ps.executeQuery();
            while (result.next()) {
                User user = new User();
                user.setId( result.getInt( "idUser" ) );
                user.setFirstname( result.getString( "firstName" ) );
                user.setLastname( result.getString( "lastname" ) );
                user.setEmail( result.getString( "email" ) );
                user.setPhotos( result.getString( "image" ) );
                users.add( user );
            }
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
        return users;
    }


    public User findParEmail(String email) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "SELECT * FROM `user` WHERE email=? ";
        User user = null;
        try {
            PreparedStatement ps = cnx.prepareStatement( req );
            ps.setString( 1, email );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt( "idUser" );
                String firstname = rs.getString( "firstName" );
                String lastName = rs.getString( "lastname" );
                int age = rs.getInt( "age" );
                int num = rs.getInt( "phoneNumber" );
                String adresse = rs.getString( "address" );
                String dob = rs.getString( "dob" );
                String cin = rs.getString( "cin" );
                String status = rs.getString( "status" );
                String date = rs.getString( "date" );
                String photos = rs.getString( "image" );
                String gender = rs.getString( "gender" );
                int idMun = rs.getInt( "idMunicipalite" );
                String password = rs.getString( "password" );
                int isVerified = rs.getInt( "isVerified" );
                user = new User( id, firstname, email, cin, age, num, adresse, dob, lastName, status, date, Role.valueOf( "Citoyen" ), photos, gender, password, idMun, isVerified );
            }
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
        return user;
    }


    public void modifierPassword(String email, String password) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "UPDATE `user` SET `password`=? WHERE `email` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );

            ps.setString( 1, password );
            ps.setString( 2, email );

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
    }

    public void modifierEmail(String email, String newEmail) {
        Connection cnx = ConnectionDB.getInstance().getCnx();
        String req = "UPDATE `user` SET `email`=?WHERE `email` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement( req );

            ps.setString( 1, newEmail );
            ps.setString( 2, email );

            ps.executeUpdate();
            System.out.println( "User updated !" );
        } catch (SQLException e) {
            System.out.println( e.getMessage() );
        }
    }


}
