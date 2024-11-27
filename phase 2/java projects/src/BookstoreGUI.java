import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookstoreGUI {
    private DatabaseOperations dbOps;

    public BookstoreGUI() {
        dbOps = new DatabaseOperations();
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Bookstore Database");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel tableLabel = new JLabel("Table:");
        tableLabel.setBounds(10, 20, 80, 25);
        panel.add(tableLabel);

        // Combo box for table selection
        String[] tables = {"book", "category", "customer", "order", "paymentinformation", "order_has_book"};
        JComboBox<String> tableComboBox = new JComboBox<>(tables);
        tableComboBox.setBounds(100, 20, 165, 25);
        panel.add(tableComboBox);

        JLabel columnLabel = new JLabel("Column/Value:");
        columnLabel.setBounds(10, 60, 150, 25);
        panel.add(columnLabel);

        JTextField columnField = new JTextField(20);
        columnField.setBounds(100, 60, 300, 25);
        panel.add(columnField);

        JButton insertButton = new JButton("Insert");
        insertButton.setBounds(10, 100, 150, 25);
        panel.add(insertButton);

        JButton fetchButton = new JButton("Fetch");
        fetchButton.setBounds(200, 100, 150, 25);
        panel.add(fetchButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(400, 100, 150, 25);
        panel.add(deleteButton);

        JTextField deleteField = new JTextField(20);
        deleteField.setBounds(100, 140, 165, 25);
        panel.add(deleteField);

        JTextField deleteColumnField = new JTextField(20);
        deleteColumnField.setBounds(300, 140, 165, 25);
        panel.add(deleteColumnField);

        // Action listener for insert
        insertButton.addActionListener(e -> {
            String tableName = (String) tableComboBox.getSelectedItem();
            String[] values = columnField.getText().split(",");
            dbOps.insertRecord(tableName, values);
        });

        // Action listener for fetch
        fetchButton.addActionListener(e -> {
            String tableName = (String) tableComboBox.getSelectedItem();
            manageTable(tableName);
        });

        // Action listener for delete
        deleteButton.addActionListener(e -> {
            String tableName = (String) tableComboBox.getSelectedItem();
            String columnName = deleteColumnField.getText();
            String value = deleteField.getText();
            dbOps.deleteRecord(tableName, columnName, value);
        });
    }

    private void manageTable(String tableName) {
        JFrame manageFrame = new JFrame("Manage Table: " + tableName);
        manageFrame.setSize(800, 400);

        String[] columnNames = dbOps.fetchTableColumnNames(tableName);
        Object[][] tableData = dbOps.fetchTableData(tableName);

        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        JTable table = new JTable(model);

        JScrollPane pane = new JScrollPane(table);
        manageFrame.add(pane);
        manageFrame.setVisible(true);
    }

    public static void main(String[] args) {
        BookstoreGUI app = new BookstoreGUI();
        app.createAndShowGUI();
    }
}
