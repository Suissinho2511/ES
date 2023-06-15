package eu.europa.esig.dss.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDataAccess {

    public static Boolean userLogin(String user, String pass) {
        try {
            Connection conn = DatabaseConnectionPool.establishConnection();
            Statement stmt = DatabaseConnectionPool.createStatement(conn);

            ResultSet resultSet = stmt
                    .executeQuery("SELECT * FROM user WHERE username = '" + user + "' AND password = '" + pass
                            + "'");
            if (resultSet.next())
                return true;
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static Boolean modifyPhoneNumber(String user, String newNumber) {
        if (!newNumber.matches("[0-9]{9}"))
            return false;
        try {
            Connection conn = DatabaseConnectionPool.establishConnection();
            Statement stmt = DatabaseConnectionPool.createStatement(conn);

            int result = stmt.executeUpdate(
                    "UPDATE user SET phonenumber = '" + newNumber + "' WHERE username = '" + user + "'");

            if (result == 0)
                return false;

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String retrievePhoneNumber(String user) {
        try {
            Connection conn = DatabaseConnectionPool.establishConnection();
            Statement stmt = DatabaseConnectionPool.createStatement(conn);

            ResultSet resultSet = stmt.executeQuery("SELECT phonenumber FROM user WHERE username = '" + user + "'");

            if (resultSet.next())
                return resultSet.getString("phonenumber");

            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
