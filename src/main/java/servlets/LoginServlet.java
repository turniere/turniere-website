package servlets;

import database.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        //check if parameters are present
        if (password == null || email == null) {
            ServletUtils.showErrorPage(req, resp, 400, "E-Mail Adresse oder Passwort fehlen");
            return;
        }

        if (UserManager.verifyCredentials(email, password)) {
            req.getSession().setAttribute("email", email);
            req.getServletContext().getRequestDispatcher("/authenticated.jsp").forward(req, resp);
        } else {
            ServletUtils.showErrorPage(req, resp, 400, "Falsche oder fehlende Anmeldedaten");
        }
    }
}
