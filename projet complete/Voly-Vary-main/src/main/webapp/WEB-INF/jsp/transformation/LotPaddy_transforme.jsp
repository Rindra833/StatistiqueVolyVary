<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ page import="org.springframework.data.domain.Page" %>
    <%@ page import="com.volyVary.modele.LotPaddyTransforme" %>
      <%@ page import="com.volyVary.modele.Transformation" %>

        <% Page<LotPaddyTransforme> listePaddy =(Page<LotPaddyTransforme>) request.getAttribute("lotPaddyTransforme");
            Transformation t = (Transformation) request.getAttribute("transformation");
            Double totalObj = (Double) request.getAttribute("total");
            double totalPaddyTransforme = totalObj != null ? totalObj : 0;
            String message = (String) request.getAttribute("message");
            String testPaddyTransforme = (String) request.getAttribute("lotPaddyVide");
            String debut = request.getParameter("debut");
            String fin = request.getParameter("fin");
            String referenceParam = request.getParameter("reference");
            String sortByParam = request.getParameter("sortBy");
            String sortDirParam = request.getParameter("sortDir");
            boolean hasDebut = debut != null && !debut.isBlank();
            boolean hasFin = fin != null && !fin.isBlank();
            boolean hasFiltre = hasDebut || hasFin;
            boolean hasReferenceSearch = referenceParam != null && !referenceParam.isBlank();
            String currentSortBy = (sortByParam != null && !sortByParam.isBlank()) ? sortByParam : "date";
            String currentSortDir = "asc".equalsIgnoreCase(sortDirParam) ? "asc" : "desc";
            String listBaseUrl;
            if (hasReferenceSearch) {
              listBaseUrl = "/transformation/searchReference?reference=" + referenceParam + "&";
            } else if (hasFiltre) {
              listBaseUrl = "/transformation/traitementFiltre?debut=" + (hasDebut ? debut : "")
                  + "&fin=" + (hasFin ? fin : "") + "&";
            } else {
              listBaseUrl = "/transformation/lotPaddyTransforme?";
            }
            String exportPdfUrl = hasFiltre
            ? "/transformation/pdfListe?debut=" + (hasDebut ? debut : "") + "&fin=" + (hasFin ? fin : "")
            : "/transformation/pdfListe";
            double depenseTotal = (t != null) ? t.getPrixUnitaire() * totalPaddyTransforme : 0;
            long pageStart = (listePaddy != null && listePaddy.getTotalElements() > 0)
            ? ((long) listePaddy.getNumber() * listePaddy.getSize()) + 1
            : 0;
            long pageEnd = (listePaddy != null && listePaddy.getTotalElements() > 0)
            ? pageStart + listePaddy.getNumberOfElements() - 1
            : 0;
            %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Transformation - VOLY VARY</title>
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
        <div>
          <h1>Transformation : liste des lots de paddy transforme</h1>
          <p>Suivi des lots de transformation</p>
        </div>
        <a href="/transformation/formulaireAjoutTransformation" class="btn btn-primary btn-sm">+ Nouvelle
          transformation</a>
      </div>

      <%-- Stat cards --%>
      <div class="stat-grid">
        <div class="stat-card">
          <div class="stat-top">
            <div class="stat-icon blue">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 2l4 4-4 4M3 12v-2a4 4 0 014-4h14M7 22l-4-4 4-4M21 12v2a4 4 0 01-4 4H3" />
              </svg>
            </div>
          </div>
          <div class="stat-value">
            <%= listePaddy !=null ? listePaddy.getTotalElements() : 0 %>
          </div>
          <div class="stat-label">Lots transformes</div>
        </div>
        <div class="stat-card">
          <div class="stat-top">
            <div class="stat-icon green">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 2l4 4-4 4M3 12v-2a4 4 0 014-4h14M7 22l-4-4 4-4M21 12v2a4 4 0 01-4 4H3" />
              </svg>
            </div>
          </div>
          <div class="stat-value">
            <% if (totalPaddyTransforme !=0) { %>
              <%= totalPaddyTransforme %>
                <% } else { %>
                  <%= testPaddyTransforme %>
                    <% } %> kg
          </div>
          <div class="stat-label">Quantite de lot de paddy transforme</div>
        </div>
        <div class="stat-card">
          <div class="stat-top">
            <div class="stat-icon yellow">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 2l4 4-4 4M3 12v-2a4 4 0 014-4h14M7 22l-4-4 4-4M21 12v2a4 4 0 01-4 4H3" />
              </svg>
            </div>
          </div>
          <div class="stat-value">
            <%= String.format("%,.0f", depenseTotal) %> Ar
          </div>
          <div class="stat-label">Depense totale pour la transformation</div>
        </div>
      </div>

      <%-- Filtre --%>
      <div class="section-card">
        <div class="filter-panel">
          <div class="filter-panel-head">
            <div>
              <h2>Filtrer les transformations</h2>
              <p>Affinez la liste par plage de dates avant export ou consultation detaillee.</p>
            </div>
            <span class="filter-chip <%= hasFiltre ? "" : "off" %>">
              <%= hasFiltre ? "Filtre actif" : "Aucun filtre actif" %>
            </span>
          </div>
          <form action="/transformation/traitementFiltre" method="get" class="filter-bar">
            <div class="form-field filter-input">
              <label for="filter-debut">Date debut</label>
              <input id="filter-debut" type="datetime-local" name="debut" value="<%= hasDebut ? debut : "" %>">
            </div>
            <div class="form-field filter-input">
              <label for="filter-fin">Date fin</label>
              <input id="filter-fin" type="datetime-local" name="fin" value="<%= hasFin ? fin : "" %>">
            </div>
            <div class="filter-actions">
              <input type="hidden" name="page" value="0">
              <input type="hidden" name="sortBy" value="<%= currentSortBy %>">
              <input type="hidden" name="sortDir" value="<%= currentSortDir %>">
              <button type="submit" class="btn btn-primary btn-sm">Appliquer</button>
              <a href="/transformation/lotPaddyTransforme" class="btn btn-outline btn-sm">Reinitialiser</a>
            </div>
          </form>
        </div>
      </div>

      <%-- Tableau --%>
      <div class="section-card">
        <div class="dt-toolbar">
          <form action="/transformation/searchReference" method="get" class="dt-toolbar-left">
            <div class="dt-search">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" />
                <path d="M21 21l-4.35-4.35" />
              </svg>
              <input type="text" name="reference" value="<%= referenceParam != null ? referenceParam : "" %>" placeholder="Rechercher une reference...">
              <input type="hidden" name="page" value="0">
              <input type="hidden" name="sortBy" value="<%= currentSortBy %>">
              <input type="hidden" name="sortDir" value="<%= currentSortDir %>">
            </div>
            <div class="table-meta">
              <span class="table-result-badge"><%= listePaddy != null ? listePaddy.getNumberOfElements() : 0 %></span>
              <button type="submit" class="btn btn-primary btn-sm">Rechercher</button>
            </div>
          </form>
          <div class="dt-toolbar-right">
            <a href="<%= exportPdfUrl %>" class="btn btn-outline btn-sm">PDF</a>
            <a href="/transformation/formulaireAjoutTransformation" class="btn btn-primary btn-sm">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 5v14M5 12h14" />
              </svg>
              Nouvelle transformation
            </a>
          </div>
        </div>
        <div class="table-wrap">
          <table class="dt">
            <thead>
              <tr>
                <th data-sort-key="reference">
                  <a class="sort-link" href="<%= listBaseUrl %>page=0&sortBy=reference&sortDir=<%= ("reference".equals(currentSortBy) && "asc".equals(currentSortDir)) ? "desc" : "asc" %>">
                    Reference
                    <span class="sort-arrow"><%= "reference".equals(currentSortBy) ? ("asc".equals(currentSortDir) ? "▲" : "▼") : "▲" %></span>
                  </a>
                </th>
                <th data-sort-key="date">
                  <a class="sort-link" href="<%= listBaseUrl %>page=0&sortBy=date&sortDir=<%= ("date".equals(currentSortBy) && "asc".equals(currentSortDir)) ? "desc" : "asc" %>">
                    Date
                    <span class="sort-arrow"><%= "date".equals(currentSortBy) ? ("asc".equals(currentSortDir) ? "▲" : "▼") : "▲" %></span>
                  </a>
                </th>
                <th data-sort-key="quantite">
                  <a class="sort-link" href="<%= listBaseUrl %>page=0&sortBy=quantite&sortDir=<%= ("quantite".equals(currentSortBy) && "asc".equals(currentSortDir)) ? "desc" : "asc" %>">
                    Quantite
                    <span class="sort-arrow"><%= "quantite".equals(currentSortBy) ? ("asc".equals(currentSortDir) ? "▲" : "▼") : "▲" %></span>
                  </a>
                </th>
                <th data-sort-key="prixTransformation">
                  <a class="sort-link" href="<%= listBaseUrl %>page=0&sortBy=prixTransformation&sortDir=<%= ("prixTransformation".equals(currentSortBy) && "asc".equals(currentSortDir)) ? "desc" : "asc" %>">
                    Prix unitaire
                    <span class="sort-arrow"><%= "prixTransformation".equals(currentSortBy) ? ("asc".equals(currentSortDir) ? "▲" : "▼") : "▲" %></span>
                  </a>
                </th>
                <th data-sort-key="prixTransformation">
                  <a class="sort-link" href="<%= listBaseUrl %>page=0&sortBy=prixTransformation&sortDir=<%= ("prixTransformation".equals(currentSortBy) && "asc".equals(currentSortDir)) ? "desc" : "asc" %>">
                    Total
                    <span class="sort-arrow"><%= "prixTransformation".equals(currentSortBy) ? ("asc".equals(currentSortDir) ? "▲" : "▼") : "▲" %></span>
                  </a>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <% if (listePaddy==null || listePaddy.isEmpty()) { %>
                <tr>
                  <td colspan="6" class="dt-empty">
                    <%= message !=null ? message : "Aucun resultat" %>
                  </td>
                </tr>
                <% } else { %>
                  <% for (LotPaddyTransforme l : listePaddy.getContent()) { %>
                    <tr>
                      <td>
                        <%= l.getReference() %>
                      </td>
                      <td>
                        <%= l.getDate() %>
                      </td>
                      <td>
                        <%= l.getQuantite() %> kg
                      </td>
                      <td>
                        <%= t !=null ? t.getPrixUnitaire() : "-" %> Ar
                      </td>
                      <td>
                        <%= l.getPrixTransformation() %> Ar
                      </td>
                      <td>
                        <div class="row-actions">
                          <a class="action-icon"
                            href="/transformation/detailsLotPaddyTransforme?idLotTransforme=<%= l.getId() %>"
                            aria-label="Voir detail">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                              <circle cx="12" cy="12" r="3" />
                            </svg>
                          </a>
                        </div>
                      </td>
                    </tr>
                    <% } %>
              <% } %>
            </tbody>
          </table>
        </div>
        <% if(listePaddy !=null) { %>
          <div class="dt-pagination">
            <span class="dt-info"><%= pageStart %>-<%= pageEnd %> sur <%= listePaddy.getTotalElements() %></span>
            <div class="dt-pages">
              <% if (listePaddy.hasPrevious()) { %>
                <a class="dt-page-btn"
                   href="<%= listBaseUrl + "page=" + (listePaddy.getNumber() - 1) + "&sortBy=" + currentSortBy + "&sortDir=" + currentSortDir %>">&laquo;</a>
              <% } else { %>
                <span class="dt-page-btn" style="cursor:not-allowed;opacity:.35">&laquo;</span>
              <% } %>

              <span class="dt-page-btn active"><%= listePaddy.getNumber() + 1 %></span>

              <% if (listePaddy.hasNext()) { %>
                <a class="dt-page-btn"
                   href="<%= listBaseUrl + "page=" + (listePaddy.getNumber() + 1) + "&sortBy=" + currentSortBy + "&sortDir=" + currentSortDir %>">&raquo;</a>
              <% } else { %>
                <span class="dt-page-btn" style="cursor:not-allowed;opacity:.35">&raquo;</span>
              <% } %>
            </div>
          </div>
        <% } %>
      </div>

    <jsp:include page="../include/footer.jsp"/>
</div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>