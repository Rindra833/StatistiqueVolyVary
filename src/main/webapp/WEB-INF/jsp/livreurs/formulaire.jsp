<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${titre}"/> - VOLY VARY</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jsp.css">
</head>
<body><div class="app-shell"><%@ include file="../fragments/navigation.jspf" %><div class="main-content"><%@ include file="../fragments/barre-superieure.jspf" %><main class="page-body">
  <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>Renseignez les informations du livreur.</p></div></div>
  <section class="section-card carte-formulaire">
    <form method="post" action="${pageContext.request.contextPath}/admin/livreurs/enregistrer">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <c:if test="${livreur.id != null}"><input type="hidden" name="id" value="${livreur.id}"></c:if>
      <div class="form-grid">
        <div class="form-field full"><label for="nom">Nom complet</label><input id="nom" name="nom" value="<c:out value='${livreur.nom}'/>" required></div>
        <div class="form-field"><label for="telephone">Téléphone</label><input id="telephone" name="telephone" value="<c:out value='${livreur.telephone}'/>" required></div>
        <div class="form-field"><label for="vehicule">Véhicule</label><select id="vehicule" name="vehicule"><option ${livreur.vehicule eq 'Moto' ? 'selected' : ''}>Moto</option><option ${livreur.vehicule eq 'Camionnette' ? 'selected' : ''}>Camionnette</option><option ${livreur.vehicule eq 'Camion' ? 'selected' : ''}>Camion</option></select></div>
        <div class="form-field"><label for="immatriculation">Immatriculation</label><input id="immatriculation" name="immatriculation" value="<c:out value='${livreur.immatriculation}'/>"></div>
        <div class="form-field"><label for="disponibilite">Disponibilité</label><select id="disponibilite" name="disponibilite"><option ${livreur.disponibilite eq 'Disponible' ? 'selected' : ''}>Disponible</option><option ${livreur.disponibilite eq 'Indisponible' ? 'selected' : ''}>Indisponible</option></select></div>
        <div class="form-field full"><label for="adresse">Adresse</label><input id="adresse" name="adresse" value="<c:out value='${livreur.adresse}'/>" required></div>
      </div>
      <div class="actions-formulaire"><a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/livreurs">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
    </form>
  </section>
</main></div></div></body>
</html>
