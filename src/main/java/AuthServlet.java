import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        try {

            DatabaseManager dbmgr = new DatabaseManager();
            if (dbmgr.verifyUserCredentials(email, password)) {
                System.out.println("Success");
                req.getSession().setAttribute("email", email);
                req.getServletContext().getRequestDispatcher("/authenticated.jsp").forward(req, resp);
            } else {
                System.out.println("Unauthorized");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
