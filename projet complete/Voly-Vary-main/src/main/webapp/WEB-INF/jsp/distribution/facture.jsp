<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Distribution : facture - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="distribution"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../include/recherche.jsp" />
    <main class="page-body">

      <div class="page-heading">
        <div>
          <h1>Distribution : facture</h1>
          <p>Résumé de la commande</p>
        </div>
        <a href="${ctx}/distribution/listeFactures" class="btn btn-outline btn-sm">Retour à la liste</a>
      </div>

<div class="card recap-card">
    <div class="card-head"><h3>Distribution ${distribution.reference}</h3></div>
    <div class="card-body">
        <div class="recap-row"><span class="recap-label">Référence</span><span class="recap-value">${distribution.reference}</span></div>
        <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${dateFacture}</span></div>
        <div class="recap-row"><span class="recap-label">Référence client</span><span class="recap-value">${distribution.client.reference}</span></div>
        <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${distribution.client.nom}</span></div>
        <div class="recap-row"><span class="recap-label">Prénom</span><span class="recap-value">${distribution.client.prenom}</span></div>
        <div class="recap-row"><span class="recap-label">Numéro téléphone</span><span class="recap-value">${distribution.client.telephone}</span></div>
        <div class="recap-row"><span class="recap-label">Lieu</span><span class="recap-value">${distribution.lieu.nom}</span></div>
        <div class="recap-row"><span class="recap-label">Livreur</span><span class="recap-value">${distribution.livreur.matriculeVehicule}</span></div>
        <div class="recap-row">
            <span class="recap-label">Statut</span>
            <span class="recap-value">
                <c:choose>
                    <c:when test="${statut == 'Terminé'}"><span class="badge badge-green">${statut}</span></c:when>
                    <c:when test="${statut == 'Annulé'}"><span class="badge badge-red">${statut}</span></c:when>
                    <c:otherwise><span class="badge badge-blue">${statut}</span></c:otherwise>
                </c:choose>
            </span>
        </div>

        <table class="recap-table">
            <thead><tr><th>Produits</th><th>Quantité</th><th>Total</th></tr></thead>
            <tbody>
            <c:forEach var="detail" items="${details}">
                <tr>
                    <td>${detail.produit.nomProduit}</td>
                    <td><fmt:formatNumber value="${detail.quantite}" maxFractionDigits="0"/></td>
                    <td><fmt:formatNumber value="${detail.quantite * detail.produit.prixUnitaire}" maxFractionDigits="0"/> Ar</td>
                </tr>
            </c:forEach>
            <tr class="total-row">
                <td></td>
                <td>Montant total</td>
                <td><fmt:formatNumber value="${total}" maxFractionDigits="0"/> Ar</td>
            </tr>
            </tbody>
        </table>

        <div class="flow-actions">
            <c:if test="${statut == 'En cours'}">
                <form method="post" action="${ctx}/distribution/annulerCommande/${distribution.id}" style="display:inline" id="form-annuler-facture">
                    <%-- Toute modification d'une distribution doit porter le jeton de la session. --%>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="button" class="btn btn-outline" id="btn-annuler-facture">Annuler</button>
                </form>
                <form method="post" action="${ctx}/distribution/terminerCommande/${distribution.id}" style="display:inline">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="submit" class="btn btn-primary">Terminer la commande</button>
                </form>
            </c:if>
            <button type="button" class="btn btn-outline" onclick="window.print()">Imprimer</button>
        </div>
    </div>
</div>

<script>
  var btnAnnuler = document.getElementById('btn-annuler-facture');
  if (btnAnnuler) {
    btnAnnuler.addEventListener('click', function () {
      openConfirmModal({
        title: 'Annuler cette distribution ?',
        message: 'La commande ${distribution.reference} sera marquée comme annulée.',
        confirmLabel: 'Annuler la commande',
        onConfirm: function () {
          document.getElementById('form-annuler-facture').submit();
        }
      });
    });
  }
</script>

    <jsp:include page="../include/footer.jsp"/>
</div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>
