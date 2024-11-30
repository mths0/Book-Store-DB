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
        operationSelector = new JComboBox<>(new String[]{"Insert", "Fetch", "Delete"});
        JButton executeButton = new JButton("Execute");

        topPanel.add(tableLabel);
        topPanel.add(tableSelector);
        topPanel.add(operationLabel);
        topPanel.add(operationSelector);
        topPanel.add(executeButton);

        // Input Panel
        inputPanel = new JPanel(new GridLayout(0, 2));
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

        if (selectedOperation.equals("Insert")) {
            // Generate text fields for all columns during insertion
            ArrayList<String> columns = dbOps.fetchTableColumnNames(selectedTable);
            ArrayList<Boolean> notNulls = dbOps.fetchNotNullConstraints(selectedTable);

            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                boolean isNotNull = notNulls.get(i);

                JLabel label = new JLabel(column + (isNotNull ? " *" : "") + ":");
                JTextField textField = new JTextField();

                inputPanel.add(label);
                inputPanel.add(textField);
            }
        } else if (selectedOperation.equals("Delete")) {
            // Generate combo boxes for primary keys during deletion
            ArrayList<String> primaryKeys = dbOps.fetchPrimaryKeyColumns(selectedTable);

            for (String pk : primaryKeys) {
                JLabel label = new JLabel(pk + " (PK):");
                JComboBox<String> comboBox = new JComboBox<>();
                ArrayList<String> pkValues = dbOps.fetchPrimaryKeyValues(selectedTable, pk);

                for (String value : pkValues) {
                    comboBox.addItem(value);
                }
                inputPanel.add(label);
                inputPanel.add(comboBox);
            }
        } else if (selectedOperation.equals("Fetch")) {
            // Add input fields for conditional fetching
            JLabel conditionLabel = new JLabel("Condition (optional):");

            // ComboBox for attribute (column names)
            JComboBox<String> attributeSelector = new JComboBox<>();
            ArrayList<String> columns = dbOps.fetchTableColumnNames(selectedTable);
            for (String column : columns) {
                attributeSelector.addItem(column);
            }

            // ComboBox for comparison operators
            JComboBox<String> operatorSelector = new JComboBox<>(new String[]{">", ">=", "<", "<=", "=", "!="});

            // TextField for input value
            JTextField valueField = new JTextField();

            inputPanel.add(conditionLabel);
            inputPanel.add(new JLabel()); // Empty label for alignment
            inputPanel.add(new JLabel("Attribute:"));
            inputPanel.add(attributeSelector);
            inputPanel.add(new JLabel("Operator:"));
            inputPanel.add(operatorSelector);
            inputPanel.add(new JLabel("Value:"));
            inputPanel.add(valueField);

            // Store components for handling fetch
            inputPanel.putClientProperty("attributeSelector", attributeSelector);
            inputPanel.putClientProperty("operatorSelector", operatorSelector);
            inputPanel.putClientProperty("valueField", valueField);
        }

        // Refresh the input panel
        inputPanel.revalidate();
        inputPanel.repaint();
    }




    private void handleOperation() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        String selectedOperation = (String) operationSelector.getSelectedItem();

        if (selectedOperation.equals("Fetch")) {
            handleFetch(selectedTable); // Pass the table name
        } else if (selectedOperation.equals("Insert")) {
            handleInsert(selectedTable);
        } else if (selectedOperation.equals("Delete")) {
            handleDelete(selectedTable);
        }
    }


    private void handleFetch(String tableName) {
        // Retrieve components for condition
        JComboBox<String> attributeSelector = (JComboBox<String>) inputPanel.getClientProperty("attributeSelector");
        JComboBox<String> operatorSelector = (JComboBox<String>) inputPanel.getClientProperty("operatorSelector");
        JTextField valueField = (JTextField) inputPanel.getClientProperty("valueField");

        String condition = "";
        if (attributeSelector != null && operatorSelector != null && valueField != null) {
            String attribute = (String) attributeSelector.getSelectedItem();
            String operator = (String) operatorSelector.getSelectedItem();
            String value = valueField.getText().trim();

            if (!value.isEmpty()) {
                condition = attribute + " " + operator + " '" + value + "'";
            }
        }

        ResultSet rs;
        if (condition.isEmpty()) {
            // Fetch entire table
            rs = dbOps.fetchRecords(tableName);
        } else {
            // Fetch with condition
            rs = dbOps.fetchRecordsWithCondition(tableName, condition);
        }

        if (rs == null) {
            JOptionPane.showMessageDialog(null, "No data available for table: " + tableName, "Info", JOptionPane.INFORMATION_MESSAGE);
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
            } else if (component instanceof JComboBox) {
                values[valueIndex] = (String) ((JComboBox<?>) component).getSelectedItem();
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
}
