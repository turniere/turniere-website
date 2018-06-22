import models.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || password == null) {
            System.out.println("Email/Username or password missing");
            return;
        }
        Session session = HibernateUtils.getSession();
        Query query = session.getNamedQuery("findUserByEmail");
        query.setParameter("email", email);
        User user = (User) query.uniqueResult();
        if (user == null) {
            System.out.println("User doesn't exist");
            return;
        }
        if (user.getPassword().equals(password)) {
            System.out.println("Auth successful");
            req.getSession().setAttribute("email", email);
            req.getServletContext().getRequestDispatcher("/authenticated.jsp").forward(req, resp);
        } else {
            System.out.println("User found, auth not successful");
        }
    }
}
