import models.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.*;

public class DatabaseManager {
    Connection databaseConnection;

    public DatabaseManager() throws SQLException, ClassNotFoundException {

        String sqlitePath = "database.sqlite";

        Class.forName("org.sqlite.JDBC");
        databaseConnection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
    }

    public boolean verifyUserCredentials(String email, String password) {
        Session session = HibernateUtils.getSession();
        Query query = session.getNamedQuery("findUserByEmail");
        query.setParameter("email", email);
        User user = (User) query.uniqueResult();
        if (user == null) {
            System.out.println("User doesn't exist");
            return false;
        }
        return user.getPassword().equals(password);
    }

    public boolean doesMailExist(String email) throws SQLException {
        final String statement = "SELECT email FROM users where email=?";
        PreparedStatement ps = databaseConnection.prepareStatement(statement);
        ps.setString(1, email);
        ResultSet result = ps.executeQuery();
        return result.next();
    }

    public void addUser(String firstName, String lastName, String email, String password) throws SQLException {
        final String statement = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = databaseConnection.prepareStatement(statement);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setString(3, email);
        ps.setString(4, password);
        ps.executeUpdate();
    }
}
