package servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletUtils {
    public static void showErrorPage(HttpServletRequest request, HttpServletResponse response, int statusCode, String message) throws ServletException, IOException {
        response.setStatus(statusCode);
        request.setAttribute("message", message);
        request.getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
    }
}
