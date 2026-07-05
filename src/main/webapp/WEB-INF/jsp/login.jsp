<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion - VolyVary</title>
</head>
<body>
    <h2>Connexion à l'application</h2>
    
    <!-- Message en cas d'erreur de connexion -->
    <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red;">Nom d'utilisateur ou mot de passe incorrect.</p>
    <% } %>

    <!-- L'action doit correspondre au loginProcessingUrl de SecurityConfig -->
    <form action="${pageContext.request.contextPath}/perform_login" method="POST">
        <div>
            <label for="username">Nom d'utilisateur :</label>
            <input type="text" id="username" name="username" required />
        </div>
        <br/>
        <div>
            <label for="password">Mot de passe :</label>
            <input type="password" id="password" name="password" required />
        </div>
        <br/>
        <button type="submit">Se connecter</button>
    </form>
</body>
</html>