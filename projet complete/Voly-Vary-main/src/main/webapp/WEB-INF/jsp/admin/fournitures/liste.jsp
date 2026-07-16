<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Fournitures - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="fournitures"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../../include/recherche.jsp" />
    <main class="page-body">
  <div class="page-heading"><div><h1>Fournitures</h1><p>Gestion du stock de fournitures</p></div><a class="btn btn-primary" href="/admin/fournitures/nouvelle">Ajouter une fourniture</a></div>
  <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>
  <section class="section-card">
    <form method="get" action="/admin/fournitures" class="barre-filtres">
      <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Référence">
      <select name="categorie">
        <option value="">Toutes les catégories</option>
        <c:forEach items="${categoriesFourniture}" var="categorie">
          <option value="${categorie.id}" ${categorieSelectionnee == categorie.id ? 'selected' : ''}><c:out value="${categorie.libelle}"/></option>
        </c:forEach>
      </select>
      <button class="btn btn-outline" type="submit">Rechercher</button><a class="btn btn-outline" href="/admin/fournitures">Réinitialiser</a>
    </form>
    <div class="table-wrap"><table class="dt" id="tableau-fournitures">
      <thead><tr><th>Référence</th><th>Catégorie</th><th>Prix unitaire</th><th>Actions</th></tr></thead>
      <tbody>
        <c:forEach items="${fournitures}" var="fourniture"><tr>
          <td><c:out value="${fourniture.reference}"/></td>
          <td><span class="badge badge-blue"><c:forEach items="${categoriesFourniture}" var="categorie"><c:if test="${categorie.id == fourniture.idCategorie}"><c:out value="${categorie.libelle}"/></c:if></c:forEach></span></td>
          <td><c:out value="${fourniture.prixUnitaire}"/> Ar</td>
          <td class="actions-tableau"><a class="btn btn-outline btn-sm" href="/admin/fournitures/${fourniture.id}/modifier">Modifier</a><form method="post" action="/admin/fournitures/${fourniture.id}/supprimer" onsubmit="return confirm('Supprimer cette fourniture ?');"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><button type="submit" class="btn btn-danger btn-sm">Supprimer</button></form></td>
        </tr></c:forEach>
        <c:if test="${empty fournitures}"><tr><td colspan="4" class="dt-empty">Aucune fourniture trouvée.</td></tr></c:if>
      </tbody>
    </table></div>
    <div class="actions-export"><button class="btn btn-outline btn-sm" type="button" onclick="exporterTableauCsv('tableau-fournitures', 'fournitures.csv')">Exporter CSV</button><button class="btn btn-outline btn-sm" type="button" onclick="imprimerPage()">Imprimer / PDF</button></div>
  </section>
    <jsp:include page="../../include/footer.jsp"/>
</div>
<script src="/assets/js/jsp-tableau.js"></script>
</body>
</html>
