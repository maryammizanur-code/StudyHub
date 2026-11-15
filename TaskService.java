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
    private static final Set<String> VALID_TASK_TYPES = Set.of("Assignment", "Exam", "Project");
    private static final Set<String> VALID_STATUSES = Set.of("Planned", "In Progress", "Completed");
    private static final DateTimeFormatter DEADLINE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public boolean addTask(int userId,String title,String description,String taskType,String deadline)
            throws SQLException{
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        if (title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        if (title.trim().length() > 150) {
            throw new IllegalArgumentException("Task title cannot exceed 150 characters.");
        }
        if (taskType == null || !VALID_TASK_TYPES.contains(taskType)){
            throw new IllegalArgumentException("Task type must be Assignment, Exam, or Project.");
        }
        if (deadline == null){
            throw new IllegalArgumentException("Deadline is required.");
        }
        try {
            LocalDateTime parsedDeadline =LocalDateTime.parse(deadline, DEADLINE_FORMAT);

            if (parsedDeadline.toLocalDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Task deadline cannot be in the past.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Deadline must use yyyy-MM-dd HH:mm:ss format.");
        }
        if (description != null && description.contains("|")) {
            throw new IllegalArgumentException( "Description cannot contain the | character.");
        }
        if (title.contains("|")) {
            throw new IllegalArgumentException("Title cannot contain the | character.");
        }
        return taskDAO.addTask(userId,title.trim(),description,taskType,deadline);
    }

    public String listTasks(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return taskDAO.listTasks(userId);
    }

    public boolean markTaskDone(int userId, int taskId)
            throws SQLException{
        if(userId <= 0 || taskId <= 0){
            throw new IllegalArgumentException("User ID and task ID must be positive numbers.");
        }
        return taskDAO.markTaskDone(userId, taskId);
    }
    public boolean deleteTask(int userId, int taskId)
            throws SQLException{
        if (userId <= 0 || taskId <= 0) {
            throw new IllegalArgumentException("User ID and task ID must be positive numbers.");
        }
        return taskDAO.deleteTask(userId, taskId);
    }
    public boolean updateTask(int userId,int taskId,String title,String description, String taskType,String deadline)
            throws SQLException{
        if (userId <= 0 || taskId <= 0){
            throw new IllegalArgumentException("User ID and task ID must be positive numbers.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        if(title.trim().length() > 150){
            throw new IllegalArgumentException("Task title cannot exceed 150 characters.");
        }
        if(title.contains("|")){
            throw new IllegalArgumentException("Title cannot contain the | character.");
        }
        if (description != null && description.contains("|")) {
            throw new IllegalArgumentException("Description cannot contain the | character.");
        }
        if (taskType == null || !VALID_TASK_TYPES.contains(taskType)) {
            throw new IllegalArgumentException("Task type must be Assignment, Exam, or Project.");
        }
        if (deadline == null) {
            throw new IllegalArgumentException("Deadline is required.");
        }
        try {
            LocalDateTime parsedDeadline = LocalDateTime.parse(deadline, DEADLINE_FORMAT);
            if (parsedDeadline.toLocalDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException( "Task deadline cannot be in the past.");
            }
        } catch(DateTimeParseException e){
            throw new IllegalArgumentException( "Deadline must use yyyy-MM-dd HH:mm:ss format.");
        }
        return taskDAO.updateTask(userId,taskId,title.trim(),description,taskType,deadline);
    }
    public String searchTasks(int userId, String keyword)
            throws SQLException{
        if(userId <= 0){
            throw new IllegalArgumentException("Invalid user ID.");
        }
        if(keyword == null || keyword.trim().isEmpty()){
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }
        if(keyword.contains("|")){
            throw new IllegalArgumentException("Search keyword cannot contain the | character.");
        }
        return taskDAO.searchTasks(userId, keyword.trim());
    }
    public String filterTasksByStatus(int userId, String status)
            throws SQLException{
        if(userId <= 0){
            throw new IllegalArgumentException("Invalid user ID.");
        }
        if (status == null || !VALID_STATUSES.contains(status)){
            throw new IllegalArgumentException( "Status must be Planned, In Progress, or Completed.");
        }
        return taskDAO.filterTasksByStatus(userId, status);
    }
    public boolean updateTaskStatus( int userId, int taskId,String status)
            throws SQLException{
        if(userId <= 0 || taskId <= 0){
            throw new IllegalArgumentException("User ID and task ID must be positive numbers.");
        }
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Status must be Planned, In Progress, or Completed.");
        }
        return taskDAO.updateTaskStatus(userId,taskId,status);
    }
    public int[] getTaskAnalytics(int userId) throws SQLException {

        if (userId <= 0){
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return taskDAO.getTaskAnalytics(userId);
    }
    public String getUpcomingDeadlineReminders(int userId)
            throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return taskDAO.getUpcomingDeadlineReminders(userId);
    }
    public java.util.List<studymate.model.Task> getTasksForCalendar(int userId)
            throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }
        return taskDAO.getTasksForCalendar(userId);
    }
}
