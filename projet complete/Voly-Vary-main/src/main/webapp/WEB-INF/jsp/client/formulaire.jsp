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
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="clients"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../include/recherche.jsp" />
    <main class="page-body">
      <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>La référence permet de retrouver ce client dans tous les modules.</p></div></div>

      <c:if test="${not empty erreur}"><div class="alerte alerte-erreur"><c:out value="${erreur}"/></div></c:if>

      <section class="section-card carte-formulaire">
        <form method="post" action="/clients/enregistrer">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <c:if test="${client.id > 0}"><input type="hidden" name="id" value="${client.id}"></c:if>
          <div class="form-grid">
            <div class="form-field"><label for="reference">Référence</label><input id="reference" name="reference" value="<c:out value='${client.reference}'/>" required></div>
            <div class="form-field"><label for="date">Date d'enregistrement</label><input id="date" type="date" name="date" value="${client.date}" required></div>
            <div class="form-field"><label for="nom">Nom</label><input id="nom" name="nom" value="<c:out value='${client.nom}'/>" required></div>
            <div class="form-field"><label for="prenom">Prénom</label><input id="prenom" name="prenom" value="<c:out value='${client.prenom}'/>" required></div>
            <div class="form-field full"><label for="telephone">Téléphone</label><input id="telephone" name="telephone" value="<c:out value='${client.telephone}'/>" pattern="[0-9 ]+" required></div>
          </div>
          <div class="actions-formulaire"><a class="btn btn-outline" href="/clients">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
        </form>
      </section>
    <jsp:include page="../include/footer.jsp"/>
  </div>
</div>
</body>
</html>
