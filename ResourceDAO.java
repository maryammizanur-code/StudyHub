package studymate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public boolean addResource(
            int userId,
            String title,
            String url,
            String description
    ) throws SQLException {

        String sql = "INSERT INTO resources "
                + "(user_id, title, url, description) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, url);
            ps.setString(4, description);

            return ps.executeUpdate() > 0;
        }
    }

    public List<Object[]> getResources(int userId)
            throws SQLException {

        List<Object[]> resources = new ArrayList<>();

        String sql = "SELECT resource_id, title, url, "
                + "description, created_at "
                + "FROM resources "
                + "WHERE user_id = ? "
                + "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Object[] resource = {
                        rs.getInt("resource_id"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                    };

                    resources.add(resource);
                }
            }
        }

        return resources;
    }

    public boolean deleteResource(int userId, int resourceId)
            throws SQLException {

        String sql = "DELETE FROM resources "
                + "WHERE resource_id = ? AND user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, resourceId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }
}