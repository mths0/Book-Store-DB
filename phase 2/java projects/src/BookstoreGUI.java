import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class BookstoreGUI {
    private DatabaseOperations dbOps;
    private JComboBox<String> tableSelector;
    private JComboBox<String> operationSelector;
    private JPanel inputPanel;
    private JTable table;

    public BookstoreGUI(DatabaseOperations dbOps) {
        this.dbOps = dbOps;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Bookstore Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel tableLabel = new JLabel("Select Table:");
        tableSelector = new JComboBox<>();
        JLabel operationLabel = new JLabel("Operation:");
        operationSelector = new JComboBox<>(new String[]{"Insert", "Fetch", "Delete", "Update"});
        JButton executeButton = new JButton("Execute");

        topPanel.add(tableLabel);
        topPanel.add(tableSelector);
        topPanel.add(operationLabel);
        topPanel.add(operationSelector);
        topPanel.add(executeButton);

        // Input Panel
        inputPanel = new JPanel(new GridLayout(0, 3)); // Updated for conditional fetch
        inputPanel.setVisible(true);

        // Table Panel
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        loadTables();

        // Event Listeners
        operationSelector.addActionListener(e -> refreshInputFields());
        executeButton.addActionListener(e -> handleOperation());
    }

    private void loadTables() {
        ArrayList<String> tables = dbOps.getTableNames();
        for (String table : tables) {
            tableSelector.addItem(table);
        }
        refreshInputFields();
    }

    private void refreshInputFields() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        String selectedOperation = (String) operationSelector.getSelectedItem();

        inputPanel.removeAll(); // Clear existing input fields
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure components stretch to fill the space

        if (selectedOperation.equals("Insert")) {
            ArrayList<String> columns = dbOps.fetchTableColumnNames(selectedTable);
            ArrayList<Boolean> notNulls = dbOps.fetchNotNullConstraints(selectedTable);

            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                boolean isNotNull = notNulls.get(i);

                JLabel label = new JLabel(column + (isNotNull ? " *" : "") + ":");
                JTextField textField = new JTextField();

                gbc.gridx = 0; // Column 0 for labels
                gbc.gridy = i; // Row for the current element
                gbc.weightx = 0.2; // Allocate less width for the label
                inputPanel.add(label, gbc);

                gbc.gridx = 1; // Column 1 for text fields
                gbc.weightx = 0.8; // Allocate more width for the text field
                inputPanel.add(textField, gbc);
            }
        } else if (selectedOperation.equals("Delete")) {
            ArrayList<String> primaryKeys = dbOps.fetchPrimaryKeyColumns(selectedTable);

            for (int i = 0; i < primaryKeys.size(); i++) {
                String pk = primaryKeys.get(i);

                JLabel label = new JLabel(pk + " (PK):");
                JComboBox<String> comboBox = new JComboBox<>();
                ArrayList<String> pkValues = dbOps.fetchPrimaryKeyValues(selectedTable, pk);

                for (String value : pkValues) {
                    comboBox.addItem(value);
                }

                gbc.gridx = 0; // Column 0 for labels
                gbc.gridy = i; // Row for the current element
                gbc.weightx = 0.2;
                inputPanel.add(label, gbc);

                gbc.gridx = 1; // Column 1 for combo boxes
                gbc.weightx = 0.8;
                inputPanel.add(comboBox, gbc);
            }
        } else if (selectedOperation.equals("Fetch")) {
            JLabel attributeLabel = new JLabel("Attribute:");
            JComboBox<String> attributeSelector = new JComboBox<>();
            ArrayList<String> columns = dbOps.fetchTableColumnNames(selectedTable);

            for (String column : columns) {
                attributeSelector.addItem(column);
            }

            JLabel operatorLabel = new JLabel("Operator:");
            JComboBox<String> operatorSelector = new JComboBox<>(new String[]{">", ">=", "<", "<=", "=", "!="});

            JLabel valueLabel = new JLabel("Value:");
            JTextField valueField = new JTextField();

            gbc.gridx = 0; // Column 0 for labels
            gbc.gridy = 0; // First row
            inputPanel.add(attributeLabel, gbc);

            gbc.gridx = 1; // Column 1 for attribute selector
            inputPanel.add(attributeSelector, gbc);

            gbc.gridy = 1; // Second row
            gbc.gridx = 0; // Reset to Column 0
            inputPanel.add(operatorLabel, gbc);

            gbc.gridx = 1; // Column 1 for operator selector
            inputPanel.add(operatorSelector, gbc);

            gbc.gridy = 2; // Third row
            gbc.gridx = 0; // Reset to Column 0
            inputPanel.add(valueLabel, gbc);

            gbc.gridx = 1; // Column 1 for value field
            inputPanel.add(valueField, gbc);
        } else if (selectedOperation.equals("Update")) {
            ArrayList<String> columns = dbOps.fetchTableColumnNames(selectedTable);
            ArrayList<String> primaryKeys = dbOps.fetchPrimaryKeyColumns(selectedTable);

            int row = 0;

            for (String pk : primaryKeys) {
                JLabel label = new JLabel(pk + " (PK):");
                JComboBox<String> comboBox = new JComboBox<>();
                ArrayList<String> pkValues = dbOps.fetchPrimaryKeyValues(selectedTable, pk);

                for (String value : pkValues) {
                    comboBox.addItem(value);
                }

                gbc.gridx = 0;
                gbc.gridy = row++;
                gbc.weightx = 0.2;
                inputPanel.add(label, gbc);

                gbc.gridx = 1;
                gbc.weightx = 0.8;
                inputPanel.add(comboBox, gbc);
            }

            for (String column : columns) {
                if (!primaryKeys.contains(column)) {
                    JLabel label = new JLabel(column + ":");
                    JTextField textField = new JTextField();

                    gbc.gridx = 0;
                    gbc.gridy = row++;
                    gbc.weightx = 0.2;
                    inputPanel.add(label, gbc);

                    gbc.gridx = 1;
                    gbc.weightx = 0.8;
                    inputPanel.add(textField, gbc);
                }
            }
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }



    private void handleOperation() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        String selectedOperation = (String) operationSelector.getSelectedItem();

        if (selectedOperation.equals("Fetch")) {
            handleFetch(selectedTable);
        } else if (selectedOperation.equals("Insert")) {
            handleInsert(selectedTable);
        } else if (selectedOperation.equals("Delete")) {
            handleDelete(selectedTable);
        } else if (selectedOperation.equals("Update")) {
            handleUpdate(selectedTable);
        }
    }

    private void handleFetch(String selectedTable) {
        String column = "";
        String operator = "";
        String value = "";

        for (int i = 0; i < inputPanel.getComponentCount(); i++) {
            Component component = inputPanel.getComponent(i);
            if (component instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) component;
                if (column.isEmpty()) {
                    column = (String) comboBox.getSelectedItem();
                } else {
                    operator = (String) comboBox.getSelectedItem();
                }
            } else if (component instanceof JTextField) {
                value = ((JTextField) component).getText().trim();
            }
        }

        String condition = "";
        if (!column.isEmpty() && !operator.isEmpty() && !value.isEmpty()) {
            condition = column + " " + operator + " '" + value + "'";
        }

        ResultSet rs;
        if (condition.isEmpty()) {
            rs = dbOps.fetchRecords(selectedTable);
        } else {
            rs = dbOps.fetchRecordsWithCondition(selectedTable, condition);
        }

        if (rs == null) {
            JOptionPane.showMessageDialog(null, "No data available for table: " + selectedTable, "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            DefaultTableModel model = dbOps.buildTableModel(rs);
            table.setModel(model);
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Fetch Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleInsert(String tableName) {
        Component[] components = inputPanel.getComponents();
        ArrayList<String> columns = dbOps.fetchTableColumnNames(tableName);
        String[] values = new String[columns.size()];

        int valueIndex = 0;
        for (Component component : components) {
            if (component instanceof JTextField) {
                values[valueIndex] = ((JTextField) component).getText();
                valueIndex++;
            }
        }

        String result = dbOps.insertRecord(tableName, columns.toArray(new String[0]), values);
        JOptionPane.showMessageDialog(null, result, "Insert Status", JOptionPane.INFORMATION_MESSAGE);
        refreshInputFields();
    }

    private void handleDelete(String tableName) {
        Component[] components = inputPanel.getComponents();
        ArrayList<String> primaryKeys = dbOps.fetchPrimaryKeyColumns(tableName);

        HashMap<String, String> pkValues = new HashMap<>();
        int pkIndex = 0;
        for (Component component : components) {
            if (component instanceof JComboBox && pkIndex < primaryKeys.size()) {
                pkValues.put(primaryKeys.get(pkIndex), (String) ((JComboBox<?>) component).getSelectedItem());
                pkIndex++;
            }
        }

        for (String pk : pkValues.keySet()) {
            String result = dbOps.deleteRecord(tableName, pk, pkValues.get(pk));
            JOptionPane.showMessageDialog(null, result, "Delete Status", JOptionPane.INFORMATION_MESSAGE);
        }

        refreshInputFields();
    }

    private void handleUpdate(String tableName) {
        Component[] components = inputPanel.getComponents();
        ArrayList<String> columns = dbOps.fetchTableColumnNames(tableName);
        ArrayList<String> primaryKeys = dbOps.fetchPrimaryKeyColumns(tableName);

        HashMap<String, String> pkValues = new HashMap<>();
        HashMap<String, String> columnValues = new HashMap<>();

        int columnIndex = 0;
        for (Component component : components) {
            if (component instanceof JTextField) {
                String value = ((JTextField) component).getText().trim();
                String column = columns.get(columnIndex);

                if (!value.isEmpty()) {
                    if (primaryKeys.contains(column)) {
                        pkValues.put(column, value); // Add to primary key map
                    } else {
                        columnValues.put(column, value); // Add to update fields map
                    }
                }
                columnIndex++;
            } else if (component instanceof JComboBox) {
                String value = (String) ((JComboBox<?>) component).getSelectedItem();
                String column = columns.get(columnIndex);

                if (value != null && !value.isEmpty()) {
                    if (primaryKeys.contains(column)) {
                        pkValues.put(column, value); // Add to primary key map
                    } else {
                        columnValues.put(column, value); // Add to update fields map
                    }
                }
                columnIndex++;
            }
        }

        // Validate primary keys
        if (pkValues.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Primary key values are required for the update operation.", "Update Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensure at least one field is provided for update
        if (columnValues.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No fields provided for updating. Please enter at least one value to update.", "Update Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Perform the update operation
        String result = dbOps.updateRecord(tableName, pkValues, columnValues);
        JOptionPane.showMessageDialog(null, result, "Update Status", JOptionPane.INFORMATION_MESSAGE);

        refreshInputFields(); // Refresh the input fields
    }







}
