import javax.swing.table.DefaultTableModel; // Add this import
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseOperations {
    private Connection connection;

    public DatabaseOperations() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTableNames() {
        ArrayList<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public ArrayList<String> fetchTableColumnNames(String tableName) {
        ArrayList<String> columns = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, "%");
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    public ArrayList<String> fetchPrimaryKeyColumns(String tableName) {
        ArrayList<String> primaryKeyColumns = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                primaryKeyColumns.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return primaryKeyColumns;
    }

    public ArrayList<String> fetchPrimaryKeyValues(String tableName, String primaryKeyColumn) {
        ArrayList<String> values = new ArrayList<>();
        String query = "SELECT DISTINCT " + primaryKeyColumn + " FROM " + tableName;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }


    public ArrayList<Boolean> fetchNotNullConstraints(String tableName) {
        ArrayList<Boolean> notNulls = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, "%");
            while (rs.next()) {
                int nullable = rs.getInt("NULLABLE");
                notNulls.add(nullable == DatabaseMetaData.columnNoNulls); // True if NOT NULL
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notNulls;
    }

    public ArrayList<String> fetchForeignKeyValues(String relatedTable) {
        ArrayList<String> values = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT * FROM " + relatedTable;
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                values.add(rs.getString(1)); // Assuming the foreign key column is the first column
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }


    public ResultSet fetchRecords(String tableName) {
        try {
            String query = "SELECT * FROM " + tableName;
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String insertRecord(String tableName, String[] columns, String[] values) {
        try {
            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            for (int i = 0; i < columns.length; i++) {
                query.append(columns[i]);
                if (i < columns.length - 1) query.append(", ");
            }
            query.append(") VALUES (");
            for (int i = 0; i < values.length; i++) {
                query.append("?");
                if (i < values.length - 1) query.append(", ");
            }
            query.append(")");

            PreparedStatement stmt = connection.prepareStatement(query.toString());
            for (int i = 0; i < values.length; i++) {
                stmt.setString(i + 1, values[i]);
            }
            int rows = stmt.executeUpdate();
            stmt.close();
            return "Inserted " + rows + " row(s) successfully.";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteRecord(String tableName, String primaryKeyColumn, String primaryKeyValue) {
        try {
            String query = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumn + " = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, primaryKeyValue);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0 ? "Record deleted successfully." : "Record not found.";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteFromPaymentInformation(int orderId, int paymentId) {
        try {
            String query = "DELETE FROM PaymentInformation WHERE order_id = ? AND payment_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, orderId);
            stmt.setInt(2, paymentId);
            int rows = stmt.executeUpdate();
            stmt.close();
            return rows > 0 ? "Record deleted successfully." : "No matching record found.";
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    public DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Get column names
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        // Get data
        ArrayList<Object[]> data = new ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = rs.getObject(i);
            }
            data.add(row);
        }

        // Create table model
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Object[] rowData : data) {
            tableModel.addRow(rowData);
        }

        return tableModel;
    }
    public HashMap<String, String> fetchForeignKeyRelations(String tableName) {
        HashMap<String, String> foreignKeys = new HashMap<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getImportedKeys(null, null, tableName);
            while (rs.next()) {
                String fkColumn = rs.getString("FKCOLUMN_NAME");
                String pkTable = rs.getString("PKTABLE_NAME");
                foreignKeys.put(fkColumn, pkTable);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foreignKeys;
    }
    public ResultSet fetchRecordsWithCondition(String tableName, String condition) {
        try {
            String query = "SELECT * FROM " + tableName + " WHERE " + condition;
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in fetching records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }



}
