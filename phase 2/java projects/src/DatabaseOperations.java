import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    private Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertRecord(String tableName, Object[] values) {
        String query = "INSERT INTO " + tableName + " VALUES (" + "?,".repeat(values.length).replaceAll(",$", "") + ")";
        try (Connection con = connect(); PreparedStatement stmt = con.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
            stmt.executeUpdate();
            System.out.println("Record inserted into table: " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object[][] fetchTableData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        try (Connection con = connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<Object[]> data = new ArrayList<>();
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                data.add(row);
            }

            Object[][] dataArray = new Object[data.size()][columnCount];
            return data.toArray(dataArray);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] fetchTableColumnNames(String tableName) {
        String query = "SELECT * FROM " + tableName + " LIMIT 1";
        try (Connection con = connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }
            return columnNames;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteRecord(String tableName, String columnName, Object value) {
        String query = "DELETE FROM " + tableName + " WHERE " + columnName + " = ?";
        try (Connection con = connect(); PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setObject(1, value);
            int rowsAffected = stmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted from " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
