<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.volyVary.dto.*" %>
<%
  List<LotStockDto> listeDto = (List<LotStockDto>) request.getAttribute("listeStock");
  String error   = (String) request.getAttribute("error");
  String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nouvelle transformation - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/style.css">
  <link rel="stylesheet" href="/assets/css/transformation.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="formulaireAjoutTransformation"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../include/recherche.jsp" />
    <main class="page-body">

      <div class="page-heading">
        <div><h1>Transformation</h1><p>Transformer un ou plusieurs lots de paddy</p></div>
        <a href="/transformation/lotPaddyTransforme" class="btn btn-outline btn-sm">Retour a la liste</a>
      </div>

      <% if (success != null) { %>
        <div class="alert alert-success"><%= success %></div>
      <% } %>
      <% if (error != null) { %>
        <div class="alert alert-error"><%= error %></div>
      <% } %>

      <div class="card flow-card">
        <div class="card-body">
          <form action="/transformation/traitementAjout" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-field full">
              <label>Date et heure</label>
              <input type="datetime-local" name="date" value="${dateCourante}" required>
            </div>

            <div class="form-field full">
              <label>Prix unitaire de transformation (Ar/kg)</label>
              <input type="number" name="prixUnitaire" value="${prixUnitaire}" min="0.01" step="0.01" required>
              <small>Le dernier tarif utilisé sera proposé automatiquement à la prochaine transformation.</small>
            </div>

            <div class="form-field full">
              <label>Lots de paddy disponibles</label>
              <div class="lots-container">
                <div class="lots-header">
                  <span>ID</span>
                  <span>Référence</span>
                  <span style="text-align: right;">Quantité</span>
                </div>

                <% if(listeDto != null && !listeDto.isEmpty()) {
                  double totalGeneral = 0;
                  for(LotStockDto l : listeDto) {
                    totalGeneral += l.getQuantiteReel();
                %>
                  <div class="lot-item">
                    <span class="lot-id">#<%= l.getIdLot() %></span>
                    <span class="lot-reference"><%= l.getReference() %></span>
                    <span class="lot-quantity"><%= String.format("%.1f", l.getQuantiteReel()) %></span>
                  </div>
                <% }
                } else { %>
                  <div class="empty-lots">Aucun lot disponible</div>
                <% } %>

              </div>
            </div>

            <div class="form-field full">
              <label>Quantite totale de Paddy (kg)</label>
              <input type="number"
                     name="quantite"
                     step="0.1"
                     required
                     placeholder="Entrez la quantité totale">
            </div>

            <div class="flow-actions">
              <button type="submit" class="btn btn-primary">Transformer</button>
            </div>
          </form>
        </div>
      </div>

    <jsp:include page="../include/footer.jsp"/>
</div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>
