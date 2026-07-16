<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Facture - VOLY VARY</title>
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
    <div><h1>Transaction de fourniture : facture</h1><p>Verifiez les informations avant de confirmer</p></div>
  </div>

  <div class="card recap-card">
    <div class="card-head"><h3>Transaction <c:out value="${referenceTransaction}"/></h3></div>
    <div class="card-body">
      <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value"><c:out value="${dateTransaction}"/></span></div>
      <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value"><c:out value="${referenceClient}"/></span></div>
      <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value"><c:out value="${nomClient}"/></span></div>
      <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value"><c:out value="${prenomClient}"/></span></div>
      <div class="recap-row"><span class="recap-label">Telephone</span><span class="recap-value"><c:out value="${telephoneClient}"/></span></div>
      <div class="recap-row"><span class="recap-label">Categorie</span><span class="recap-value"><c:out value="${categorieFourniture.libelleCategorieFourniture}"/></span></div>

      <table class="recap-table">
        <thead><tr><th>Fournitures</th><th>Quantite</th><th>Montant</th></tr></thead>
        <tbody>
          <c:forEach items="${lignes}" var="ligne">
            <tr>
              <td><c:out value="${ligne.referenceFourniture}"/></td>
              <td><c:out value="${ligne.quantite}"/></td>
              <td><c:out value="${ligne.montantLigne}"/> Ar</td>
            </tr>
          </c:forEach>
          <tr class="total-row"><td></td><td>Total</td><td><c:out value="${totalGeneral}"/> Ar</td></tr>
        </tbody>
      </table>

      <div class="flow-actions">
        <form method="post" action="/transaction/annuler" style="flex:1;margin:0;">
          <%-- Le jeton CSRF est obligatoire pour chaque formulaire POST sécurisé. --%>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <button type="submit" class="btn btn-outline" style="width:100%;">Annuler</button>
        </form>
        <form method="post" action="/transaction/confirmer" style="flex:1;margin:0;">
          <%-- Autorise la confirmation tout en protégeant la session de l'utilisateur. --%>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <input type="hidden" name="typeTransactionId" value="${typeTransactionId}">
          <input type="hidden" name="referenceClient" value="${referenceClient}">
          <input type="hidden" name="nomClient" value="${nomClient}">
          <input type="hidden" name="prenomClient" value="${prenomClient}">
          <input type="hidden" name="telephoneClient" value="${telephoneClient}">
          <input type="hidden" name="categorieFournitureId" value="${categorieFournitureId}">
          <input type="hidden" name="referenceTransaction" value="${referenceTransaction}">
          <input type="hidden" name="dateTransaction" value="${dateTransaction}">
          <c:forEach items="${idFournitures}" var="idF">
            <input type="hidden" name="idFourniture" value="${idF}">
          </c:forEach>
          <c:forEach items="${quantites}" var="qte">
            <input type="hidden" name="quantite" value="${qte}">
          </c:forEach>
          <button type="submit" class="btn btn-primary" style="width:100%;">Confirmer la transaction</button>
        </form>
      </div>

      <div style="margin-top:12px;text-align:center;">
        <button class="btn btn-ghost btn-sm" onclick="document.getElementById('exportModal').classList.add('open')">Exporter la facture</button>
      </div>
    </div>
  </div>

  <div class="modal-overlay" id="exportModal">
    <div class="modal-box">
      <div class="modal-header"><h3>Exporter la facture</h3><button class="modal-close" onclick="document.getElementById('exportModal').classList.remove('open')">&times;</button></div>
      <div class="modal-body">
        <p style="font-size:var(--fs-sm);color:var(--color-gray-500);">Choisissez le format d'export.</p>
        <form method="post" action="/transaction/exporter" style="margin-top:16px;display:flex;gap:8px;">
          <%-- L'export est envoyé en POST avec les données de la facture : il doit aussi porter le jeton. --%>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <input type="hidden" name="referenceTransaction" value="${referenceTransaction}">
          <input type="hidden" name="dateTransaction" value="${dateTransaction}">
          <input type="hidden" name="referenceClient" value="${referenceClient}">
          <input type="hidden" name="nomClient" value="${nomClient}">
          <input type="hidden" name="prenomClient" value="${prenomClient}">
          <input type="hidden" name="telephoneClient" value="${telephoneClient}">
          <c:forEach items="${idFournitures}" var="idF">
            <input type="hidden" name="idFourniture" value="${idF}">
          </c:forEach>
          <c:forEach items="${quantites}" var="qte">
            <input type="hidden" name="quantite" value="${qte}">
          </c:forEach>
          <button type="submit" name="format" value="csv" class="btn btn-outline" style="flex:1;">CSV</button>
          <button type="submit" name="format" value="excel" class="btn btn-outline" style="flex:1;">Excel</button>
          <button type="submit" name="format" value="pdf" class="btn btn-outline" style="flex:1;">PDF</button>
        </form>
      </div>
    </div>
  </div>

  <jsp:include page="../include/footer.jsp"/>
</div>

<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>
