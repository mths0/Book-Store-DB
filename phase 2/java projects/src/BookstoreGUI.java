import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookstoreGUI {
    private JComboBox<String> tableSelector;
    private JPanel attributesPanel;
    private DatabaseOperations dbOps;

    public BookstoreGUI() {
        dbOps = new DatabaseOperations();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Bookstore Management");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        JLabel tableLabel = new JLabel("Select Table:");
        tableLabel.setBounds(10, 10, 100, 25);
        panel.add(tableLabel);

        tableSelector = new JComboBox<>(new String[]{"Book", "Customer", "Order", "PaymentInformation"});
        tableSelector.setBounds(110, 10, 150, 25);
        panel.add(tableSelector);

        JButton insertButton = new JButton("Insert");
        insertButton.setBounds(10, 50, 100, 25);
        panel.add(insertButton);

        JButton fetchButton = new JButton("Fetch");
        fetchButton.setBounds(120, 50, 100, 25);
        panel.add(fetchButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(230, 50, 100, 25);
        panel.add(deleteButton);

        JTextArea resultArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBounds(10, 150, 550, 200);
        panel.add(scrollPane);

        attributesPanel = new JPanel();
        attributesPanel.setBounds(10, 90, 550, 50);
        attributesPanel.setLayout(new GridLayout(2, 6, 5, 5)); // Adjust grid size based on number of attributes
        panel.add(attributesPanel);

        tableSelector.addActionListener(e -> loadAttributesForTable());
        insertButton.addActionListener(e -> insertRecord(resultArea));
        fetchButton.addActionListener(e -> fetchRecords(resultArea));
        deleteButton.addActionListener(e -> deleteRecord(resultArea));

        frame.setVisible(true);
        loadAttributesForTable(); // Load attributes for the default table
    }

    private void loadAttributesForTable() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        attributesPanel.removeAll();

        if (selectedTable != null) {
            String[] attributes = dbOps.fetchTableColumnNames(selectedTable); // Fetch column names dynamically
            for (String attribute : attributes) {
                attributesPanel.add(new JLabel(attribute));
                attributesPanel.add(new JTextField());
            }
        }

        attributesPanel.revalidate();
        attributesPanel.repaint();
    }

    private void insertRecord(JTextArea resultArea) {
        try {
            String selectedTable = (String) tableSelector.getSelectedItem();
            if (selectedTable == null) {
                resultArea.setText("Please select a table!");
                return;
            }

            Component[] components = attributesPanel.getComponents();
            String[] columnValues = new String[components.length / 2];

            for (int i = 0; i < components.length; i += 2) {
                JTextField textField = (JTextField) components[i + 1];
                columnValues[i / 2] = textField.getText().trim();
            }

            dbOps.insertRecord(selectedTable, columnValues);
            resultArea.setText("Record inserted successfully!");
        } catch (Exception e) {
            resultArea.setText("Error inserting record: " + e.getMessage());
        }
    }

    private void fetchRecords(JTextArea resultArea) {
        try {
            String selectedTable = (String) tableSelector.getSelectedItem();
            if (selectedTable == null) {
                resultArea.setText("Please select a table!");
                return;
            }

            ResultSet rs = dbOps.fetchRecords(selectedTable);
            StringBuilder result = new StringBuilder();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Header
            for (int i = 1; i <= columnCount; i++) {
                result.append(metaData.getColumnName(i)).append("\t");
            }
            result.append("\n");

            // Rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getString(i)).append("\t");
                }
                result.append("\n");
            }

            resultArea.setText(result.toString());
        } catch (Exception e) {
            resultArea.setText("Error fetching records: " + e.getMessage());
        }
    }

    private void deleteRecord(JTextArea resultArea) {
        try {
            String selectedTable = (String) tableSelector.getSelectedItem();
            if (selectedTable == null) {
                resultArea.setText("Please select a table!");
                return;
            }

            Component[] components = attributesPanel.getComponents();
            String keyColumn = dbOps.getPrimaryKey(selectedTable);
            String keyValue = "";

            for (int i = 0; i < components.length; i += 2) {
                JLabel label = (JLabel) components[i];
                if (label.getText().equalsIgnoreCase(keyColumn)) {
                    JTextField textField = (JTextField) components[i + 1];
                    keyValue = textField.getText().trim();
                    break;
                }
            }

            if (keyValue.isEmpty()) {
                resultArea.setText("Please enter a value for the primary key!");
                return;
            }

            dbOps.deleteRecord(selectedTable, keyColumn, keyValue);
            resultArea.setText("Record deleted successfully!");
        } catch (Exception e) {
            resultArea.setText("Error deleting record: " + e.getMessage());
        }
    }
}
