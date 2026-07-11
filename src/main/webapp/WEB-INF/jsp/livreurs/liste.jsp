<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Livreurs - VOLY VARY</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jsp.css">
</head>
<body>
  <div class="app-shell">
    <%@ include file="../fragments/navigation.jspf" %>
    <div class="main-content">
      <%@ include file="../fragments/barre-superieure.jspf" %>
      <main class="page-body">
        <div class="page-heading">
          <div><h1>Livreurs</h1><p>Registre des livreurs sans compte de connexion</p></div>
          <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/livreurs/nouveau">Ajouter un livreur</a>
        </div>

        <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>

        <section class="section-card">
          <form method="get" action="${pageContext.request.contextPath}/admin/livreurs" class="barre-filtres">
            <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Nom, téléphone ou adresse">
            <select name="vehicule">
              <option value="">Tous les véhicules</option>
              <option value="Moto" ${vehiculeSelectionne eq 'Moto' ? 'selected' : ''}>Moto</option>
              <option value="Camionnette" ${vehiculeSelectionne eq 'Camionnette' ? 'selected' : ''}>Camionnette</option>
              <option value="Camion" ${vehiculeSelectionne eq 'Camion' ? 'selected' : ''}>Camion</option>
            </select>
            <select name="disponibilite">
              <option value="">Toutes les disponibilités</option>
              <option value="Disponible" ${disponibiliteSelectionnee eq 'Disponible' ? 'selected' : ''}>Disponible</option>
              <option value="Indisponible" ${disponibiliteSelectionnee eq 'Indisponible' ? 'selected' : ''}>Indisponible</option>
            </select>
            <button class="btn btn-outline" type="submit">Rechercher</button>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/livreurs">Réinitialiser</a>
          </form>

          <div class="table-wrap">
            <table class="dt" id="tableau-livreurs">
              <thead><tr><th>Nom</th><th>Téléphone</th><th>Adresse</th><th>Véhicule</th><th>Immatriculation</th><th>Statut</th><th>Actions</th></tr></thead>
              <tbody>
                <c:forEach items="${livreurs}" var="livreur">
                  <tr>
                    <td><c:out value="${livreur.nom}"/></td>
                    <td><c:out value="${livreur.telephone}"/></td>
                    <td><c:out value="${livreur.adresse}"/></td>
                    <td><span class="badge badge-blue"><c:out value="${livreur.vehicule}"/></span></td>
                    <td><c:out value="${livreur.immatriculation}"/></td>
                    <td><span class="badge ${livreur.disponibilite eq 'Disponible' ? 'badge-green' : 'badge-red'}"><c:out value="${livreur.disponibilite}"/></span></td>
                    <td class="actions-tableau">
                      <a class="btn btn-outline btn-sm" href="${pageContext.request.contextPath}/admin/livreurs/${livreur.id}/modifier">Modifier</a>
                      <form method="post" action="${pageContext.request.contextPath}/admin/livreurs/${livreur.id}/supprimer" onsubmit="return confirm('Supprimer ce livreur ?');">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <button type="submit" class="btn btn-danger btn-sm">Supprimer</button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
                <c:if test="${empty livreurs}"><tr><td colspan="7" class="dt-empty">Aucun livreur trouvé.</td></tr></c:if>
              </tbody>
            </table>
          </div>
          <div class="actions-export"><button class="btn btn-outline btn-sm" type="button" onclick="exporterTableauCsv('tableau-livreurs', 'livreurs.csv')">Exporter CSV</button><button class="btn btn-outline btn-sm" type="button" onclick="imprimerPage()">Imprimer / PDF</button></div>
        </section>
      </main>
    </div>
  </div>
  <script src="${pageContext.request.contextPath}/assets/js/jsp-tableau.js"></script>
</body>
</html>
