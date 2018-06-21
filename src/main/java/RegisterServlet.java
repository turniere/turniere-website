import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String repassword = req.getParameter("repassword");
        String prename = req.getParameter("prename");
        String lastname = req.getParameter("lastname");
        try {

            DatabaseManager dbmgr = new DatabaseManager();
            if (password == repassword) {
                if (!dbmgr.doesMailExist(email)){
                    dbmgr.addUser(email,password,prename,lastname);
                    //redirect to my account page
                }else{
                    //redirect to same page with account existing warning
                }
            } else {
                //redirect to same page with password not matching warning
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
