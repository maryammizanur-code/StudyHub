package studymate.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private static final Color BACKGROUND = new Color(0xE1E3E8);
    private static final Color PANEL = new Color(0xF1F2F5);
    private static final Color ACCENT = new Color(0x4055B5);
    private static final Color BORDER = new Color(0xB9BFCC);
    private static final Color TEXT = new Color(0x303746);
    private static final Color MUTED = new Color(0x77778B);
    private final int userId;
    private DeadlineReminder deadlineReminder;
    public DashboardFrame() {
        this(-1);
    }
    public DashboardFrame(int userId) {
        this.userId = userId;
        setTitle("StudyHub - Dashboard");
        setSize(950, 650);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(8, 8));
        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        if (userId > 0){
            deadlineReminder = new DeadlineReminder(userId, this);
            deadlineReminder.startReminder();
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel title = new JLabel("StudyHub Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT);
        JLabel subtitle = new JLabel("Manage your academic activities in one place");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);
        JButton profileButton = createButton("My Profile");
        profileButton.addActionListener(e -> openProfile());
        header.add(heading, BorderLayout.WEST);
        header.add(profileButton, BorderLayout.EAST);
        return header;
    }
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(8, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 12, 15));
        main.add(createWelcomePanel(), BorderLayout.NORTH);
        main.add(createFeaturePanel(), BorderLayout.CENTER);
        return main;
    }
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel welcome = new JLabel("Welcome to StudyHub!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 17));
        welcome.setForeground(TEXT);
        JLabel description = new JLabel("Stay organized, manage tasks, & monitor study progress." );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(MUTED);
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(welcome);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(description);
        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }
    private JPanel createFeaturePanel() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(BACKGROUND);
        JLabel sectionTitle = new JLabel("Your Workspace");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sectionTitle.setForeground(TEXT);
        container.add(sectionTitle, BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(2, 2, 12, 12));
        cards.setBackground(BACKGROUND);
        cards.add(createCard("01","My Tasks","Add, View, Update, Delete, Search, and Manage Assignments, Exams, & Projects.","Open Tasks",this::openTasks));
        cards.add(createCard("02","Resources","Save, view, and manage useful study links and learning materials.","Open Resources",this::openResources));
        cards.add(createCard("03","Study Analytics","View your total, completed, pending, and overdue tasks.","View Analytics",this::openAnalytics));
        cards.add(createCard("04","Calendar","View your monthly schedule and check task deadlines for each date.", "Open Calendar",this::openCalendar));
        container.add(cards, BorderLayout.CENTER);
        return container;
    }
    private JPanel createCard(String number,String title,String description,String buttonText, Runnable action)
    {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),BorderFactory.createEmptyBorder(15, 12, 15, 12)));
        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        numberLabel.setForeground(ACCENT);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(TEXT);
        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setForeground(MUTED);
        descriptionArea.setBackground(PANEL);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setBorder(null);
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(numberLabel);
        heading.add(Box.createVerticalStrut(8));
        heading.add(titleLabel);
        JButton openButton = createButton(buttonText);
        openButton.addActionListener(e -> action.run());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(openButton);
        card.add(heading, BorderLayout.NORTH);
        card.add(descriptionArea, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BACKGROUND);
        footer.setBorder(BorderFactory.createEmptyBorder(2, 12, 10, 12));
        JLabel footerText = new JLabel("StudyHub | Your personal study workspace");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerText.setForeground(MUTED);
        JButton logoutButton = createButton("Logout");
        logoutButton.addActionListener(e -> logout());
        footer.add(footerText, BorderLayout.WEST);
        footer.add(logoutButton, BorderLayout.EAST);
        return footer;
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    private void openTasks() {
        if (userId <= 0){
            showLoginMessage();
            return;
        }
        new TaskFrame(userId).setVisible(true);
    }
    private void openResources(){
        if (userId <= 0){
            showLoginMessage();
            return;
        }
        new ResourceFrame(userId).setVisible(true);
    }
    private void openAnalytics() {
        if(userId <= 0){
            showLoginMessage();
            return;
        }
        new AnalyticsFrame(userId).setVisible(true);
    }
    private void openCalendar(){
        if(userId <= 0){
            showLoginMessage();
            return;
        }
        new CalendarFrame(userId).setVisible(true);
    }
    private void openProfile(){
        if (userId <= 0){
            showLoginMessage();
            return;
        }
        new ProfileFrame(userId).setVisible(true);
    }
    private void showLoginMessage(){
        JOptionPane.showMessageDialog(this,"Please log in first to access this feature.","Login Required",JOptionPane.INFORMATION_MESSAGE);
    }
    private void logout(){
        if (deadlineReminder != null) {
            deadlineReminder.stopReminder();
        }
        new LoginFrame().setVisible(true);
        dispose();
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {new DashboardFrame().setVisible(true);});
    }
}
