package studymate.model;

import java.time.LocalDateTime;

public class Task {

    private int taskId;
    private String title;
    private String description;
    private String taskType;
    private LocalDateTime deadline;
    private String status;

    public Task(
            int taskId,
            String title,
            String description,
            String taskType,
            LocalDateTime deadline,
            String status) {

        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.deadline = deadline;
        this.status = status;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskType() {
        return taskType;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }
}