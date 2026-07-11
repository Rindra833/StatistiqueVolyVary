<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Fournitures - VOLY VARY</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jsp.css">
</head>
<body><div class="app-shell"><%@ include file="../fragments/navigation.jspf" %><div class="main-content"><%@ include file="../fragments/barre-superieure.jspf" %><main class="page-body">
  <div class="page-heading"><div><h1>Fournitures</h1><p>Gestion du stock de fournitures</p></div><a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/fournitures/nouvelle">Ajouter une fourniture</a></div>
  <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>
  <section class="section-card">
    <form method="get" action="${pageContext.request.contextPath}/admin/fournitures" class="barre-filtres">
      <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Nom ou fournisseur">
      <select name="categorie"><option value="">Toutes les catégories</option><option value="Emballage" ${categorieSelectionnee eq 'Emballage' ? 'selected' : ''}>Emballage</option><option value="Intrant agricole" ${categorieSelectionnee eq 'Intrant agricole' ? 'selected' : ''}>Intrant agricole</option><option value="Matériel" ${categorieSelectionnee eq 'Matériel' ? 'selected' : ''}>Matériel</option><option value="Équipement" ${categorieSelectionnee eq 'Équipement' ? 'selected' : ''}>Équipement</option></select>
      <button class="btn btn-outline" type="submit">Rechercher</button><a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/fournitures">Réinitialiser</a>
    </form>
    <div class="table-wrap"><table class="dt" id="tableau-fournitures">
      <thead><tr><th>Nom</th><th>Catégorie</th><th>Quantité</th><th>Prix unitaire</th><th>Fournisseur</th><th>Date</th><th>Actions</th></tr></thead>
      <tbody>
        <c:forEach items="${fournitures}" var="fourniture"><tr>
          <td><c:out value="${fourniture.nom}"/></td><td><span class="badge badge-blue"><c:out value="${fourniture.categorie}"/></span></td><td><c:out value="${fourniture.quantite}"/></td><td><c:out value="${fourniture.prix}"/> Ar</td><td><c:out value="${fourniture.fournisseur}"/></td><td><c:out value="${fourniture.date}"/></td>
          <td class="actions-tableau"><a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/admin/fournitures/${fourniture.id}/modifier">Modifier</a><form method="post" action="${pageContext.request.contextPath}/admin/fournitures/${fourniture.id}/supprimer" onsubmit="return confirm('Supprimer cette fourniture ?');"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button type="submit" class="btn btn-danger btn-sm">Supprimer</button></form></td>
        </tr></c:forEach>
        <c:if test="${empty fournitures}"><tr><td colspan="7" class="dt-empty">Aucune fourniture trouvée.</td></tr></c:if>
      </tbody>
    </table></div>
    <div class="actions-export"><button class="btn btn-outline btn-sm" type="button" onclick="exporterTableauCsv('tableau-fournitures', 'fournitures.csv')">Exporter CSV</button><button class="btn btn-outline btn-sm" type="button" onclick="imprimerPage()">Imprimer / PDF</button></div>
  </section>
</main></div></div><script src="${pageContext.request.contextPath}/assets/js/jsp-tableau.js"></script></body>
</html>
