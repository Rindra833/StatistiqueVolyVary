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
  <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="categories-fournitures"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../../include/recherche.jsp" />
    <main class="page-body">
      <div class="page-heading"><div><h1><c:out value="${titre}"/></h1><p>Le libellé sera proposé dans les formulaires Fourniture et Transaction.</p></div></div>
      <c:if test="${not empty messageErreur}"><div class="alerte alerte-erreur"><c:out value="${messageErreur}"/></div></c:if>
      <section class="section-card carte-formulaire">
        <form method="post" action="/admin/categories-fournitures/enregistrer">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <%-- L'identifiant primitif vaut 0 tant que la catégorie n'est pas persistée. --%>
          <c:if test="${categorie.id > 0}"><input type="hidden" name="id" value="${categorie.id}"></c:if>
          <div class="form-grid"><div class="form-field full">
            <label for="libelle">Libellé de la catégorie</label>
            <input id="libelle" name="libelle" value="<c:out value='${categorie.libelle}'/>" required maxlength="120">
          </div></div>
          <div class="actions-formulaire"><a class="btn btn-outline" href="/admin/categories-fournitures">Annuler</a><button type="submit" class="btn btn-primary">Enregistrer</button></div>
        </form>
      </section>
      <jsp:include page="../../include/footer.jsp"/>
    </main>
  </div>
</div>
</body>
</html>
