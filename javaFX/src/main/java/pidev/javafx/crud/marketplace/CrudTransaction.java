package pidev.javafx.crud.marketplace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pidev.javafx.crud.ConnectionDB;
import pidev.javafx.crud.CrudInterface;
import pidev.javafx.model.MarketPlace.Transaction;
import pidev.javafx.model.MarketPlace.TransactionMode;

public class CrudTransaction implements CrudInterface<Transaction> {

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private static CrudTransaction instance;

    public static CrudTransaction getInstance() {
        if (instance == null) {
            instance = new CrudTransaction();
        }
        return instance;
    }

    @Override
    public void addItem(Transaction transaction) {
        String sql = "INSERT INTO transaction (idProduct,idContract, idSeller, idBuyer,pricePerUnit,quantity, transactionMode) " +
                "VALUES (?, ?, ?, ? ,? ,?,?)";

        connect = ConnectionDB.getInstance().getCnx();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, transaction.getIdProd());
            prepare.setInt(2, transaction.getIdContract());
            prepare.setInt(3, transaction.getIdSeller());
            prepare.setInt(4, transaction.getIdBuyer());
            prepare.setFloat(5, transaction.getPricePerUnit());
            prepare.setInt(6, transaction.getQuantity());
            prepare.setString(7, transaction.getTransactionMode().toString());
            prepare.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Transaction transaction) {
    }

    @Override
    public ObservableList<Transaction> selectItems() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        String selectSql = "SELECT * FROM transaction";
        connect = ConnectionDB.getInstance().getCnx();


        try {
            prepare = connect.prepareStatement(selectSql);
            result = prepare.executeQuery();

            while (result.next()) {
                transactions.add( new Transaction(result.getInt("idTransaction"),
                        result.getInt("idProd"),
                        result.getInt("idContract"),
                        result.getInt("idSeller"),
                        result.getInt("idBuyer"),
                        result.getFloat("pricePerUnit"),
                        result.getInt("quantity"),
                        TransactionMode.valueOf(result.getString("transactionMode")),
                        result.getTimestamp("timeStamp") ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public Transaction findById(int id) {
        return null;
    }



    @Override
    public Transaction selectFirstItem() {
        String selectFirstSql = "SELECT * FROM transaction LIMIT 1";
        connect = ConnectionDB.getInstance().getCnx();

        try {
            prepare = connect.prepareStatement(selectFirstSql);
            result = prepare.executeQuery();

            if (result.next()) {
                return new Transaction(result.getInt("idTransaction"),
                        result.getInt("idProd"),
                        result.getInt("idContract"),
                        result.getInt("idSeller"),
                        result.getInt("idBuyer"),
                        result.getFloat("pricePerUnit"),
                        result.getInt("quantity"),
                        TransactionMode.valueOf(result.getString("transactionMode")),
                        result.getTimestamp("timeStamp") );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteItem(int id) {
        String deleteSql = "DELETE FROM transaction WHERE idTransaction = ?";
        connect = ConnectionDB.getInstance().getCnx();

        try {
            prepare = connect.prepareStatement(deleteSql);
            prepare.setInt(1, id);

            prepare.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
