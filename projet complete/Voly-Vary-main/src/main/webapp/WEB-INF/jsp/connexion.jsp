<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Connexion - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/jsp.css">
  <link rel="stylesheet" href="/assets/css/connexion.css">
</head>
<body>
  <div class="login-page">
    <section class="login-visual">
      <div class="login-visual-content">
        <svg viewBox="0 0 32 32" fill="none" width="48" height="48" aria-hidden="true">
          <rect width="32" height="32" rx="8" fill="white"/>
          <path d="M9 11l7 12 7-12" stroke="#2563EB" stroke-width="2.5"/>
        </svg>
        <h2>Pilotez votre chaîne de valeur</h2>
        <p>Gérez les livreurs, les fournitures et les comptes depuis une interface Spring MVC simple.</p>
      </div>
    </section>

    <main class="login-form-side">
      <div class="login-card">
        <div class="login-brand">
          <svg viewBox="0 0 32 32" fill="none" width="36" height="36" aria-hidden="true">
            <rect width="32" height="32" rx="8" fill="#2563EB"/>
            <path d="M9 11l7 12 7-12" stroke="white" stroke-width="2.5"/>
          </svg>
          <span>VOLY VARY</span>
        </div>
        <h1>Connexion</h1>
        <p class="login-sub">Accédez à votre espace de gestion</p>

        <c:if test="${param.erreur != null}">
          <div class="alerte alerte-erreur">Nom d'utilisateur ou mot de passe incorrect.</div>
        </c:if>
        <c:if test="${param.deconnexion != null}">
          <div class="alerte alerte-succes">Vous avez été correctement déconnecté.</div>
        </c:if>

        <form id="login-form" method="post" action="/connexion">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <div class="form-field">
            <label for="nom">Nom d'utilisateur</label>
            <input type="text" id="nom" name="nom" autocomplete="username" required autofocus>
          </div>
          <div class="form-field">
            <label for="mdp">Mot de passe</label>
            <input type="password" id="mdp" name="mdp" autocomplete="current-password" required>
          </div>
          <button type="submit" class="btn btn-primary login-submit">Se connecter</button>
        </form>
      </div>
    </main>
  </div>
</body>
</html>
