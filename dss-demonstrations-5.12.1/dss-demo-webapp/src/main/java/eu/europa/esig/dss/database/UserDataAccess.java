package eu.europa.esig.dss.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class UserDataAccess {

    public static Boolean userLogin(String user, String pass) {
        try {
            Connection conn = DatabaseConnectionPool.establishConnection();
            String query = "SELECT password FROM user WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, user);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                if (BCrypt.checkpw(pass, storedPasswordHash)) {
                    return true;
                }
            }
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
            String query = "UPDATE user SET phonenumber = ? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newNumber);
            pstmt.setString(2, user);

            int result = pstmt.executeUpdate();

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
            String query = "SELECT phonenumber FROM user WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, user);

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next())
                return resultSet.getString("phonenumber");

            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
