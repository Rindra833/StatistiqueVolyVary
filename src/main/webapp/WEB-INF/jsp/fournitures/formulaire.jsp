<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${titre}"/> - VOLY VARY</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/variables.css"><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css"><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css"><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/jsp.css">
</head>
<body><div class="app-shell"><%@ include file="../fragments/navigation.jspf" %><div class="main-content"><%@ include file="../fragments/barre-superieure.jspf" %><main class="page-body">
  <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>Renseignez les informations de la fourniture.</p></div></div>
  <section class="section-card carte-formulaire"><form method="post" action="${pageContext.request.contextPath}/admin/fournitures/enregistrer">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"><c:if test="${fourniture.id != null}"><input type="hidden" name="id" value="${fourniture.id}"></c:if>
    <div class="form-grid">
      <div class="form-field full"><label for="nom">Nom</label><input id="nom" name="nom" value="<c:out value='${fourniture.nom}'/>" required></div>
      <div class="form-field"><label for="categorie">Catégorie</label><select id="categorie" name="categorie"><option ${fourniture.categorie eq 'Emballage' ? 'selected' : ''}>Emballage</option><option ${fourniture.categorie eq 'Intrant agricole' ? 'selected' : ''}>Intrant agricole</option><option ${fourniture.categorie eq 'Matériel' ? 'selected' : ''}>Matériel</option><option ${fourniture.categorie eq 'Équipement' ? 'selected' : ''}>Équipement</option></select></div>
      <div class="form-field"><label for="fournisseur">Fournisseur</label><input id="fournisseur" name="fournisseur" value="<c:out value='${fourniture.fournisseur}'/>" required></div>
      <div class="form-field"><label for="quantite">Quantité</label><input id="quantite" type="number" min="0" name="quantite" value="<c:out value='${fourniture.quantite}'/>" required></div>
      <div class="form-field"><label for="prix">Prix unitaire (Ar)</label><input id="prix" type="number" min="0" step="0.01" name="prix" value="<c:out value='${fourniture.prix}'/>" required></div>
      <div class="form-field full"><label for="date">Date</label><input id="date" type="date" name="date" value="<c:out value='${fourniture.date}'/>" required></div>
    </div>
    <div class="actions-formulaire"><a class="btn btn-outline" href="${pageContext.request.contextPath}/admin/fournitures">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
  </form></section>
</main></div></div></body>
</html>
