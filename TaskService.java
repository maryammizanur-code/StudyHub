
package studymate.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Set;
import studymate.database.TaskDAO;

public class TaskService {

    private final TaskDAO taskDAO = new TaskDAO();

    private static final Set<String> VALID_TASK_TYPES =
            Set.of("Assignment", "Exam", "Project");

    private static final DateTimeFormatter DEADLINE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);

    // 1. Add a task after validating its information
    public boolean addTask(
            int userId,
            String title,
            String description,
            String taskType,
            String deadline) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty.");
        }

        if (title.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Task title cannot exceed 150 characters.");
        }

        if (taskType == null || !VALID_TASK_TYPES.contains(taskType)) {
            throw new IllegalArgumentException(
                    "Task type must be Assignment, Exam, or Project.");
        }

        if (deadline == null) {
            throw new IllegalArgumentException("Deadline is required.");
        }

        try {
            LocalDateTime parsedDeadline =
                    LocalDateTime.parse(deadline, DEADLINE_FORMAT);

            if (parsedDeadline.toLocalDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Task deadline cannot be in the past.");
            }

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Deadline must use yyyy-MM-dd HH:mm:ss format.");
        }

        if (description != null && description.contains("|")) {
            throw new IllegalArgumentException(
                    "Description cannot contain the | character.");
        }

        if (title.contains("|")) {
            throw new IllegalArgumentException(
                    "Title cannot contain the | character.");
        }

        return taskDAO.addTask(
                userId,
                title.trim(),
                description,
                taskType,
                deadline);
    }

    // 2. Get all tasks belonging to a user
    public String listTasks(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        return taskDAO.listTasks(userId);
    }

    // 3. Mark a task as completed
    public boolean markTaskDone(int userId, int taskId)
            throws SQLException {

        if (userId <= 0 || taskId <= 0) {
            throw new IllegalArgumentException(
                    "User ID and task ID must be positive numbers.");
        }

        return taskDAO.markTaskDone(userId, taskId);
    }

    // 4. Delete a task belonging to a user
    public boolean deleteTask(int userId, int taskId)
            throws SQLException {

        if (userId <= 0 || taskId <= 0) {
            throw new IllegalArgumentException(
                    "User ID and task ID must be positive numbers.");
        }

        return taskDAO.deleteTask(userId, taskId);
    }

    // 5. Update an existing study task
    public boolean updateTask(
            int userId,
            int taskId,
            String title,
            String description,
            String taskType,
            String deadline) throws SQLException {

        if (userId <= 0 || taskId <= 0) {
            throw new IllegalArgumentException(
                    "User ID and task ID must be positive numbers.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Task title cannot be empty.");
        }

        if (title.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Task title cannot exceed 150 characters.");
        }

        if (title.contains("|")) {
            throw new IllegalArgumentException(
                    "Title cannot contain the | character.");
        }

        if (description != null && description.contains("|")) {
            throw new IllegalArgumentException(
                    "Description cannot contain the | character.");
        }

        if (taskType == null || !VALID_TASK_TYPES.contains(taskType)) {
            throw new IllegalArgumentException(
                    "Task type must be Assignment, Exam, or Project.");
        }

        if (deadline == null) {
            throw new IllegalArgumentException(
                    "Deadline is required.");
        }

        try {
            LocalDateTime parsedDeadline =
                    LocalDateTime.parse(deadline, DEADLINE_FORMAT);

            if (parsedDeadline.toLocalDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Task deadline cannot be in the past.");
            }

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Deadline must use yyyy-MM-dd HH:mm:ss format.");
        }

        return taskDAO.updateTask(
                userId,
                taskId,
                title.trim(),
                description,
                taskType,
                deadline);
    }

    // 6. Search a user's tasks by title
    public String searchTasks(int userId, String keyword)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty.");
        }

        if (keyword.contains("|")) {
            throw new IllegalArgumentException(
                    "Search keyword cannot contain the | character.");
        }

        return taskDAO.searchTasks(userId, keyword.trim());
    }

    // 7. Filter a user's tasks by status
    public String filterTasksByStatus(int userId, String status)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID.");
        }

        if (status == null
                || !(status.equals("Planned")
                || status.equals("In Progress")
                || status.equals("Completed"))) {

            throw new IllegalArgumentException(
                    "Status must be Planned, In Progress, or Completed.");
        }

        return taskDAO.filterTasksByStatus(userId, status);
    }
    
    // 8. Get study analytics for a user
    public int[] getTaskAnalytics(int userId) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        return taskDAO.getTaskAnalytics(userId);
    }
    
    // 9. Get upcoming deadline reminders for a user
    public String getUpcomingDeadlineReminders(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        return taskDAO.getUpcomingDeadlineReminders(userId);
    }


}
