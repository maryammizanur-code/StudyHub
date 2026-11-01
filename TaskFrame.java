
package studymate.gui;

import studymate.service.TaskService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskFrame extends JFrame {

    private final int userId;
    private final TaskService taskService = new TaskService();

    private final Color background = new Color(0xE1E3E8);
    private final Color panelColor = new Color(0xF1F2F5);
    private final Color accent = new Color(0x4055B5);
    private final Color borderColor = new Color(0xB9BFCC);

    private final JTextField titleField = new JTextField(15);
    private final JTextField descriptionField = new JTextField(12);
    private final JTextField deadlineField = new JTextField(10);
    private final JTextField searchField = new JTextField(15);

    private final JComboBox<String> typeBox =
            new JComboBox<>(new String[]{
                "Assignment", "Exam", "Project"
            });

    private final JComboBox<String> filterBox =
            new JComboBox<>(new String[]{
                "All Types", "Assignment", "Exam", "Project"
            });

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                        "ID", "Title", "Type",
                        "Deadline", "Status", "Description"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable taskTable = new JTable(tableModel);

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(tableModel);

    private final JLabel countLabel = new JLabel("Tasks: 0");

    public TaskFrame(int userId) {
        this.userId = userId;

        setTitle("StudyHub - My Tasks");
        setSize(950, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(background);
        setLayout(new BorderLayout(8, 8));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        configureTable();
        configureActions();

        loadTasks();
    }

    // Header
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelColor);

        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, borderColor
                ),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel title = new JLabel("My Tasks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(accent);

        JLabel subtitle = new JLabel(
                "Manage assignments, exams, and projects"
        );

        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);

        return header;
    }

    // Main area
    private JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBackground(background);

        main.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );

        main.add(createEntryPanel(), BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);

        return main;
    }

    // Task entry form
    private JPanel createEntryPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormItem(panel, gbc, 0, 0, "Title", titleField);
        addFormItem(panel, gbc, 1, 0, "Description", descriptionField);
        addFormItem(panel, gbc, 2, 0, "Deadline (yyyy-MM-dd)", deadlineField);

        addFormItem(panel, gbc, 0, 1, "Type", typeBox);

        JButton addButton = createButton("Add Task");
        addButton.addActionListener(e -> addTask());

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        panel.add(addButton, gbc);

        return panel;
    }

    private void addFormItem(
            JPanel panel,
            GridBagConstraints gbc,
            int column,
            int row,
            String label,
            Component field) {

        gbc.gridwidth = 1;
        gbc.gridx = column;
        gbc.gridy = row;

        JPanel item = new JPanel();
        item.setOpaque(false);
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel text = new JLabel(label);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        item.add(text);
        item.add(Box.createVerticalStrut(4));
        item.add(field);

        panel.add(item, gbc);
    }

    // Task table
    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(6, 8));
        panel.setBackground(panelColor);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 2)
        );

        searchPanel.setOpaque(false);

        searchPanel.add(new JLabel("Search title:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Type:"));
        searchPanel.add(filterBox);

        JButton searchButton = createButton("Apply");
        JButton clearButton = createButton("Clear");

        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        searchButton.addActionListener(e -> applyFilters());

        clearButton.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedIndex(0);
            applyFilters();
        });

        taskTable.setRowSorter(sorter);

        taskTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        taskTable.setRowHeight(25);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taskTable.setBackground(Color.WHITE);
        taskTable.setGridColor(new Color(0xDDE0E7));
        taskTable.setShowVerticalLines(false);

        taskTable.setSelectionBackground(new Color(0xDCE3FA));
        taskTable.setSelectionForeground(Color.BLACK);

        taskTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        taskTable.getTableHeader().setBackground(
                new Color(0xD4D9E5)
        );

        taskTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel label = new JLabel("Your study tasks");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        top.add(label, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Bottom action buttons
    private JPanel createFooter() {

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(background);

        footer.setBorder(
                BorderFactory.createEmptyBorder(2, 12, 10, 12)
        );

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 7, 2)
        );

        buttons.setOpaque(false);

        JButton completeButton = createButton("Mark Completed");
        JButton updateButton = createButton("Update Task");
        JButton deleteButton = createButton("Delete");
        JButton refreshButton = createButton("Refresh");
        JButton backButton = createButton("Back");

        completeButton.addActionListener(e -> completeTask());
        updateButton.addActionListener(e -> updateTask());
        deleteButton.addActionListener(e -> deleteTask());
        refreshButton.addActionListener(e -> loadTasks());
        backButton.addActionListener(e -> dispose());

        buttons.add(completeButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);
        buttons.add(backButton);

        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        footer.add(buttons, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.EAST);

        return footer;
    }

    // Button styling
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // Configure table columns
    private void configureTable() {

        taskTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Hide Description column but preserve its data.
        taskTable.getColumnModel().getColumn(5).setMinWidth(0);
        taskTable.getColumnModel().getColumn(5).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(0);
    }

    private void configureActions() {

        searchField.addActionListener(e -> applyFilters());
        filterBox.addActionListener(e -> applyFilters());
    }

    // Load tasks
    private void loadTasks() {

        try {

            String result = taskService.listTasks(userId);

            tableModel.setRowCount(0);

            if (result != null && !result.equals("No tasks found.")) {

                String[] blocks = result.split("--------------------");

                for (String block : blocks) {

                    Integer id = extractInt(block, "Task ID: ");

                    String title = extractValue(block, "Title: ");
                    String description = extractValue(block, "Description: ");
                    String type = extractValue(block, "Type: ");
                    String deadline = extractValue(block, "Deadline: ");
                    String status = extractValue(block, "Status: ");

                    if (id != null && title != null) {

                        if (deadline != null && deadline.length() >= 10) {
                            deadline = deadline.substring(0, 10);
                        }

                        tableModel.addRow(new Object[]{
                            id,
                            title,
                            type,
                            deadline,
                            status,
                            description
                        });
                    }
                }
            }

            applyFilters();

            countLabel.setText(
                    "Tasks: " + tableModel.getRowCount()
            );

        } catch (SQLException ex) {

            showError("Could not load tasks.", ex);

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid User",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Integer extractInt(String block, String label) {

        String value = extractValue(block, label);

        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String extractValue(String block, String label) {

        Pattern pattern = Pattern.compile(
                "(?m)^" + Pattern.quote(label) + "(.*)$"
        );

        Matcher matcher = pattern.matcher(block);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    // Add task
    private void addTask() {

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String deadline = deadlineField.getText().trim();
        String type = (String) typeBox.getSelectedItem();

        if (title.isEmpty() || deadline.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Title and deadline are required."
            );

            return;
        }

        try {

            LocalDate parsedDate = LocalDate.parse(deadline);

            String serviceDeadline =
                    parsedDate.toString() + " 00:00:00";

            boolean added = taskService.addTask(
                    userId,
                    title,
                    description,
                    type,
                    serviceDeadline
            );

            if (added) {

                JOptionPane.showMessageDialog(
                        this,
                        "Task added successfully."
                );

                titleField.setText("");
                descriptionField.setText("");
                deadlineField.setText("");
                typeBox.setSelectedIndex(0);

                loadTasks();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "The task could not be added."
                );
            }

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter the deadline as yyyy-MM-dd, for example 2026-11-15."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid Task Information",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            showError("Could not add task.", ex);
        }
    }

    // Get selected task ID
    private Integer getSelectedTaskId() {

        int selectedRow = taskTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a task from the table first."
            );

            return null;
        }

        int modelRow = taskTable.convertRowIndexToModel(selectedRow);

        return Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString()
        );
    }

    // Update task
    private void updateTask() {

        Integer taskId = getSelectedTaskId();

        if (taskId == null) {
            return;
        }

        int selectedRow = taskTable.getSelectedRow();
        int modelRow = taskTable.convertRowIndexToModel(selectedRow);

        String oldTitle = tableModel.getValueAt(modelRow, 1).toString();
        String oldType = tableModel.getValueAt(modelRow, 2).toString();
        String oldDeadline = tableModel.getValueAt(modelRow, 3).toString();

        Object descriptionValue = tableModel.getValueAt(modelRow, 5);

        String oldDescription = descriptionValue == null
                ? ""
                : descriptionValue.toString();

        JTextField editTitle = new JTextField(oldTitle, 20);
        JTextField editDescription = new JTextField(oldDescription, 20);
        JTextField editDeadline = new JTextField(oldDeadline, 12);

        JComboBox<String> editType = new JComboBox<>(
                new String[]{
                    "Assignment", "Exam", "Project"
                }
        );

        editType.setSelectedItem(oldType);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));

        panel.add(new JLabel("Title:"));
        panel.add(editTitle);

        panel.add(new JLabel("Description:"));
        panel.add(editDescription);

        panel.add(new JLabel("Type:"));
        panel.add(editType);

        panel.add(new JLabel("Deadline (yyyy-MM-dd):"));
        panel.add(editDeadline);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String title = editTitle.getText().trim();
        String description = editDescription.getText().trim();
        String deadline = editDeadline.getText().trim();
        String type = (String) editType.getSelectedItem();

        if (title.isEmpty() || deadline.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Title and deadline are required."
            );

            return;
        }

        try {

            LocalDate parsedDate = LocalDate.parse(deadline);

            String serviceDeadline =
                    parsedDate.toString() + " 00:00:00";

            boolean updated = taskService.updateTask(
                    userId,
                    taskId,
                    title,
                    description,
                    type,
                    serviceDeadline
            );

            if (updated) {

                JOptionPane.showMessageDialog(
                        this,
                        "Task updated successfully."
                );

                loadTasks();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Task could not be updated."
                );
            }

        } catch (DateTimeParseException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter the deadline as yyyy-MM-dd."
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid Task Information",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            showError("Could not update task.", ex);
        }
    }

    // Mark task completed
    private void completeTask() {

        Integer taskId = getSelectedTaskId();

        if (taskId == null) {
            return;
        }

        try {

            boolean updated = taskService.markTaskDone(
                    userId,
                    taskId
            );

            JOptionPane.showMessageDialog(
                    this,
                    updated
                            ? "Task marked as completed."
                            : "Task not found for your account."
            );

            loadTasks();

        } catch (SQLException ex) {

            showError("Could not update task.", ex);

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }

    // Delete task
    private void deleteTask() {

        Integer taskId = getSelectedTaskId();

        if (taskId == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected task?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted = taskService.deleteTask(
                    userId,
                    taskId
            );

            JOptionPane.showMessageDialog(
                    this,
                    deleted
                            ? "Task deleted successfully."
                            : "Task not found for your account."
            );

            loadTasks();

        } catch (SQLException ex) {

            showError("Could not delete task.", ex);

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage()
            );
        }
    }

    // Search and filter
    private void applyFilters() {

        RowFilter<DefaultTableModel, Object> filter = null;

        try {

            java.util.List<RowFilter<DefaultTableModel, Object>> filters =
                    new java.util.ArrayList<>();

            String keyword = searchField.getText().trim();

            if (!keyword.isEmpty()) {

                filters.add(RowFilter.regexFilter(
                        "(?i)" + Pattern.quote(keyword), 1
                ));
            }

            String selectedType =
                    (String) filterBox.getSelectedItem();

            if (selectedType != null &&
                    !selectedType.equals("All Types")) {

                filters.add(RowFilter.regexFilter(
                        "^" + Pattern.quote(selectedType) + "$", 2
                ));
            }

            if (!filters.isEmpty()) {
                filter = RowFilter.andFilter(filters);
            }

            sorter.setRowFilter(filter);

        } catch (IllegalArgumentException ex) {

            sorter.setRowFilter(null);
        }

        countLabel.setText(
                "Showing " + taskTable.getRowCount()
                        + " of " + tableModel.getRowCount() + " tasks"
        );
    }

    // Database error message
    private void showError(String message, SQLException ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
