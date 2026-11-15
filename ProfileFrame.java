package studymate.gui;

import studymate.database.UserDAO;
import studymate.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class ProfileFrame extends JFrame {
    private final int userId;
    private final UserService userService;
    private JTextField userIdField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField createdAtField;
    private JButton saveButton;
    private JButton refreshButton;
    private JButton changePasswordButton;
    private JButton closeButton;
    private static final Color BACKGROUND = new Color(0xE1E3E8);
    private static final Color PANEL = new Color(0xF1F2F5);
    private static final Color ACCENT = new Color(0x4055B5);
    public ProfileFrame(int userId){
        this.userId = userId;
        this.userService = new UserService();
        setTitle("StudyHub - User Profile");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        createComponents();
        loadProfile();
    }
    private void createComponents(){
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        JLabel heading = new JLabel("User Profile");
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(ACCENT);
        JLabel subtitle = new JLabel("View and manage account information.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(BACKGROUND);
        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(subtitle);
        mainPanel.add(headingPanel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 12, 15));
        formPanel.setBackground(PANEL);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        userIdField = new JTextField();
        fullNameField = new JTextField();
        emailField = new JTextField();
        createdAtField = new JTextField();
        userIdField.setEditable(false);
        createdAtField.setEditable(false);
        addFormField(formPanel, "User ID:", userIdField);
        addFormField(formPanel, "Full Name:", fullNameField);
        addFormField(formPanel, "Email:", emailField);
        addFormField(formPanel, "Account Created:", createdAtField);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        refreshButton = new JButton("Refresh");
        saveButton = new JButton("Save Changes");
        changePasswordButton = new JButton("Change Password");
        closeButton = new JButton("Close");
        styleButton(refreshButton);
        styleButton(saveButton);
        styleButton(changePasswordButton);
        styleButton(closeButton);
        saveButton.setBackground(ACCENT);
        saveButton.setForeground(Color.WHITE);
        changePasswordButton.setBackground(ACCENT);
        changePasswordButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e->loadProfile());
        saveButton.addActionListener(e->saveProfile());
        changePasswordButton.addActionListener(e->openChangePasswordDialog());
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(refreshButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    private void addFormField(JPanel panel,String labelText,JTextField textField){
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(label);
        panel.add(textField);
    }
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private void loadProfile(){
        try{
            UserDAO.UserProfile profile = userService.getProfile(userId);
            userIdField.setText(String.valueOf(profile.getUserId()));
            fullNameField.setText(profile.getFullName());
            emailField.setText(profile.getEmail());
            createdAtField.setText(profile.getCreatedAt());
        }catch(SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog( this,"Could not load profile: " + e.getMessage(),"Profile Error",JOptionPane.ERROR_MESSAGE );
        }
    }
    private void saveProfile() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        try{
            userService.updateProfile(userId,fullName,email);
            JOptionPane.showMessageDialog(this,"Profile updated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
            loadProfile();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Database error. The email may already be in use.\n"+ e.getMessage(), "Update Error",JOptionPane.ERROR_MESSAGE);
        }catch(IllegalArgumentException e){
            JOptionPane.showMessageDialog(this,e.getMessage(),"Invalid Information",JOptionPane.WARNING_MESSAGE);
        }
    }
    private void openChangePasswordDialog() {
        JDialog dialog = new JDialog(this,"Change Password",true);
        dialog.setSize(420, 330);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel heading = new JLabel("Change Password");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(ACCENT);
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headingPanel.setBackground(BACKGROUND);
        headingPanel.add(heading);
        mainPanel.add(headingPanel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBackground(PANEL);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        addFormField(formPanel,"Current Password:",currentPasswordField);
        addFormField(formPanel,"New Password:",newPasswordField);
        addFormField(formPanel,"Confirm Password:",confirmPasswordField);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        JButton updateButton = new JButton("Update Password");
        JButton cancelButton = new JButton("Cancel");
        styleButton(updateButton);
        styleButton(cancelButton);
        updateButton.setBackground(ACCENT);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e->{
            char[] currentPassword = currentPasswordField.getPassword();
            char[] newPassword = newPasswordField.getPassword();
            char[] confirmPassword =confirmPasswordField.getPassword();
            try{
                userService.changePassword(userId,currentPassword,newPassword,confirmPassword);
                JOptionPane.showMessageDialog(dialog,"Password changed successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(dialog,"Database error: "+ex.getMessage(),"Update Error",JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(dialog,ex.getMessage(),"Invalid Information",JOptionPane.WARNING_MESSAGE);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(dialog,"Could not change password: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }finally{
                Arrays.fill(currentPassword, '\0');
                Arrays.fill(newPassword, '\0');
                Arrays.fill(confirmPassword, '\0');
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            }
        });
        cancelButton.addActionListener(e->dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
