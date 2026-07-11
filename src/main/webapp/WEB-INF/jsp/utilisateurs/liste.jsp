<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Utilisateurs - VOLY VARY</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css"><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css"><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css"><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jsp.css">
</head>
<body><div class="app-shell"><%@ include file="../fragments/navigation.jspf" %><div class="main-content"><%@ include file="../fragments/barre-superieure.jspf" %><main class="page-body">
  <div class="page-heading"><div><h1>Utilisateurs</h1><p>Gestion des comptes et des rôles</p></div><a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/utilisateurs/nouveau">Créer un compte</a></div>
  <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>
  <section class="section-card">
    <form method="get" action="${pageContext.request.contextPath}/admin/utilisateurs" class="barre-filtres">
      <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Nom d'utilisateur">
      <select name="role"><option value="">Tous les rôles</option><c:forEach items="${roles}" var="roleDisponible"><option value="<c:out value='${roleDisponible}'/>" ${roleSelectionne eq roleDisponible ? 'selected' : ''}><c:out value="${roleDisponible}"/></option></c:forEach></select>
      <button class="btn btn-outline" type="submit">Rechercher</button><a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/utilisateurs">Réinitialiser</a>
    </form>
    <div class="table-wrap"><table class="dt" id="tableau-utilisateurs"><thead><tr><th>Nom d'utilisateur</th><th>Rôle</th><th>Employé associé</th><th>Actions</th></tr></thead><tbody>
      <c:forEach items="${utilisateurs}" var="utilisateur"><tr><td><c:out value="${utilisateur.nom}"/></td><td><span class="badge badge-blue"><c:out value="${utilisateur.role}"/></span></td><td><c:choose><c:when test="${utilisateur.employee != null}"><c:out value="${utilisateur.employee.nom}"/></c:when><c:otherwise>Non associé</c:otherwise></c:choose></td><td class="actions-tableau"><a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/admin/utilisateurs/${utilisateur.id}/modifier">Modifier</a><form method="post" action="${pageContext.request.contextPath}/admin/utilisateurs/${utilisateur.id}/supprimer" onsubmit="return confirm('Supprimer ce compte ?');"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button type="submit" class="btn btn-danger btn-sm">Supprimer</button></form></td></tr></c:forEach>
      <c:if test="${empty utilisateurs}"><tr><td colspan="4" class="dt-empty">Aucun utilisateur trouvé.</td></tr></c:if>
    </tbody></table></div>
    <div class="actions-export"><button class="btn btn-outline btn-sm" type="button" onclick="exporterTableauCsv('tableau-utilisateurs', 'utilisateurs.csv')">Exporter CSV</button><button class="btn btn-outline btn-sm" type="button" onclick="imprimerPage()">Imprimer / PDF</button></div>
  </section>
</main></div></div><script src="${pageContext.request.contextPath}/assets/js/jsp-tableau.js"></script></body>
</html>
