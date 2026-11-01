package studymate.gui;

import studymate.service.UserService;

import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RegisterFrame extends JFrame {

    private static final Color BACKGROUND = new Color(0xE1E3E8);
    private static final Color PANEL = new Color(0xF1F2F5);
    private static final Color ACCENT = new Color(0x4055B5);
    private static final Color BORDER = new Color(0xB9BFCC);

    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField =
            new JPasswordField(20);

    private final UserService userService = new UserService();

    public RegisterFrame() {

        setTitle("StudyHub - Register");
        setSize(500, 410);
        setMinimumSize(new Dimension(430, 360));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(BACKGROUND);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createFormPanel(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
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

        JLabel title = new JLabel(
                "Create Your StudyHub Account"
        );

        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT);

        JLabel subtitle = new JLabel(
                "Register to organize your academic work."
        );

        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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

    // Registration form
    private JPanel createFormPanel() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BACKGROUND);

        outer.setBorder(
                new EmptyBorder(16, 18, 12, 18)
        );

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL);

        form.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(
                                18, 18, 18, 18
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addFormItem(form, gbc, 0, "Full Name", nameField);
        addFormItem(form, gbc, 1, "Email", emailField);
        addFormItem(form, gbc, 2, "Password", passwordField);

        JButton registerButton = createButton("Create Account");

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;

        form.add(registerButton, gbc);

        registerButton.addActionListener(e -> register());

        getRootPane().setDefaultButton(registerButton);

        outer.add(form, BorderLayout.CENTER);

        return outer;
    }

    private void addFormItem(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component field) {

        JLabel text = new JLabel(label);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;

        panel.add(text, gbc);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(field, gbc);
    }

    // Footer
    private JPanel createFooter() {

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 4)
        );

        footer.setBackground(BACKGROUND);

        footer.setBorder(
                new EmptyBorder(0, 12, 10, 12)
        );

        JLabel footerLabel = new JLabel(
                "StudyHub | Your personal academic organizer"
        );

        footerLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        footer.add(footerLabel);

        return footer;
    }

    // Button styling
    private static JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // Registration process
    private void register() {

        char[] password = passwordField.getPassword();

        try {

            boolean registered = userService.registerUser(
                    nameField.getText(),
                    emailField.getText(),
                    password
            );

            if (registered) {

                JOptionPane.showMessageDialog(
                        this,
                        "Account created successfully! You can now log in."
                );

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration failed. Please try again."
                );
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Invalid Information",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {

                JOptionPane.showMessageDialog(
                        this,
                        "This email may already be registered."
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Database error. Check that MySQL is running."
                );

                System.err.println(e.getMessage());
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration could not be completed."
            );

            System.err.println(e.getMessage());

        } finally {

            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }
}