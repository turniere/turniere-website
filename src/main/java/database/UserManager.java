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

    public static void register(String username, String email, String password){

        Session session = getSession();
        Transaction transaction = session.getTransaction();
        User user = new User(username, email, password);

        session.save(user);
        if (transaction.getStatus().equals(TransactionStatus.ACTIVE)) {
            System.out.println("Committing transaction");
            transaction.commit();
        }

    }
}
