<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title><c:out value="${titre}"/> - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css"><link rel="stylesheet" href="/assets/css/base.css"><link rel="stylesheet" href="/assets/css/components.css"><link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="utilisateurs"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../../include/recherche.jsp" />
    <main class="page-body">
  <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>Le mot de passe est toujours encodé avec BCrypt.</p></div></div>
  <c:if test="${not empty messageErreur}"><div class="alerte alerte-erreur"><c:out value="${messageErreur}"/></div></c:if>
  <section class="section-card carte-formulaire"><form method="post" action="/admin/utilisateurs/enregistrer">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><c:if test="${utilisateur.id != null}"><input type="hidden" name="id" value="${utilisateur.id}"></c:if>
    <div class="form-grid">
      <div class="form-field full"><label for="nom">Nom d'utilisateur</label><input id="nom" name="nom" value="<c:out value='${utilisateur.nom}'/>" required></div>
      <div class="form-field"><label for="role">Rôle</label><select id="role" name="role"><c:forEach items="${roles}" var="roleDisponible"><option value="<c:out value='${roleDisponible}'/>" ${utilisateur.role eq roleDisponible ? 'selected' : ''}><c:out value="${roleDisponible}"/></option></c:forEach></select></div>
      <div class="form-field"><label for="employeeId">Employé associé</label><select id="employeeId" name="employeeId"><option value="">Aucun employé</option><c:forEach items="${employees}" var="employee"><option value="${employee.id}" ${utilisateur.employee != null and utilisateur.employee.id eq employee.id ? 'selected' : ''}><c:out value="${employee.nom}"/></option></c:forEach></select></div>
      <div class="form-field full"><label for="mdp">${utilisateur.id == null ? 'Mot de passe' : 'Nouveau mot de passe (facultatif)'}</label><input id="mdp" type="password" name="mdp" ${utilisateur.id == null ? 'required' : ''} autocomplete="new-password"></div>
    </div>
    <div class="actions-formulaire"><a class="btn btn-outline" href="/admin/utilisateurs">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
  </form></section>
    <jsp:include page="../../include/footer.jsp"/>
</div>
</body>
</html>