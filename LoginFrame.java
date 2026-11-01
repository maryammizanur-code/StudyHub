package studymate.gui;

import studymate.service.UserService;

import java.awt.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private static final Color BACKGROUND = new Color(0xE1E3E8);
    private static final Color PANEL = new Color(0xF1F2F5);
    private static final Color ACCENT = new Color(0x4055B5);
    private static final Color BORDER = new Color(0xB9BFCC);

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField =
            new JPasswordField(20);

    private final UserService userService = new UserService();

    public LoginFrame() {

        setTitle("StudyHub - Login");
        setSize(430, 330);
        setMinimumSize(new Dimension(400, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(
                new BoxLayout(header, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("Login to StudyHub");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Welcome back! Access your study space."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        main.add(header, BorderLayout.NORTH);

        // Login form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL);

        form.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormItem(form, gbc, 0, "Email", emailField);
        addFormItem(form, gbc, 1, "Password", passwordField);

        JButton registerButton = createButton("Create Account");
        JButton loginButton = createButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;

        form.add(registerButton, gbc);

        gbc.gridx = 1;

        form.add(loginButton, gbc);

        main.add(form, BorderLayout.CENTER);

        setContentPane(main);

        getRootPane().setDefaultButton(loginButton);

        loginButton.addActionListener(e -> login());

        registerButton.addActionListener(e ->
                new RegisterFrame().setVisible(true)
        );
    }

    private void addFormItem(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        JLabel text = new JLabel(label);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        panel.add(text, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(field, gbc);
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void login() {

        char[] password = passwordField.getPassword();

        try {

            boolean authenticated = userService.loginUser(
                    emailField.getText(),
                    password
            );

            if (authenticated) {

                String email = emailField.getText().trim();

                int userId =
                        userService.getUserIdByEmail(email);

                if (userId <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Could not identify your account."
                    );

                    return;
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful! Welcome to StudyHub."
                );

                new DashboardFrame(userId).setVisible(true);

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid email or password.",
                        "Login Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error. Check that MySQL is running."
            );

            System.err.println(e.getMessage());

        } catch (GeneralSecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not verify the password."
            );

            System.err.println(e.getMessage());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login could not be completed."
            );

            System.err.println(e.getMessage());

        } finally {

            Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}