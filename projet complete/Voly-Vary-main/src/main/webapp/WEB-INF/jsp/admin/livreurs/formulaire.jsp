<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${titre}"/> - VOLY VARY</title>
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
      <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>Renseignez le véhicule affecté au livreur.</p></div></div>
      <section class="section-card carte-formulaire">
        <form method="post" action="/admin/livreurs/enregistrer">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <%-- L'identifiant vaut 0 lors d'une création car le modèle utilise le type primitif int. --%>
          <c:if test="${livreur.id > 0}"><input type="hidden" name="id" value="${livreur.id}"></c:if>
          <div class="form-grid">
            <%-- Seul cet attribut existe dans l'entité Livreur partagée par le projet complet. --%>
            <div class="form-field full">
              <label for="matriculeVehicule">Matricule du véhicule</label>
              <input id="matriculeVehicule" name="matriculeVehicule" value="<c:out value='${livreur.matriculeVehicule}'/>" required maxlength="50">
            </div>
          </div>
          <div class="actions-formulaire"><a class="btn btn-outline" href="/admin/livreurs">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
        </form>
      </section>
    <jsp:include page="../../include/footer.jsp"/>
  </div>
</div>
</body>
</html>
