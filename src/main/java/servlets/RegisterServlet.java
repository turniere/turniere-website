package servlets;

import database.UserManager;
import org.hibernate.exception.ConstraintViolationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String repassword = req.getParameter("repassword");

        if (password.equals(repassword)) {
            try {
                UserManager.register(username, email, password);
            } catch (ConstraintViolationException e) {
                System.out.println(e.getMessage());
                ServletUtils.showErrorPage(req, resp, 400, "Dieser Nutzername/diese E-Mail Adresse wird bereits verwendet");
            }
            resp.sendRedirect("/login.jsp");
        } else {
            ServletUtils.showErrorPage(req, resp, 400, "Die Passwörter sind nicht gleich");
        }
    }
}
