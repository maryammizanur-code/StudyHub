
package studymate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(
            String fullName,
            String email,
            String passwordHash) throws SQLException {

        String sql = "INSERT INTO users "
                + "(full_name, email, password_hash) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, passwordHash);

            return ps.executeUpdate() > 0;
        }
    }
    
public String getPasswordHashByEmail(String email)
        throws SQLException {

    String sql = "SELECT password_hash FROM users WHERE email = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, email);

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        }
    }

    return null;
}

public int getUserIdByEmail(String email) throws SQLException {

    String sql = "SELECT user_id FROM users WHERE email = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, email);

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        }
    }

    return -1;
}


}
