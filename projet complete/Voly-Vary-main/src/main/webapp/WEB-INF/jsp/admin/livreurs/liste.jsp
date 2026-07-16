<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Livreurs - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
  <div class="app-shell">
    <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="livreurs"/></jsp:include>
    <div class="main-content">
      <jsp:include page="../../include/recherche.jsp" />
      <main class="page-body">
        <div class="page-heading">
          <div><h1>Livreurs</h1><p>Registre des véhicules affectés aux livraisons</p></div>
          <a class="btn btn-primary" href="/admin/livreurs/nouveau">Ajouter un livreur</a>
        </div>

        <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>
        <c:if test="${not empty messageErreur}"><div class="alerte alerte-erreur"><c:out value="${messageErreur}"/></div></c:if>

        <section class="section-card">
          <form method="get" action="/admin/livreurs" class="barre-filtres">
            <%-- Le modèle commun du projet identifie un livreur par le matricule de son véhicule. --%>
            <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Matricule du véhicule">
            <button class="btn btn-outline" type="submit">Rechercher</button>
            <a class="btn btn-outline" href="/admin/livreurs">Réinitialiser</a>
          </form>

          <div class="table-wrap">
            <table class="dt" id="tableau-livreurs">
              <thead><tr><th>Matricule du véhicule</th><th>Actions</th></tr></thead>
              <tbody>
                <c:forEach items="${livreurs}" var="livreur">
                  <tr>
                    <td><c:out value="${livreur.matriculeVehicule}"/></td>
                    <td class="actions-tableau">
                      <a class="btn btn-outline btn-sm" href="/admin/livreurs/${livreur.id}/modifier">Modifier</a>
                      <form method="post" action="/admin/livreurs/${livreur.id}/supprimer" onsubmit="return confirm('Supprimer ce livreur ?');">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <button type="submit" class="btn btn-danger btn-sm">Supprimer</button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
                <c:if test="${empty livreurs}"><tr><td colspan="2" class="dt-empty">Aucun livreur trouvé.</td></tr></c:if>
              </tbody>
            </table>
          </div>
          <div class="actions-export"><button class="btn btn-outline btn-sm" type="button" onclick="exporterTableauCsv('tableau-livreurs', 'livreurs.csv')">Exporter CSV</button><button class="btn btn-outline btn-sm" type="button" onclick="imprimerPage()">Imprimer / PDF</button></div>
        </section>
      <jsp:include page="../../include/footer.jsp"/>
    </div>
  </div>
  <script src="/assets/js/jsp-tableau.js"></script>
</body>
</html>
