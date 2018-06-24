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
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        //check if parameters are present
        if (password == null || email == null) {
            System.out.println("Email or password missing");
            return; //TODO return to login page with message to specify both
        }

        if (UserManager.verifyCredentials(email, password)) {
            System.out.println("Auth for " + email + " successful");
            req.getSession().setAttribute("email", email);
            req.getServletContext().getRequestDispatcher("/authenticated.jsp").forward(req, resp);
        } else {
            System.out.println("Wrong or missing Credentials");
            //TODO return to login page with wrong credentials notification
        }
    }
}
