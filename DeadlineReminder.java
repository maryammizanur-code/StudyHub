
package studymate.gui;

import studymate.service.TaskService;

import javax.swing.*;
import java.sql.SQLException;

public class DeadlineReminder implements Runnable {

    private final int userId;
    private final JFrame parent;

    private final TaskService taskService = new TaskService();

    private Thread reminderThread;
    private volatile boolean running = false;

    // Stores the last reminder message to avoid repeated popups
    private String lastReminderText = "";

    public DeadlineReminder(int userId, JFrame parent) {
        this.userId = userId;
        this.parent = parent;
    }

    // Start the background reminder thread
    public synchronized void startReminder() {

        if (running || userId <= 0) {
            return;
        }

        running = true;

        reminderThread = new Thread(
                this,
                "StudyHub-Deadline-Reminder"
        );

        reminderThread.setDaemon(true);
        reminderThread.start();
    }

    // Stop the background reminder thread
    public synchronized void stopReminder() {

        running = false;

        if (reminderThread != null) {
            reminderThread.interrupt();
        }
    }

    // Background checking process
    @Override
    public void run() {

        while (running) {

            try {

                checkDeadlines();

                // Check again after 60 seconds
                Thread.sleep(60000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;

            }
        }
    }

    // Retrieve and display upcoming deadlines
    private void checkDeadlines() {

        try {

            String reminders =
                    taskService.getUpcomingDeadlineReminders(userId);

            if (!reminders.isEmpty()
                    && !reminders.equals(lastReminderText)) {

                lastReminderText = reminders;

                SwingUtilities.invokeLater(() -> {

                    if (running) {

                        JOptionPane.showMessageDialog(
                                parent,
                                reminders,
                                "StudyHub - Upcoming Deadlines",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                });

            } else if (reminders.isEmpty()) {

                lastReminderText = "";
            }

        } catch (SQLException e) {

            System.err.println(
                    "Could not check upcoming deadlines: "
                    + e.getMessage()
            );
        }
    }
}
