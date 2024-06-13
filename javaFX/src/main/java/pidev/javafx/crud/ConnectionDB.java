package pidev.javafx.crud;

import pidev.javafx.tools.GlobalVariables;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDB {
    private Connection connect;
    private static ConnectionDB instance;

    public static ConnectionDB getInstance(){
        if(instance == null)
            instance = new ConnectionDB();
        return instance;
    }

    private ConnectionDB(){
            try{
//                connect= DriverManager.getConnection("jdbc:mysql://localhost/pi_dev", "root", "");
                connect= DriverManager.getConnection("jdbc:mysql://"+ GlobalVariables.IP +"/pi_dev", "root", "omar");
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
    }

    public Connection getCnx(){
        return this.connect;
    }
}
