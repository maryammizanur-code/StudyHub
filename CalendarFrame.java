package studymate.gui;

import studymate.model.Task;
import studymate.service.TaskService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFrame extends JFrame {
    private final int userId;
    private final TaskService taskService = new TaskService();
    private YearMonth currentMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();
    private List<Task> allTasks = new ArrayList<>();
    private final Map<LocalDate, List<Task>> taskMap =new HashMap<>();
    private JPanel calendarPanel;
    private JLabel monthLabel;
    private JLabel selectedDateLabel;
    private JTextArea taskArea;
    private final Color backgroundColor =new Color(225, 227, 232);
    private final Color panelColor =new Color(241, 242, 245);
    private final Color accentColor =new Color(64, 85, 181);
    public CalendarFrame(int userId){
        this.userId = userId;
        setTitle("StudyHub - Calendar");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(backgroundColor);
        createHeader();
        createMainPanel();
        loadTasks();
        showCalendar();
        showTasksForDate(selectedDate);
    }
    private void createHeader(){
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);
        header.setBorder(new EmptyBorder(15, 20, 5, 20));
        JLabel title = new JLabel("Study Calendar");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(accentColor);
        JButton backButton = new JButton("Back to Dashboard");
        styleButton(backButton);
        backButton.addActionListener(e ->
        {
            new DashboardFrame(userId).setVisible(true);
            dispose();
        });
        header.add(title, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }
    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(5, 20, 20, 20));
        JPanel calendarContainer =new JPanel(new BorderLayout(10, 15));
        calendarContainer.setBackground(panelColor);
        calendarContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        JPanel navigationPanel =new JPanel(new BorderLayout());
        navigationPanel.setBackground(panelColor);
        JButton previousButton = new JButton("< Previous");
        JButton nextButton = new JButton("Next >");
        styleButton(previousButton);
        styleButton(nextButton);
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 20));
        monthLabel.setForeground(accentColor);
        previousButton.addActionListener(e ->
        {
            currentMonth = currentMonth.minusMonths(1);
            showCalendar();
        });
        nextButton.addActionListener(e ->
        {
            currentMonth = currentMonth.plusMonths(1);
            showCalendar();
        });
        navigationPanel.add(previousButton, BorderLayout.WEST);
        navigationPanel.add(monthLabel, BorderLayout.CENTER);
        navigationPanel.add(nextButton, BorderLayout.EAST);
        calendarContainer.add(navigationPanel, BorderLayout.NORTH);
        calendarPanel = new JPanel(new GridLayout(0, 7, 8, 8));
        calendarPanel.setBackground(panelColor);
        calendarContainer.add(calendarPanel, BorderLayout.CENTER);
        JPanel detailsPanel =new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(panelColor);
        detailsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel detailsTitle = new JLabel("Tasks for Selected Date");
        detailsTitle.setFont(new Font("Arial", Font.BOLD, 17));
        detailsTitle.setForeground(accentColor);
        selectedDateLabel = new JLabel();
        selectedDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        taskArea = new JTextArea();
        taskArea.setEditable(false);
        taskArea.setFont(new Font("Arial", Font.PLAIN, 14));
        taskArea.setBackground(panelColor);
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(true);
        JScrollPane scrollPane =new JScrollPane(taskArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel detailsHeader =new JPanel(new GridLayout(2, 1, 5, 5));
        detailsHeader.setBackground(panelColor);
        detailsHeader.add(detailsTitle);
        detailsHeader.add(selectedDateLabel);
        detailsPanel.add(detailsHeader, BorderLayout.NORTH);
        detailsPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(calendarContainer, BorderLayout.CENTER);
        mainPanel.add(detailsPanel, BorderLayout.EAST);
        detailsPanel.setPreferredSize(new Dimension(280, 0));
        add(mainPanel, BorderLayout.CENTER);
    }
    private void loadTasks() {
        try{
            allTasks =taskService.getTasksForCalendar(userId);
            taskMap.clear();
            for (Task task : allTasks) {
                LocalDate date =task.getDeadline().toLocalDate();
                taskMap.computeIfAbsent( date, k -> new ArrayList<>()).add(task);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Unable to load calendar tasks:\n" + e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE );
        }
    }
    private void showCalendar() {
        calendarPanel.removeAll();
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy") ));
        String[] days = {"Sunday", "Monday", "Tuesday","Wednesday", "Thursday","Friday", "Saturday"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont( new Font("Arial", Font.BOLD, 13));
            label.setForeground(accentColor);
            calendarPanel.add(label);
        }
        LocalDate firstDay = currentMonth.atDay(1);
        int startDay =firstDay.getDayOfWeek().getValue()%7;
        for (int i = 0; i < startDay; i++){
            calendarPanel.add(new JLabel(""));
        }
        int totalDays =currentMonth.lengthOfMonth();
        for (int day = 1; day <= totalDays; day++) {
            LocalDate date =currentMonth.atDay(day);
            List<Task> dateTasks = taskMap.getOrDefault(date, new ArrayList<>());
            String buttonText = String.valueOf(day);
            if (!dateTasks.isEmpty())
            {
                buttonText = "<html><center>"+ day +"<br>● "+ dateTasks.size()+ " task(s)</center></html>";
            }
            JButton dayButton =new JButton(buttonText);

            dayButton.setFocusPainted(false);
            dayButton.setFont(new Font("Arial", Font.BOLD, 13));

            dayButton.setBackground(Color.WHITE);
            dayButton.setBorder( BorderFactory.createLineBorder(new Color(185, 191, 204)));
            if (date.equals(LocalDate.now())) {
                dayButton.setBackground(new Color(210, 220, 255));
            }
            if (date.equals(selectedDate)) {
                dayButton.setBackground( new Color(190, 200, 245));
                dayButton.setBorder(BorderFactory.createLineBorder(accentColor, 2));
            }
            dayButton.addActionListener(e -> {
                selectedDate = date;
                showCalendar();
                showTasksForDate(date);
            });
            calendarPanel.add(dayButton);
        }
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    private void showTasksForDate(LocalDate date) {
        selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        taskArea.setText("");
        List<Task> tasks =taskMap.getOrDefault(date, new ArrayList<>());
        if (tasks.isEmpty()) {
            taskArea.setText("No tasks scheduled for this date.");
        }else {
            for (Task task : tasks){
                taskArea.append("Title: " + task.getTitle() + "\n");
                taskArea.append("Type: " + task.getTaskType() + "\n");
                taskArea.append("Deadline: "+ task.getDeadline().format(DateTimeFormatter.ofPattern("hh:mm a")) + "\n");
                taskArea.append("Status: " + task.getStatus() + "\n");
                taskArea.append( "------------------------------\n");
            }
        }
    }
    private void styleButton(JButton button) {
    button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
}
}
