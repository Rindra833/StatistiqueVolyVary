<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Lieux de livraison - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css"><link rel="stylesheet" href="/assets/css/base.css"><link rel="stylesheet" href="/assets/css/components.css"><link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="lieux"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../../include/recherche.jsp" />
    <main class="page-body">
      <div class="page-heading">
        <div><h1>Lieux de livraison</h1><p>Adresses proposées lors de la création d'une distribution</p></div>
        <a class="btn btn-primary" href="/admin/lieux/nouveau">Ajouter un lieu</a>
      </div>
      <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>
      <c:if test="${not empty messageErreur}"><div class="alerte alerte-erreur"><c:out value="${messageErreur}"/></div></c:if>
      <section class="section-card">
        <form method="get" action="/admin/lieux" class="barre-filtres">
          <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Nom du lieu">
          <button class="btn btn-outline" type="submit">Rechercher</button>
          <a class="btn btn-outline" href="/admin/lieux">Réinitialiser</a>
        </form>
        <div class="table-wrap">
          <table class="dt" id="tableau-lieux">
            <thead><tr><th>Nom du lieu</th><th>Actions</th></tr></thead>
            <tbody>
              <c:forEach items="${lieux}" var="lieu">
                <tr><td><c:out value="${lieu.nom}"/></td><td class="actions-tableau">
                  <a class="btn btn-outline btn-sm" href="/admin/lieux/${lieu.id}/modifier">Modifier</a>
                  <form method="post" action="/admin/lieux/${lieu.id}/supprimer" onsubmit="return confirm('Supprimer ce lieu ?');">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="submit" class="btn btn-danger btn-sm">Supprimer</button>
                  </form>
                </td></tr>
              </c:forEach>
              <c:if test="${empty lieux}"><tr><td colspan="2" class="dt-empty">Aucun lieu enregistré.</td></tr></c:if>
            </tbody>
          </table>
        </div>
        <div class="actions-export"><button class="btn btn-outline btn-sm" type="button" onclick="exporterTableauCsv('tableau-lieux', 'lieux.csv')">Exporter CSV</button><button class="btn btn-outline btn-sm" type="button" onclick="imprimerPage()">Imprimer / PDF</button></div>
      </section>
      <jsp:include page="../../include/footer.jsp"/>
    </main>
  </div>
</div>
<script src="/assets/js/jsp-tableau.js"></script>
</body>
</html>
