package database;

import database.models.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserManager extends DatabaseManager {
    public static boolean verifyCredentials(String email, String password) {
        Session session = getSession();
        // fetch corresponding user object
        Query userQuery = session.getNamedQuery("findUserByEmail");
        userQuery.setParameter("email", email);
        User user = (User) userQuery.uniqueResult();
        // verify user exists
        if (user == null) return false;
        // compare passwords
        return user.getPassword().equals(password);
    }
}
