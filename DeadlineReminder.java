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
    private String lastReminderText = "";
    public DeadlineReminder(int userId, JFrame parent) {
        this.userId = userId;
        this.parent = parent;
    }
    public synchronized void startReminder() {
        if (running || userId <= 0){
            return;
        }
        running = true;
        reminderThread = new Thread(this,"StudyHub-Deadline-Reminder" );
        reminderThread.setDaemon(true);
        reminderThread.start();
    }
    public synchronized void stopReminder() {
        running = false;
        if (reminderThread != null)
        {
            reminderThread.interrupt();
        }
    }
    @Override
    public void run() {
        while (running) {
            try {
                checkDeadlines();
                Thread.sleep(60000);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    private void checkDeadlines() {
        try {
            String reminders = taskService.getUpcomingDeadlineReminders(userId);
            if (!reminders.isEmpty() && !reminders.equals(lastReminderText)) {
                lastReminderText = reminders;
                SwingUtilities.invokeLater(()->{
                    if (running)
                    {
                        JOptionPane.showMessageDialog(parent,reminders,"StudyHub - Upcoming Deadlines",JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }else if(reminders.isEmpty()){
                lastReminderText = "";
            }
        }catch(SQLException e) {
            System.err.println("Could not check upcoming deadlines: "+ e.getMessage() );
        }
    }
}
