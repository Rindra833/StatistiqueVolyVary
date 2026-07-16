<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${titre}"/> - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css"><link rel="stylesheet" href="/assets/css/base.css"><link rel="stylesheet" href="/assets/css/components.css"><link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="fournitures"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../../include/recherche.jsp" />
    <main class="page-body">
      <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>Renseignez les informations de la fourniture.</p></div></div>
      <section class="section-card carte-formulaire">
        <form method="post" action="/admin/fournitures/enregistrer">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <c:if test="${fourniture.id > 0}"><input type="hidden" name="id" value="${fourniture.id}"></c:if>
          <div class="form-grid">
            <div class="form-field full"><label for="reference">Référence</label><input id="reference" name="reference" value="<c:out value='${fourniture.reference}'/>" required></div>
            <div class="form-field">
              <label for="idCategorie">Catégorie</label>
              <select id="idCategorie" name="idCategorie" required>
                <option value="">Sélectionner</option>
                <c:forEach items="${categoriesFourniture}" var="categorie">
                  <option value="${categorie.id}" ${fourniture.idCategorie == categorie.id ? 'selected' : ''}><c:out value="${categorie.libelle}"/></option>
                </c:forEach>
              </select>
              <small><a href="/admin/categories-fournitures">Gérer les catégories de fournitures</a></small>
            </div>
            <div class="form-field"><label for="prixUnitaire">Prix unitaire (Ar)</label><input id="prixUnitaire" type="number" min="0" step="0.01" name="prixUnitaire" value="<c:out value='${fourniture.prixUnitaire}'/>" required></div>
          </div>
          <div class="actions-formulaire"><a class="btn btn-outline" href="/admin/fournitures">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
        </form>
      </section>
      <jsp:include page="../../include/footer.jsp"/>
    </main>
  </div>
</div>
</body>
</html>
