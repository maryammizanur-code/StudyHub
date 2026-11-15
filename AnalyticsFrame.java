package studymate.gui;

import studymate.service.TaskService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class AnalyticsFrame extends JFrame {
    private final int userId;
    private final TaskService taskService = new TaskService();
    private final Color background = new Color(0xE1E3E8);
    private final Color panelColor = new Color(0xF1F2F5);
    private final Color accent = new Color(0x4055B5);
    private final Color borderColor = new Color(0xB9BFCC);
    private final Color textColor = new Color(0x303746);
    private final JLabel totalValue = new JLabel("0");
    private final JLabel completedValue = new JLabel("0");
    private final JLabel pendingValue = new JLabel("0");
    private final JLabel overdueValue = new JLabel("0");
    private final JLabel completionRateValue = new JLabel("0%");
    private final JLabel plannedValue = new JLabel("0");
    private final JLabel inProgressValue = new JLabel("0");
    public AnalyticsFrame(int userId) {
        this.userId = userId;
        setTitle("StudyHub - Study Analytics");
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(background);
        setLayout(new BorderLayout(8, 8));
        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        loadAnalytics();
    }
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelColor);
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel title = new JLabel("Study Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(accent);
        JLabel subtitle = new JLabel("A summary of your academic task progress.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading,BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);
        header.add(heading, BorderLayout.WEST);
        return header;
    }
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBackground(background);
        main.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JPanel cards = new JPanel(new GridLayout(2, 2, 10, 10));
        cards.setOpaque(false);
        cards.add(createStatCard("Total Tasks", totalValue));
        cards.add(createStatCard("Completed Tasks",completedValue));
        cards.add(createStatCard("Pending Tasks",pendingValue));
        cards.add(createStatCard("Overdue Tasks", overdueValue));
        JPanel details = new JPanel();
        details.setBackground(panelColor);
        details.setLayout(new BoxLayout(details,BoxLayout.Y_AXIS));
        details.setBorder( BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor),new EmptyBorder(12, 16, 12, 16)));
        JLabel heading = new JLabel("Task Progress Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13 ));
        heading.setForeground(accent);
        details.add(heading);
        details.add(Box.createVerticalStrut(10));
        addDetailRow(details,"Completion Rate:",completionRateValue);
        addDetailRow(details, "Planned Tasks:",plannedValue);
        addDetailRow(details, "In Progress Tasks:",inProgressValue);
        main.add(cards, BorderLayout.CENTER);
        main.add(details, BorderLayout.SOUTH);
        return main;
    }
    private JPanel createStatCard(String title,JLabel value)
    {
        JPanel card = new JPanel();
        card.setBackground(panelColor);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor),new EmptyBorder(12, 16, 12, 16)));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI",Font.BOLD,13));
        heading.setForeground(textColor);
        value.setFont(new Font("Segoe UI",Font.BOLD,30));
        value.setForeground(accent);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(value);
        return card;
    }
    private void addDetailRow(JPanel panel,String title,JLabel value) 
    {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI",Font.PLAIN,12) );
        label.setForeground(textColor);
        value.setFont(new Font("Segoe UI",Font.BOLD,12));
        value.setForeground(accent);
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        row.setMaximumSize( new Dimension(Integer.MAX_VALUE,25));
        panel.add(row);
        panel.add(Box.createVerticalStrut(3));
    }
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(background);
        footer.setBorder(BorderFactory.createEmptyBorder( 2, 12, 10, 12));
        JLabel note = new JLabel("StudyHub | Your personal academic organizer");
        note.setFont( new Font("Segoe UI",Font.PLAIN,11 ));
        JButton refreshButton = createButton("Refresh");
        JButton backButton = createButton("Back");
        refreshButton.addActionListener( e -> loadAnalytics() );
        backButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT,7,2));
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(backButton);
        footer.add(note, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.EAST);
        return footer;
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont( new Font("Segoe UI",Font.PLAIN,12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    private void loadAnalytics(){
        try{
            int[] data =taskService.getTaskAnalytics(userId);
            int total = data[0];
            int completed = data[1];
            totalValue.setText(String.valueOf(total));
            completedValue.setText(String.valueOf(completed) );
            pendingValue.setText(String.valueOf(data[2]) );
            overdueValue.setText(String.valueOf(data[3]));
            plannedValue.setText(String.valueOf(data[4]));
            inProgressValue.setText(String.valueOf(data[5]));
            double rate =total == 0
                            ? 0: (completed * 100.0) / total;
            completionRateValue.setText(String.format("%.1f%%", rate));
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(this,"Could not load study analytics.\nCheck your database connection.","Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
