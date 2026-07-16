<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Detail transaction - VOLY VARY</title>
<link rel="stylesheet" href="/assets/css/variables.css">
<link rel="stylesheet" href="/assets/css/base.css">
<link rel="stylesheet" href="/assets/css/components.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="transactions"/></jsp:include>
<div class="main-content">
        <jsp:include page="../include/recherche.jsp" />
        <main class="page-body">

  <div class="page-heading">
    <div><h1>Transaction <c:out value="${transaction.referenceTransaction}"/></h1><p>Detail de la transaction</p></div>
    <!-- <a href="/transactions" class="btn btn-outline btn-sm">Retour a la liste</a> -->
  </div>

  <div class="card recap-card">
    <div class="card-body">
      <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value"><c:out value="${transaction.dateTransaction}"/></span></div>
      <div class="recap-row"><span class="recap-label">Type</span><span class="recap-value"><c:out value="${transaction.typeTransaction.libelleTypeTransaction}"/></span></div>
      <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value"><c:out value="${transaction.client.referenceClient}"/></span></div>
      <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value"><c:out value="${transaction.client.nom}"/></span></div>
      <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value"><c:out value="${transaction.client.prenom}"/></span></div>
      <div class="recap-row"><span class="recap-label">Telephone</span><span class="recap-value"><c:out value="${transaction.client.telephone}"/></span></div>

      <table class="recap-table">
        <thead><tr><th>Fournitures</th><th>Quantite</th><th>Prix unitaire</th><th>Montant</th></tr></thead>
        <tbody>
          <c:forEach items="${lignes}" var="ligne">
            <tr>
              <td><c:out value="${ligne.referenceFourniture}"/></td>
              <td><c:out value="${ligne.quantite}"/></td>
              <td><c:out value="${ligne.prixUnitaire}"/> Ar</td>
              <td><c:out value="${ligne.montantLigne}"/> Ar</td>
            </tr>
          </c:forEach>
          <tr class="total-row"><td></td><td></td><td>Total</td><td><c:out value="${transaction.montantTotal}"/> Ar</td></tr>
        </tbody>
      </table>

      <c:if test="${not empty historique}">
        <div style="margin-top:24px;">
          <h3 style="font-size:var(--fs-md);font-weight:700;margin-bottom:12px;">Historique des statuts</h3>
          <c:forEach items="${historique}" var="hist">
            <div class="recap-row">
              <span class="recap-label"><c:out value="${hist.date}"/></span>
              <span class="recap-value">
                <span class="badge <c:choose><c:when test="${hist.sigle == 'VAL'}">badge-green</c:when><c:when test="${hist.sigle == 'ATT'}">badge-yellow</c:when><c:otherwise>badge-gray</c:otherwise></c:choose>">
                  <c:out value="${hist.statut}"/> (<c:out value="${hist.sigle}"/>)
                </span>
              </span>
            </div>
          </c:forEach>
        </div>
      </c:if>

      <div class="flow-actions" style="margin-top:24px;">
        <a href="/transactions" class="btn btn-outline" style="flex:1;">Retour a la liste</a>
        <button class="btn btn-primary" style="flex:1;" onclick="window.print()">Imprimer</button>
      </div>
    </div>
  </div>

  <jsp:include page="../include/footer.jsp"/>
</div>
 </main>
  </div> 
  </div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>
