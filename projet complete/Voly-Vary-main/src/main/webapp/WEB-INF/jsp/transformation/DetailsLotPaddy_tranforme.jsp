<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.volyVary.modele.DetailLotTransforme" %>
<%@ page import="com.volyVary.modele.LotPaddyTransforme" %>
<%@ page import="com.volyVary.modele.LotPaddyMouvement" %>
<%
  List<DetailLotTransforme> details          = (List<DetailLotTransforme>) request.getAttribute("details");
  LotPaddyTransforme        lotpaddyTransforme = (LotPaddyTransforme) request.getAttribute("lotpaddyTransforme");
  List<LotPaddyMouvement>   lotTouche        = (List<LotPaddyMouvement>) request.getAttribute("LotTouche");
  Double                    saisie           = (Double) request.getAttribute("saisie");
  String                    error            = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Detail transformation - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="transformation"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../include/recherche.jsp" />
    <main class="page-body">

      <div class="page-heading">
        <div><h1>Transformation : detail lot de paddy transforme</h1><p></p></div>
      </div>

      <div class="card recap-card">
        <% if (error != null) { %>
          <div class="card-body flow-empty">
            <p><%= error %></p>
            <a class="detail-link" href="/transformation/lotPaddyTransforme">Retour a la liste</a>
          </div>
        <% } else if (details != null && !details.isEmpty() && lotpaddyTransforme != null) { %>
          <div class="card-head"><h3>Detail lot transforme</h3></div>
          <div class="card-body">

            <div class="recap-row">
              <span class="recap-label">Date</span>
              <span class="recap-value"><%= details.get(0).getDate() %></span>
            </div>
            <div class="recap-row">
              <span class="recap-label">Reference</span>
              <span class="recap-value"><%= details.get(0).getLotTransforme().getReference() %></span>
            </div>
            <div class="recap-row">
              <span class="recap-label">Quantite de Paddy</span>
              <span class="recap-value"><%= saisie %> kg</span>
            </div>

            <p style="font-size:var(--fs-sm);font-weight:600;color:var(--color-gray-700);margin-top:12px">Obtenues</p>
            <table class="recap-table">
              <tbody>
                <% for (DetailLotTransforme detail : details) { %>
                  <tr>
                    <td><%= detail.getProduit().getNomProduit() %></td>
                    <td><%= detail.getQuantite() %> kg</td>
                  </tr>
                <% } %>
              </tbody>
            </table>

            <div class="recap-row" style="margin-top:12px">
              <span class="recap-label">Prix/kg pour la transformation</span>
              <span class="recap-value"><%= lotpaddyTransforme.getPrixTransformation() %> Ar</span>
            </div>
            <div class="recap-row">
              <span class="recap-label">Montant total</span>
              <span class="recap-value"><%= lotpaddyTransforme.getPrixTransformation() * saisie %> Ar</span>
            </div>

            <p style="font-size:var(--fs-sm);font-weight:600;color:var(--color-gray-700);margin-top:12px">Lots de Paddy utilises</p>
            <table class="recap-table">
              <thead><tr><th>Reference</th><th>Quantite</th></tr></thead>
              <tbody>
                <% for (LotPaddyMouvement lot : lotTouche) { %>
                  <tr>
                    <td><%= lot.getLotPaddy().getReference() %></td>
                    <td><%= lot.getQuantite() %> kg</td>
                  </tr>
                <% } %>
              </tbody>
            </table>

            <div class="flow-actions">
              <a href="/transformation/lotPaddyTransforme" class="btn btn-outline">Retour a la liste</a>
              <a href="/transformation/formulaireAjoutTransformation" class="btn btn-outline">Nouvelle transformation</a>
              <a href="/transformation/pdf/<%= lotpaddyTransforme.getId() %>" class="btn btn-outline">Exporter PDF</a>
              <button class="btn btn-primary" onclick="window.print()">Imprimer</button>
            </div>

          </div>
        <% } else { %>
          <div class="card-body flow-empty">
            <p>Aucun detail disponible pour cette transformation.</p>
            <a class="detail-link" href="/transformation">Retour a la liste</a>
          </div>
        <% } %>
      </div>

    <jsp:include page="../include/footer.jsp"/>
  </div>
</div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>