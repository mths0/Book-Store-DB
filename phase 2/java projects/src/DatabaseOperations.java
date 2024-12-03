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
    public ArrayList<String> fetchCorrespondingForeignKeyValues(String tableName, String firstForeignKey, String secondForeignKey, String firstForeignKeyValue) {
        ArrayList<String> values = new ArrayList<>();
        String query = "SELECT DISTINCT " + secondForeignKey + " FROM " + tableName + " WHERE " + firstForeignKey + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, firstForeignKeyValue);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }


    public ArrayList<String> fetchPrimaryKeyColumns(String tableName) {
        ArrayList<String> primaryKeyColumns = new ArrayList<>();
        System.out.println("Fetching primary keys for table: " + tableName);

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

    public ArrayList<String> fetchDependentPrimaryKeyValues(String tableName, String dependentColumn, String keyColumn, String keyValue) {
        ArrayList<String> values = new ArrayList<>();
        String query = "SELECT DISTINCT " + dependentColumn + " FROM " + tableName + " WHERE " + keyColumn + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, keyValue);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                values.add(rs.getString(1));
            }
            rs.close();
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


    public ResultSet selectRecords(String tableName) {
        try {
            String query = "SELECT * FROM `" + tableName + "`"; // Enclose table name in backticks
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
        } catch (SQLIntegrityConstraintViolationException e) {
            return "Constraint Violation: " + e.getMessage(); // Return a detailed error message.
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }


    public String deleteRecord(String tableName, HashMap<String, String> pkValues) {
        StringBuilder whereClause = new StringBuilder();
        ArrayList<String> values = new ArrayList<>();

        for (String column : pkValues.keySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(column).append(" = ?");
            values.add(pkValues.get(column));
        }

        String query = "DELETE FROM " + tableName + " WHERE " + whereClause;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Record deleted successfully." : "No matching record found.";
        } catch (SQLException e) {
            return "Error deleting record: " + e.getMessage();
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
            String query = "SELECT * FROM `" + tableName + "` WHERE " + condition; // Enclose table name in backticks
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String updateRecord(String tableName, HashMap<String, String> pkValues, HashMap<String, String> columnValues) {
        try {
            // Build SET clause for fields to update
            StringBuilder setClause = new StringBuilder();
            for (String column : columnValues.keySet()) {
                if (setClause.length() > 0) setClause.append(", ");
                setClause.append(column).append(" = ?");
            }

            // Build WHERE clause for primary keys
            StringBuilder whereClause = new StringBuilder();
            for (String pkColumn : pkValues.keySet()) {
                if (whereClause.length() > 0) whereClause.append(" AND ");
                whereClause.append(pkColumn).append(" = ?");
            }

            // Combine clauses into the final query
            String query = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
            PreparedStatement stmt = connection.prepareStatement(query);

            // Bind values for SET clause
            int paramIndex = 1;
            for (String value : columnValues.values()) {
                stmt.setString(paramIndex++, value);
            }

            // Bind values for WHERE clause
            for (String value : pkValues.values()) {
                stmt.setString(paramIndex++, value);
            }

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {
                return "Record updated successfully.";
            } else {
                return "No matching record found for the update.";
            }
        } catch (SQLException e) {
            return "Error updating record: " + e.getMessage();
        }
    }

    public ArrayList<String> fetchDependentKeyValues(String tableName, String keyColumn, String keyValue, String dependentColumn) {
        ArrayList<String> dependentValues = new ArrayList<>();
        String query = "SELECT DISTINCT " + dependentColumn + " FROM " + tableName + " WHERE " + keyColumn + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, keyValue);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dependentValues.add(rs.getString(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dependentValues;
    }


    public String updateOrderTotalPrice() {
        try {
            String query = """
            UPDATE orders o
            SET o.total_price = (
                SELECT SUM(b.price * ohb.quantity)
                FROM order_has_book ohb
                JOIN book b ON ohb.Book_id1 = b.book_id
                WHERE ohb.Order_id1 = o.order_id
            );
        """;

            PreparedStatement stmt = connection.prepareStatement(query);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return "Order total prices updated successfully for " + rowsAffected + " orders.";
        } catch (SQLException e) {
            return "Error updating total prices: " + e.getMessage();
        }
    }



}

