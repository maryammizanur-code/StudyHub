
package studymate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskDAO {

    // 1. Add a new study task
    public boolean addTask(
            int userId,
            String title,
            String description,
            String taskType,
            String deadline) throws SQLException {

        String sql = "INSERT INTO tasks "
                + "(user_id, title, description, task_type, deadline, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, taskType);
            ps.setString(5, deadline);
            ps.setString(6, "Planned");

            return ps.executeUpdate() > 0;
        }
    }

    // 2. Display all tasks belonging to a user
    public String listTasks(int userId) throws SQLException {

        String sql = "SELECT task_id, title, description, task_type, "
                + "deadline, status "
                + "FROM tasks WHERE user_id = ? "
                + "ORDER BY deadline";

        StringBuilder result = new StringBuilder();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.append("Task ID: ")
                            .append(rs.getInt("task_id"))
                            .append("\nTitle: ")
                            .append(rs.getString("title"))
                            .append("\nDescription: ")
                            .append(rs.getString("description"))
                            .append("\nType: ")
                            .append(rs.getString("task_type"))
                            .append("\nDeadline: ")
                            .append(rs.getString("deadline"))
                            .append("\nStatus: ")
                            .append(rs.getString("status"))
                            .append("\n--------------------\n");
                }
            }
        }

        return result.length() == 0
                ? "No tasks found."
                : result.toString();
    }

    // 3. Mark a task as completed
    public boolean markTaskDone(int userId, int taskId)
            throws SQLException {

        String sql = "UPDATE tasks SET status = ? "
                + "WHERE user_id = ? AND task_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "Completed");
            ps.setInt(2, userId);
            ps.setInt(3, taskId);

            return ps.executeUpdate() > 0;
        }
    }

    // 4. Delete a task belonging to a user
    public boolean deleteTask(int userId, int taskId)
            throws SQLException {

        String sql = "DELETE FROM tasks "
                + "WHERE user_id = ? AND task_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, taskId);

            return ps.executeUpdate() > 0;
        }
    }

    // 5. Update an existing study task
    public boolean updateTask(
            int userId,
            int taskId,
            String title,
            String description,
            String taskType,
            String deadline) throws SQLException {

        String sql = "UPDATE tasks SET "
                + "title = ?, "
                + "description = ?, "
                + "task_type = ?, "
                + "deadline = ? "
                + "WHERE user_id = ? AND task_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, taskType);
            ps.setString(4, deadline);
            ps.setInt(5, userId);
            ps.setInt(6, taskId);

            return ps.executeUpdate() > 0;
        }
    }

    // 6. Search a user's tasks by title
    public String searchTasks(int userId, String keyword)
            throws SQLException {

        String sql = "SELECT task_id, title, description, task_type, "
                + "deadline, status "
                + "FROM tasks WHERE user_id = ? AND title LIKE ? "
                + "ORDER BY deadline";

        StringBuilder result = new StringBuilder();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.append("Task ID: ")
                            .append(rs.getInt("task_id"))
                            .append("\nTitle: ")
                            .append(rs.getString("title"))
                            .append("\nDescription: ")
                            .append(rs.getString("description"))
                            .append("\nType: ")
                            .append(rs.getString("task_type"))
                            .append("\nDeadline: ")
                            .append(rs.getString("deadline"))
                            .append("\nStatus: ")
                            .append(rs.getString("status"))
                            .append("\n--------------------\n");
                }
            }
        }

        return result.length() == 0
                ? "No matching tasks found."
                : result.toString();
    }

    // 7. Filter a user's tasks by status
    public String filterTasksByStatus(int userId, String status)
            throws SQLException {

        String sql = "SELECT task_id, title, description, task_type, "
                + "deadline, status "
                + "FROM tasks WHERE user_id = ? AND status = ? "
                + "ORDER BY deadline";

        StringBuilder result = new StringBuilder();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.append("Task ID: ")
                            .append(rs.getInt("task_id"))
                            .append("\nTitle: ")
                            .append(rs.getString("title"))
                            .append("\nDescription: ")
                            .append(rs.getString("description"))
                            .append("\nType: ")
                            .append(rs.getString("task_type"))
                            .append("\nDeadline: ")
                            .append(rs.getString("deadline"))
                            .append("\nStatus: ")
                            .append(rs.getString("status"))
                            .append("\n--------------------\n");
                }
            }
        }

        return result.length() == 0
                ? "No tasks found with this status."
                : result.toString();
    }
    
    // 8. Get task analytics for a specific user
    public int[] getTaskAnalytics(int userId) throws SQLException {

        String sql = "SELECT "
                + "COUNT(*) AS total_tasks, "
                + "SUM(CASE WHEN status = 'Completed' "
                + "THEN 1 ELSE 0 END) AS completed_tasks, "
                + "SUM(CASE WHEN status <> 'Completed' "
                + "AND deadline >= NOW() "
                + "THEN 1 ELSE 0 END) AS pending_tasks, "
                + "SUM(CASE WHEN status <> 'Completed' "
                + "AND deadline < NOW() "
                + "THEN 1 ELSE 0 END) AS overdue_tasks, "
                + "SUM(CASE WHEN status = 'Planned' "
                + "THEN 1 ELSE 0 END) AS planned_tasks, "
                + "SUM(CASE WHEN status = 'In Progress' "
                + "THEN 1 ELSE 0 END) AS in_progress_tasks "
                + "FROM tasks WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new int[]{
                        rs.getInt("total_tasks"),
                        rs.getInt("completed_tasks"),
                        rs.getInt("pending_tasks"),
                        rs.getInt("overdue_tasks"),
                        rs.getInt("planned_tasks"),
                        rs.getInt("in_progress_tasks")
                    };
                }
            }
        }

        return new int[6];
    }
    
    // 9. Get upcoming deadline reminders for a user
    public String getUpcomingDeadlineReminders(int userId)
            throws SQLException {

        String sql = "SELECT task_id, title, deadline, status "
                + "FROM tasks "
                + "WHERE user_id = ? "
                + "AND status <> 'Completed' "
                + "AND DATE(deadline) BETWEEN CURDATE() "
                + "AND DATE_ADD(CURDATE(), INTERVAL 1 DAY) "
                + "ORDER BY deadline ASC";

        StringBuilder reminders = new StringBuilder();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    reminders.append("Task ID: ")
                            .append(rs.getInt("task_id"))
                            .append("\nTitle: ")
                            .append(rs.getString("title"))
                            .append("\nDeadline: ")
                            .append(rs.getString("deadline"))
                            .append("\nStatus: ")
                            .append(rs.getString("status"))
                            .append("\n--------------------\n");
                }
            }
        }

        return reminders.length() == 0
                ? ""
                : reminders.toString();
    }


}
