package studymate.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(BACKGROUND);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);

        // Start deadline reminders
        if (userId > 0) {

            deadlineReminder =
                    new DeadlineReminder(userId, this);

            deadlineReminder.startReminder();
        }
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

        JPanel heading = new JPanel();

        heading.setOpaque(false);

        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Dashboard");

        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT);

        JLabel subtitle = new JLabel(
                "Plan your work. Stay organized. Keep moving forward."
        );

        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        JLabel profile = new JLabel(" STUDENT ");

        profile.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        profile.setForeground(ACCENT);
        profile.setBackground(PANEL);
        profile.setOpaque(true);

        profile.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        header.add(heading, BorderLayout.WEST);
        header.add(profile, BorderLayout.EAST);

        return header;
    }

    // Main content
    private JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(8, 14));

        main.setBackground(BACKGROUND);

        main.setBorder(
                new EmptyBorder(8, 12, 8, 12)
        );

        // Welcome panel
        JPanel welcome = new JPanel();

        welcome.setBackground(PANEL);

        welcome.setLayout(
                new BoxLayout(welcome, BoxLayout.Y_AXIS)
        );

        welcome.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(16, 18, 16, 18)
                )
        );

        JLabel welcomeTitle = new JLabel(
                "Welcome to your study space"
        );

        welcomeTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );

        welcomeTitle.setForeground(ACCENT);

        JLabel welcomeSubtitle = new JLabel(
                "Everything you need to manage your academic work."
        );

        welcomeSubtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        welcome.add(welcomeTitle);
        welcome.add(Box.createVerticalStrut(6));
        welcome.add(welcomeSubtitle);

        // Feature section
        JPanel featureArea = new JPanel(
                new BorderLayout(8, 8)
        );

        featureArea.setOpaque(false);

        JLabel sectionTitle = new JLabel("YOUR WORKSPACE");

        sectionTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        sectionTitle.setForeground(MUTED);

        // Feature cards
        JPanel cards = new JPanel(
                new GridLayout(1, 3, 10, 0)
        );

        cards.setOpaque(false);

        JPanel taskCard = createCard(
                "01",
                "My Tasks",
                "Assignments, exams, and projects",
                "Manage your tasks and deadlines."
        );

        JPanel resourceCard = createCard(
                "02",
                "Resources",
                "Your saved study materials",
                "Keep useful links and materials together."
        );

        JPanel analyticsCard = createCard(
                "03",
                "Study Analytics",
                "Your task progress",
                "Review completed, pending, and overdue tasks."
        );

        cards.add(taskCard);
        cards.add(resourceCard);
        cards.add(analyticsCard);

        featureArea.add(sectionTitle, BorderLayout.NORTH);
        featureArea.add(cards, BorderLayout.CENTER);

        // Center layout
        JPanel center = new JPanel();

        center.setOpaque(false);

        center.setLayout(
                new BoxLayout(center, BoxLayout.Y_AXIS)
        );

        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcome.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 95)
        );

        featureArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(welcome);
        center.add(Box.createVerticalStrut(16));
        center.add(featureArea);

        // Feature card actions
        taskCard.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        openTasks();
                    }
                }
        );

        resourceCard.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        openResources();
                    }
                }
        );

        analyticsCard.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        openAnalytics();
                    }
                }
        );

        main.add(center, BorderLayout.CENTER);

        return main;
    }

    // Feature card creation
    private JPanel createCard(
            String number,
            String title,
            String subtitle,
            String description) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(PANEL);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(16, 14, 16, 14)
                )
        );

        card.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        JLabel numberLabel = new JLabel(number);

        numberLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        numberLabel.setForeground(ACCENT);

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );

        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(
                "<html><div style='width:145px'>"
                + subtitle
                + "</div></html>"
        );

        subtitleLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        subtitleLabel.setForeground(ACCENT);

        JLabel descriptionLabel = new JLabel(
                "<html><div style='width:145px'>"
                + description
                + "</div></html>"
        );

        descriptionLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        descriptionLabel.setForeground(MUTED);

        JLabel openLabel = new JLabel("Open  ->");

        openLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        openLabel.setForeground(ACCENT);

        card.add(numberLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(descriptionLabel);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(12));
        card.add(openLabel);

        return card;
    }

    // Footer
    private JPanel createFooter() {

        JPanel footer = new JPanel(new BorderLayout());

        footer.setBackground(BACKGROUND);

        footer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 0, 0, 0, BORDER
                        ),
                        new EmptyBorder(6, 12, 8, 12)
                )
        );

        JLabel footerLabel = new JLabel(
                "StudyHub | Your personal academic organizer"
        );

        footerLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        JButton logoutButton = createButton("Logout");

        logoutButton.addActionListener(e -> logout());

        footer.add(footerLabel, BorderLayout.WEST);
        footer.add(logoutButton, BorderLayout.EAST);

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

    // Open tasks
    private void openTasks() {

        if (userId <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please log in to access your tasks."
            );

            return;
        }

        new TaskFrame(userId).setVisible(true);
    }

    // Open resources
    private void openResources() {

        if (userId <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please log in to access your resources."
            );

            return;
        }

        new ResourceFrame(userId).setVisible(true);
    }

    // Open analytics
    private void openAnalytics() {

        if (userId <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please log in to access your analytics."
            );

            return;
        }

        new AnalyticsFrame(userId).setVisible(true);
    }

    // Logout
    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            if (deadlineReminder != null) {

                deadlineReminder.stopReminder();
            }

            new LoginFrame().setVisible(true);

            dispose();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new DashboardFrame().setVisible(true);
        });
    }
}