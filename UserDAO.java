
package studymate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public static class UserProfile {
        private final int userId;
        private final String fullName;
        private final String email;
        private final String createdAt;
        public UserProfile(int userId, String fullName,String email, String createdAt){
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.createdAt = createdAt;
        }
        public int getUserId(){
            return userId;
        }
        public String getFullName(){
            return fullName;
        }
        public String getEmail(){
            return email;
        }
        public String getCreatedAt(){
            return createdAt;
        }
    }
    public boolean registerUser(
            String fullName,
            String email,
            String passwordHash)
            throws SQLException{
        String sql = "INSERT INTO users "+ "(full_name, email, password_hash) "+ "VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            return ps.executeUpdate()>0;
        }
    }
    public String getPasswordHashByEmail(String email)
            throws SQLException{
        String sql = "SELECT password_hash "+ "FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getString("password_hash");
                }
            }
        }
        return null;
    }
    public String getPasswordHashByUserId(int userId)
            throws SQLException {
        String sql = "SELECT password_hash "+"FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return rs.getString("password_hash");
                }
            }
        }
        return null;
    }

    // 4. Get user ID by email
    public int getUserIdByEmail(String email)
            throws SQLException {

        String sql = "SELECT user_id "
                + "FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1;
    }
    public UserProfile getProfile(int userId)
            throws SQLException {
        String sql = "SELECT user_id, full_name, email, created_at "+ "FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("created_at")
                    );
                }
            }
        }
        return null;
    }
    public boolean updateProfile(
            int userId,
            String fullName,
            String email) throws SQLException {
        String sql = "UPDATE users "
                + "SET full_name = ?, email = ? "
                + "WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, userId);
            return ps.executeUpdate()==1;
        }
    }

    public boolean updatePasswordHash(int userId,String passwordHash)
            throws SQLException {
        String sql = "UPDATE users "
                + "SET password_hash = ? "
                + "WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate()==1;
        }
    }
}
