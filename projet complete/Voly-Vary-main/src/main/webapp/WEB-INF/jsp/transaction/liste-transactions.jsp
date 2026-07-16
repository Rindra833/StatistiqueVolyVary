<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Transactions - VOLY VARY</title>
<link rel="stylesheet" href="/assets/css/variables.css">
<link rel="stylesheet" href="/assets/css/base.css">
<link rel="stylesheet" href="/assets/css/components.css">
<link rel="stylesheet" href="/assets/css/recherche.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="transactions"/></jsp:include>
   <div class="main-content">
        <jsp:include page="../include/recherche.jsp" />
        <main class="page-body">

  <div class="page-heading">
    <div>
      <h1>Liste des transactions</h1>
      <p>Historique des transactions financières</p>
    </div>
  </div>

  <div class="stat-grid">
    <div class="stat-card">
      <div class="stat-top"><div class="stat-icon green"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg></div></div>
      <div class="stat-value">${quantiteTotaleVendue}</div>
      <div class="stat-label">Quantité totale vendue</div>
    </div>
    <div class="stat-card">
      <div class="stat-top"><div class="stat-icon yellow"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg></div></div>
      <div class="stat-value"><c:out value="${recetteTotale}" default="0"/> Ar</div>
      <div class="stat-label">Recette totale</div>
    </div>
  </div>

  <div class="section-card">
    <div class="dt-toolbar">
      <div class="dt-toolbar-left">
        <form method="get" action="/transactions" class="filter-form">
          <div class="dt-search">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input type="text" name="reference" value="${reference}" placeholder="Réf de la transaction...">
          </div>
          <div class="dt-search">
            <input type="text" name="referenceClient" value="${referenceClient}" placeholder="Ref client...">
          </div>
          <div class="dt-filter">
            <select name="typeTransactionId">
              <option value="">Tous les types</option>
              <c:forEach items="${listeTypesTransaction}" var="typeT">
                <option value="${typeT.idTypeTransaction}" <c:if test="${typeT.idTypeTransaction == typeTransactionId}">selected</c:if>><c:out value="${typeT.libelleTypeTransaction}"/></option>
              </c:forEach>
            </select>
          </div>
          <input type="date" name="dateDebut" value="${dateDebut}">
          <input type="date" name="dateFin" value="${dateFin}">
          <button type="submit" class="btn btn-primary btn-sm">Rechercher</button>
          <a href="/transactions" class="btn btn-outline btn-sm">Reset</a>
        </form>
      </div>
      <div class="dt-toolbar-right">
        <button class="btn btn-outline btn-sm" onclick="document.getElementById('exportModal').classList.add('open')">Exporter</button>
      </div>
    </div>

    <div class="table-wrap">
      <table class="dt">
        <thead>
          <tr>
            <th onclick="trierTableau(0,false,this)">Référence <span class="fleche-tri">↕</span></th>
            <th onclick="trierTableau(1,false,this)">Référence client <span class="fleche-tri">↕</span></th>
            <th onclick="trierTableau(2,false,this)">Date <span class="fleche-tri">↕</span></th>
            <th onclick="trierTableau(3, true,this)">Quantité totale <span class="fleche-tri">↕</span></th>
            <th onclick="trierTableau(4, true,this)">Total <span class="fleche-tri">↕</span></th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${listeTransactions}" var="tx">
            <tr>
              <td><c:out value="${tx.referenceTransaction}"/></td>
              <td><c:out value="${tx.referenceClient}"/></td>
              <td><c:out value="${tx.dateTransaction}"/></td>
              <td><c:out value="${tx.quantiteTotale}"/></td>
              <td><c:out value="${tx.total}"/> Ar</td>
              <td class="row-actions">
                <a class="action-icon" href="/transactions/${tx.idTransaction}" title="Voir détail">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                </a>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty listeTransactions}">
            <tr><td colspan="6" class="dt-empty">Aucune transaction trouvée</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>

    <div class="dt-pagination">
      <div class="dt-info">Page ${pageActuelle + 1} sur ${totalPages}</div>
      <div class="dt-pages">
        <c:if test="${pageActuelle > 0}">
          <a class="dt-page-btn" href="/transactions?page=${pageActuelle - 1}&reference=${reference}&referenceClient=${referenceClient}&typeTransactionId=${typeTransactionId}&dateDebut=${dateDebut}&dateFin=${dateFin}">&laquo;</a>
        </c:if>
        <span class="dt-page-btn active">${pageActuelle + 1}</span>
        <c:if test="${pageActuelle + 1 < totalPages}">
          <a class="dt-page-btn" href="/transactions?page=${pageActuelle + 1}&reference=${reference}&referenceClient=${referenceClient}&typeTransactionId=${typeTransactionId}&dateDebut=${dateDebut}&dateFin=${dateFin}">&raquo;</a>
        </c:if>
      </div>
    </div>
  </div>

  <div class="modal-overlay" id="exportModal">
    <div class="modal-box">
      <div class="modal-header"><h3>Exporter la liste</h3><button class="modal-close" onclick="document.getElementById('exportModal').classList.remove('open')">&times;</button></div>
      <div class="modal-body">
        <p style="font-size:var(--fs-sm);color:var(--color-gray-500);">Choisissez le format d'export pour la page courante.</p>
        <form method="post" action="/transactions/exporter" style="margin-top:16px;display:flex;gap:8px;">
          <%-- L'export de la page est envoyé en POST et doit être accepté par Spring Security. --%>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <input type="hidden" name="page" value="${pageActuelle}">
          <button type="submit" name="format" value="csv" class="btn btn-outline" style="flex:1;">CSV</button>
          <button type="submit" name="format" value="excel" class="btn btn-outline" style="flex:1;">Excel</button>
          <button type="submit" name="format" value="pdf" class="btn btn-outline" style="flex:1;">PDF</button>
        </form>
      </div>
    </div>
  </div>

 <jsp:include page="../include/footer.jsp"/>
  </main>
  </div> 
  </div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
<script src="/assets/js/triColonne.js"></script>
<script>toastConsume();</script>
</body>
</html>
