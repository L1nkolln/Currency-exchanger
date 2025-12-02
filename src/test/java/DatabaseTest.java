import com.petprj.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {

    public static void main(String[] args) {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            String sql = """
                    SELECT ID, Code, FullName, Sigh
                    FROM Currencies
                    """;
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("Список валют");
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String code = resultSet.getString("Code");
                String fullName = resultSet.getString("FullName");
                String sign = resultSet.getString("Sigh");
                System.out.printf("%d: %s - %s (%s)%n", id, code, fullName, sign);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//    public void testCurrencies() {
//        try (Connection connection = DatabaseManager.getConnection();
//            Statement statement = connection.createStatement()) {
//
//            String sql = """
//                    SELECT ID, Code, FullName, Sigh
//                    FROM Currencies
//                    """;
//            ResultSet resultSet = statement.executeQuery(sql);
//
//            System.out.println("Список валют");
//            while (resultSet.next()) {
//                int id = resultSet.getInt("ID");
//                String code = resultSet.getString("Code");
//                String fullName = resultSet.getString("FullName");
//                String sign = resultSet.getString("Sigh");
//                System.out.printf("%d: %s - %s (%s)%s", id, code, fullName, sign);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
