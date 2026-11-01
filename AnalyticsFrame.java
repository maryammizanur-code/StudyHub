package studymate.gui;

import studymate.service.TaskService;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AnalyticsFrame extends JFrame {

    private static final Color BACKGROUND = new Color(0xE1E3E8);
    private static final Color PANEL = new Color(0xF1F2F5);
    private static final Color ACCENT = new Color(0x4055B5);
    private static final Color BORDER = new Color(0xB9BFCC);
    private static final Color TEXT = new Color(0x303746);

    private final int userId;

    private final TaskService taskService = new TaskService();

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
        setSize(750, 550);
        setMinimumSize(new Dimension(650, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(8, 8));

        root.setBackground(BACKGROUND);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);

        loadAnalytics();
    }

    // Header
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(PANEL);

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 0, BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                12, 16, 12, 16
                        )
                )
        );

        JLabel title = new JLabel("Study Analytics");

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );

        title.setForeground(ACCENT);

        JLabel subtitle = new JLabel(
                "A summary of your academic task progress."
        );

        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        JPanel heading = new JPanel();

        heading.setOpaque(false);

        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );

        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);

        return header;
    }

    // Main panel
    private JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(8, 12));

        main.setBackground(BACKGROUND);

        main.setBorder(
                new EmptyBorder(8, 12, 8, 12)
        );

        // Statistics cards
        JPanel cards = new JPanel(
                new GridLayout(2, 2, 10, 10)
        );

        cards.setOpaque(false);

        cards.add(
                createStatCard("Total Tasks", totalValue)
        );

        cards.add(
                createStatCard("Completed Tasks", completedValue)
        );

        cards.add(
                createStatCard("Pending Tasks", pendingValue)
        );

        cards.add(
                createStatCard("Overdue Tasks", overdueValue)
        );

        // Task progress details
        JPanel details = new JPanel();

        details.setBackground(PANEL);

        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );

        details.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(12, 16, 12, 16)
                )
        );

        JLabel statusHeading = new JLabel(
                "Task Progress Details"
        );

        statusHeading.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        statusHeading.setForeground(ACCENT);

        details.add(statusHeading);

        details.add(Box.createVerticalStrut(10));

        addDetailRow(
                details,
                "Completion Rate:",
                completionRateValue
        );

        addDetailRow(
                details,
                "Planned Tasks:",
                plannedValue
        );

        addDetailRow(
                details,
                "In Progress Tasks:",
                inProgressValue
        );

        main.add(cards, BorderLayout.CENTER);

        main.add(details, BorderLayout.SOUTH);

        return main;
    }

    // Statistics card
    private JPanel createStatCard(
            String title,
            JLabel value) {

        JPanel card = new JPanel();

        card.setBackground(PANEL);

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(12, 16, 12, 16)
                )
        );

        JLabel heading = new JLabel(title);

        heading.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        heading.setForeground(TEXT);

        value.setFont(
                new Font("Segoe UI", Font.BOLD, 28)
        );

        value.setForeground(ACCENT);

        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading);

        card.add(Box.createVerticalStrut(8));

        card.add(value);

        return card;
    }

    // Progress detail rows
    private void addDetailRow(
            JPanel panel,
            String title,
            JLabel value) {

        JPanel row = new JPanel(new BorderLayout());

        row.setOpaque(false);

        JLabel label = new JLabel(title);

        label.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        label.setForeground(TEXT);

        value.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        value.setForeground(ACCENT);

        row.add(label, BorderLayout.WEST);

        row.add(value, BorderLayout.EAST);

        row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 25)
        );

        panel.add(row);

        panel.add(Box.createVerticalStrut(3));
    }

    // Footer
    private JPanel createFooter() {

        JPanel footer = new JPanel(new BorderLayout());

        footer.setBackground(BACKGROUND);

        footer.setBorder(
                new EmptyBorder(2, 12, 10, 12)
        );

        JLabel note = new JLabel(
                "StudyHub | Your personal academic organizer"
        );

        note.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        JButton refreshButton = createButton("Refresh");

        JButton closeButton = createButton("Back");

        refreshButton.addActionListener(
                e -> loadAnalytics()
        );

        closeButton.addActionListener(
                e -> dispose()
        );

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 7, 2)
        );

        buttons.setOpaque(false);

        buttons.add(refreshButton);

        buttons.add(closeButton);

        footer.add(note, BorderLayout.WEST);

        footer.add(buttons, BorderLayout.EAST);

        return footer;
    }

    // Button styling
    private static JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // Load analytics
    private void loadAnalytics() {

        try {

            int[] data = taskService.getTaskAnalytics(userId);

            int total = data[0];

            int completed = data[1];

            totalValue.setText(
                    String.valueOf(total)
            );

            completedValue.setText(
                    String.valueOf(completed)
            );

            pendingValue.setText(
                    String.valueOf(data[2])
            );

            overdueValue.setText(
                    String.valueOf(data[3])
            );

            plannedValue.setText(
                    String.valueOf(data[4])
            );

            inProgressValue.setText(
                    String.valueOf(data[5])
            );

            double rate = total == 0
                    ? 0
                    : (completed * 100.0) / total;

            completionRateValue.setText(
                    String.format("%.1f%%", rate)
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load study analytics. "
                    + "Check your database connection.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            System.err.println(e.getMessage());
        }
    }
}