<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<form method="POST" action="/register">
    <table>
        <tr>
            <td colspan="2">Register at turnie.re</td>
        </tr>
        <tr>
            <td>Vorname</td>
            <td><input type="text" name="prename" /></td>
        </tr>
        <tr>
            <td>Nachname</td>
            <td><input type="text" name="lastname" /></td>
        </tr>
        <tr>
            <td>Email:</td>
            <td><input type="text" name="email" /></td>
        </tr>
        <tr>
            <td>Passwort:</td>
            <td><input type="password" name="password"/></td>
        </tr>
        <tr>
            <td>Passwort wiederholen:</td>   <!--TODO add clientside verification for not matching passwords -->
            <td><input type="password" name="repassword"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="Go" /></td>
        </tr>
    </table>
    <br>
    <br>
    <b><%
        if (request.getParameter("pwdMatchErr") != null) {
            out.println("The Passwords don't match");
        }
    %></b>
</form>
</body>
</html>
