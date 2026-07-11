<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Accès refusé - VOLY VARY</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jsp.css">
</head>
<body class="page-erreur">
  <main class="carte-erreur">
    <h1>Accès refusé</h1>
    <p>Votre compte ne possède pas les droits nécessaires pour ouvrir cette page.</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/connexion">Retour</a>
  </main>
</body>
</html>
