import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class DatabaseManager {
    Connection databaseConnection;
    public DatabaseManager() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        databaseConnection = DriverManager.getConnection("jdbc:sqlite:/home/jseydel/workspace/java/turnie.re/database.sqlite");
    }

    public boolean verifyUserCredentials(String email, String password) throws SQLException {
        final String statement = "SELECT password from users where email=?";
        PreparedStatement ps = databaseConnection.prepareStatement(statement);
        ps.setString(1, email);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            String passwordFromDatabase = result.getString("password");
            return passwordFromDatabase.equals(password);
        }
        return false;
    }
}
