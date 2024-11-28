import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    private Connection connection;

    public DatabaseOperations() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch column names dynamically
    public String[] fetchTableColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " LIMIT 1"; // Fetch only the first row
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columnNames.toArray(new String[0]);
    }

    // Other methods like fetchRecords, insertRecord, deleteRecord
    public ResultSet fetchRecords(String tableName) {
        String query = "SELECT * FROM " + tableName;
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertRecord(String tableName, String[] values) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            placeholders.append("?");
            if (i < values.length - 1) placeholders.append(", ");
        }

        String query = "INSERT INTO " + tableName + " VALUES (" + placeholders + ")";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecord(String tableName, String keyColumn, String keyValue) {
        String query = "DELETE FROM " + tableName + " WHERE " + keyColumn + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, keyValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPrimaryKey(String tableName) {
        String query = "SHOW KEYS FROM " + tableName + " WHERE Key_name = 'PRIMARY'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getString("Column_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
