
package studymate.gui;

import studymate.service.ResourceService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ResourceFrame extends JFrame {
    private final int userId;
    private final ResourceService resourceService = new ResourceService();

    private final Color background = new Color(0xE1E3E8);
    private final Color panelColor = new Color(0xF1F2F5);
    private final Color accent = new Color(0x4055B5);
    private final Color borderColor = new Color(0xB9BFCC);

    private final JTextField titleField = new JTextField(20);
    private final JTextField urlField = new JTextField(20);
    private final JTextField searchField = new JTextField(15);
    private final JTextArea descriptionArea = new JTextArea(3, 20);
    private final DefaultTableModel tableModel =new DefaultTableModel(new String[]
    {"No.", "Title", "URL","Description", "Resource ID"}, 0)
    {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable resourceTable = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    private final JLabel countLabel = new JLabel("Resources: 0");

    public ResourceFrame(int userId){

        this.userId = userId;
        setTitle("StudyHub - Resources");
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

        loadResources();
    }

    
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(panelColor);

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)
                )
        );

        JLabel title = new JLabel("Study Resources");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(accent);

        JLabel subtitle = new JLabel("Save, search, and access your study materials");
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

    
    private JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBackground(background);

        main.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        main.add(createEntryPanel(), BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);
        return main;
    }

    
    private JPanel createEntryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelColor);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        addFormItem(panel, gbc, 0, 0, "Title", titleField);
        addFormItem(panel, gbc, 1, 0, "URL", urlField);

        addFormItem(panel,gbc,0,1,"Description",new JScrollPane(descriptionArea));

        JButton addButton = createButton("Add Resource");

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        panel.add(addButton, gbc);
        addButton.addActionListener(e -> addResource());
        return panel;
    }

    private void addFormItem(
            JPanel panel,
            GridBagConstraints gbc,
            int column,
            int row,
            String label,
            Component field) {

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JPanel item = new JPanel();
        item.setOpaque(false);
        item.setLayout( new BoxLayout(item, BoxLayout.Y_AXIS));

        JLabel text = new JLabel(label);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        item.add(text);
        item.add(Box.createVerticalStrut(4));
        item.add(field);
        panel.add(item, gbc);
    }

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(6, 8));
        panel.setBackground(panelColor);
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );

        JPanel searchPanel = new JPanel( new FlowLayout(FlowLayout.LEFT, 8, 2));

        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        JButton searchButton = createButton("Apply");
        JButton clearButton = createButton("Clear");

        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        searchButton.addActionListener(e -> searchResources());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
            updateCount();
        });

        resourceTable.setRowSorter(sorter);

        resourceTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        resourceTable.setRowHeight(25);
        resourceTable.setFont( new Font("Segoe UI", Font.PLAIN, 12));

        resourceTable.setBackground(Color.WHITE);
        resourceTable.setGridColor(new Color(0xDDE0E7));
        resourceTable.setShowVerticalLines(false);

        resourceTable.setSelectionBackground(new Color(0xDCE3FA));
        resourceTable.setSelectionForeground(Color.BLACK);
        resourceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resourceTable.getTableHeader().setBackground(new Color(0xD4D9E5));
        resourceTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(resourceTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel label = new JLabel("Your Saved Resources");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        top.add(label, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
   
    private JPanel createFooter() {

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(background);

        footer.setBorder(BorderFactory.createEmptyBorder(2, 12, 10, 12));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 2) );

        buttons.setOpaque(false);

        JButton openButton = createButton("Open Resource");
        JButton deleteButton = createButton("Delete");
        JButton refreshButton = createButton("Refresh");
        JButton backButton = createButton("Back");

        openButton.addActionListener(e -> openResource());
        deleteButton.addActionListener(e -> deleteResource());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
            loadResources();
        });

        backButton.addActionListener(e -> dispose());

        buttons.add(openButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);
        buttons.add(backButton);

        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        footer.add(buttons, BorderLayout.WEST);
        footer.add(countLabel, BorderLayout.EAST);

        return footer;
    }

    
    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    
    private void configureTable() {

        resourceTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        resourceTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        resourceTable.getColumnModel().getColumn(2).setPreferredWidth(280);
        resourceTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        hideColumn(4);
    }

    private void hideColumn(int columnIndex) {

        resourceTable.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        resourceTable.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        resourceTable.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
    }

    private void configureActions() {
        searchField.addActionListener(e -> searchResources());
    }

    private void addResource() {
        try {
            boolean added = resourceService.addResource(
                    userId,
                    titleField.getText(),
                    urlField.getText(),
                    descriptionArea.getText()
            );
            if (added) {
                JOptionPane.showMessageDialog(this,"Resource is added successfully!");
                titleField.setText("");
                urlField.setText("");
                descriptionArea.setText("");
                searchField.setText("");
                sorter.setRowFilter(null);
                loadResources();
            } else {
                JOptionPane.showMessageDialog(this,"Resource could not be added.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog( this,e.getMessage(), "Invalid Input",JOptionPane.WARNING_MESSAGE );
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void loadResources() {
        try {
            List<Object[]> resources = resourceService.getResources(userId);
            tableModel.setRowCount(0);
            for (Object[] resource : resources) {
                int resourceId = Integer.parseInt(resource[0].toString());
                int serialNumber = tableModel.getRowCount() + 1;

                tableModel.addRow(new Object[]{
                    serialNumber,
                    resource[1],
                    resource[2],
                    resource[3],
                    resourceId
                });
            }
            updateCount();
        } catch (SQLException e) {showDatabaseError(e);
        }
    }

    private void updateCount() {
        countLabel.setText(
                "Showing " + resourceTable.getRowCount()
                + " of " + tableModel.getRowCount()
                + " resources"
        );
    }
   
    private void searchResources() {

        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter("(?i)" + Pattern.quote(keyword),1, 2, 3)
            );
        }
        updateCount();
    }

    
    private void openResource() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Please select a resource first.");
            return;
        }

        int modelRow =resourceTable.convertRowIndexToModel(selectedRow);
        String url = tableModel.getValueAt(modelRow, 2).toString();
        try {
            if (!Desktop.isDesktopSupported()|| !Desktop.getDesktop().isSupported(
                    Desktop.Action.BROWSE)) {
                JOptionPane.showMessageDialog(this,"Opening browser links is not supported.");
                return;
            }
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Could not open the resource. Check the URL.");
        }
    }
    private void deleteResource() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Please select a resource to delete.");
            return;
        }

        int modelRow =resourceTable.convertRowIndexToModel(selectedRow);

        // Actual database Resource ID is in hidden column 4
        int resourceId = Integer.parseInt(tableModel.getValueAt(modelRow, 4).toString());

        String title = tableModel.getValueAt(modelRow, 1).toString();
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + title + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted = resourceService.deleteResource(
                    userId,
                    resourceId
            );
            JOptionPane.showMessageDialog(this,deleted
                            ? "Resource deleted successfully."
                            : "Resource was not found."
            );
            loadResources();
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }
  
    private void showDatabaseError(SQLException e) {
        System.err.println("Resource database error: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
                "A database error occurred. Check the database "
                +"connection and resources table.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
