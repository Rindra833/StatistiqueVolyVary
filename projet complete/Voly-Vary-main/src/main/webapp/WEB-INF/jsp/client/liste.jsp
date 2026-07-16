<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Clients - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="clients"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../include/recherche.jsp" />
    <main class="page-body">
      <div class="page-heading">
        <div><h1>Clients</h1><p>Répertoire partagé par les transactions, collectes et distributions</p></div>
        <a class="btn btn-primary" href="/clients/nouveau">Ajouter un client</a>
      </div>

      <c:if test="${not empty messageSucces}"><div class="alerte alerte-succes"><c:out value="${messageSucces}"/></div></c:if>
      <c:if test="${not empty erreur}"><div class="alerte alerte-erreur"><c:out value="${erreur}"/></div></c:if>

      <section class="section-card">
        <form method="get" action="/clients" class="barre-filtres">
          <input type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Référence, nom ou téléphone">
          <button class="btn btn-outline" type="submit">Rechercher</button>
          <a class="btn btn-outline" href="/clients">Réinitialiser</a>
        </form>

        <div class="table-wrap">
          <table class="dt">
            <thead><tr><th>Référence</th><th>Nom</th><th>Prénom</th><th>Téléphone</th><th>Date</th><th>Actions</th></tr></thead>
            <tbody>
              <c:forEach items="${clients}" var="client">
                <tr>
                  <td><strong><c:out value="${client.reference}"/></strong></td>
                  <td><c:out value="${client.nom}"/></td>
                  <td><c:out value="${client.prenom}"/></td>
                  <td><c:out value="${client.telephone}"/></td>
                  <td><c:out value="${client.date}"/></td>
                  <td class="actions-tableau">
                    <a class="btn btn-outline btn-sm" href="/clients/${client.id}/modifier">Modifier</a>
                    <form method="post" action="/clients/${client.id}/supprimer" onsubmit="return confirm('Supprimer ce client ?');">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                      <button type="submit" class="btn btn-danger btn-sm">Supprimer</button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty clients}"><tr><td colspan="6" class="dt-empty">Aucun client trouvé.</td></tr></c:if>
            </tbody>
          </table>
        </div>
      </section>
    <jsp:include page="../include/footer.jsp"/>
  </div>
</div>
</body>
</html>
