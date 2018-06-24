package servlets;

import database.UserManager;

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

        if (UserManager.verifyCredentials(email, password)) {
            System.out.println("Auth successful");
            req.getSession().setAttribute("email", email);
            req.getServletContext().getRequestDispatcher("/authenticated.jsp").forward(req, resp);
        } else {
            System.out.println("Wrong or missing Credentials");
        }
    }
}
