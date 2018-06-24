package database;

import database.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

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

    public static void register(String username, String email, String password) {
        Session session = getSession();
        // start transaction
        Transaction transaction = session.beginTransaction();
        // construct new user object
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        // finish transaction
        session.save(user);
        if (transaction.getStatus().equals(TransactionStatus.ACTIVE)) {
            transaction.commit();
        }
    }
}
