<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Distribution : liste des factures - VOLY VARY</title>
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
          <h1>Distribution : liste des factures</h1>
          <p>Suivi des livraisons et statuts de distribution</p>
        </div>
      </div>

<div class="stat-grid">
    <div class="stat-card">
        <div class="stat-top"><div class="stat-icon blue">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="7" width="15" height="13"/><path d="M16 11h4l3 4v5h-7z"/><circle cx="5.5" cy="20.5" r="1.5"/><circle cx="18.5" cy="20.5" r="1.5"/></svg>
        </div></div>
        <div class="stat-value">${nombreTotalFactures}</div>
        <div class="stat-label">Total distributions</div>
    </div>
    <div class="stat-card">
        <div class="stat-top"><div class="stat-icon green">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 12V8H6a2 2 0 010-4h12v4"/><path d="M4 6v12a2 2 0 002 2h14v-4"/><path d="M18 12a2 2 0 000 4h4v-4z"/></svg>
        </div></div>
        <div class="stat-value"><fmt:formatNumber value="${qteGlobale}" maxFractionDigits="0"/></div>
        <div class="stat-label">Quantité totale de produits vendus</div>
    </div>
    <div class="stat-card">
        <div class="stat-top"><div class="stat-icon yellow">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M12 7v10M9 9.5c0-1 .9-1.8 2-1.8h1.5c1.1 0 2 .8 2 1.7 0 .8-.5 1.4-1.4 1.6l-2.2.5c-.9.2-1.4.8-1.4 1.6 0 1 .9 1.7 2 1.7H14c1.1 0 2-.8 2-1.8"/></svg>
        </div></div>
        <div class="stat-value"><fmt:formatNumber value="${recetteTotale}" maxFractionDigits="0"/> Ar</div>
        <div class="stat-label">Recette totale obtenue</div>
    </div>
    <div class="stat-card">
        <div class="stat-top"><div class="stat-icon red">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 9v4M12 17h.01M10.29 3.86l-8.18 14.14A2 2 0 003.82 21h16.36a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/></svg>
        </div></div>
        <div class="stat-value">${nombreEnCours}</div>
        <div class="stat-label">Commandes en cours</div>
    </div>
</div>

<div class="section-card">
    <div class="dt-toolbar">
        <form method="get" action="${ctx}/distribution/listeFactures" class="dt-toolbar-left" style="display:flex;gap:10px;flex-wrap:wrap;">
            <input type="hidden" name="tri" value="${tri}">
            <input type="hidden" name="dir" value="${dir}">
            <input type="hidden" name="taille" value="${taille}">
            <div class="dt-search">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
                <input type="text" name="q" value="${q}" placeholder="Rechercher (référence, client...)">
            </div>
            <div class="dt-filter">
                <select name="statut" onchange="this.form.submit()">
                    <option value="" ${empty statut ? 'selected' : ''}>Statut</option>
                    <option value="En cours" ${statut == 'En cours' ? 'selected' : ''}>En cours</option>
                    <option value="Terminé" ${statut == 'Terminé' ? 'selected' : ''}>Terminé</option>
                    <option value="Annulé" ${statut == 'Annulé' ? 'selected' : ''}>Annulé</option>
                </select>
            </div>
            <button type="submit" class="btn btn-outline btn-sm">Filtrer</button>
        </form>
        <div class="dt-toolbar-right">
            <a class="btn btn-outline btn-sm" href="<c:url value='/distribution/export/csv'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='${tri}'/><c:param name='dir' value='${dir}'/></c:url>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>
                CSV
            </a>
            <a class="btn btn-outline btn-sm" href="<c:url value='/distribution/export/excel'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='${tri}'/><c:param name='dir' value='${dir}'/></c:url>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>
                Excel
            </a>
            <a class="btn btn-outline btn-sm" href="<c:url value='/distribution/export/pdf'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='${tri}'/><c:param name='dir' value='${dir}'/></c:url>">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><path d="M14 2v6h6"/></svg>
                PDF
            </a>
            <a class="btn btn-primary btn-sm" href="${ctx}/distribution/nouvelleDistribution">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
                Nouvelle distribution
            </a>
        </div>
    </div>

    <div class="table-wrap">
        <table class="dt">
            <thead>
            <tr>
                <%-- En-tetes triables : chaque lien recharge la page avec le tri demande, en conservant recherche/filtre --%>
                <th class="${tri == 'reference' ? 'sorted' : ''}">
                    <a href="<c:url value='/distribution/listeFactures'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='reference'/><c:param name='dir' value='${tri == "reference" && dir == "asc" ? "desc" : "asc"}'/><c:param name='taille' value='${taille}'/></c:url>" style="color:inherit;text-decoration:none;">
                        Référence <c:if test="${tri == 'reference'}"><span class="sort-arrow">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                    </a>
                </th>
                <th class="${tri == 'clientReference' ? 'sorted' : ''}">
                    <a href="<c:url value='/distribution/listeFactures'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='clientReference'/><c:param name='dir' value='${tri == "clientReference" && dir == "asc" ? "desc" : "asc"}'/><c:param name='taille' value='${taille}'/></c:url>" style="color:inherit;text-decoration:none;">
                        Référence client <c:if test="${tri == 'clientReference'}"><span class="sort-arrow">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                    </a>
                </th>
                <th class="${tri == 'date' ? 'sorted' : ''}">
                    <a href="<c:url value='/distribution/listeFactures'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='date'/><c:param name='dir' value='${tri == "date" && dir == "asc" ? "desc" : "asc"}'/><c:param name='taille' value='${taille}'/></c:url>" style="color:inherit;text-decoration:none;">
                        Date <c:if test="${tri == 'date'}"><span class="sort-arrow">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                    </a>
                </th>
                <th class="${tri == 'quantite' ? 'sorted' : ''}">
                    <a href="<c:url value='/distribution/listeFactures'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='quantite'/><c:param name='dir' value='${tri == "quantite" && dir == "asc" ? "desc" : "asc"}'/><c:param name='taille' value='${taille}'/></c:url>" style="color:inherit;text-decoration:none;">
                        Qté vendue <c:if test="${tri == 'quantite'}"><span class="sort-arrow">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                    </a>
                </th>
                <th class="${tri == 'montant' ? 'sorted' : ''}">
                    <a href="<c:url value='/distribution/listeFactures'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='montant'/><c:param name='dir' value='${tri == "montant" && dir == "asc" ? "desc" : "asc"}'/><c:param name='taille' value='${taille}'/></c:url>" style="color:inherit;text-decoration:none;">
                        Total <c:if test="${tri == 'montant'}"><span class="sort-arrow">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                    </a>
                </th>
                <th class="${tri == 'statut' ? 'sorted' : ''}">
                    <a href="<c:url value='/distribution/listeFactures'><c:param name='q' value='${q}'/><c:param name='statut' value='${statut}'/><c:param name='tri' value='statut'/><c:param name='dir' value='${tri == "statut" && dir == "asc" ? "desc" : "asc"}'/><c:param name='taille' value='${taille}'/></c:url>" style="color:inherit;text-decoration:none;">
                        Statut <c:if test="${tri == 'statut'}"><span class="sort-arrow">${dir == 'asc' ? '▲' : '▼'}</span></c:if>
                    </a>
                </th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty factures}">
                    <tr><td colspan="7" class="dt-empty">Aucun résultat trouvé</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="facture" items="${factures}">
                        <tr>
                            <td>${facture.reference}</td>
                            <td>${facture.clientReference}</td>
                            <td>${facture.dateFormatee}</td>
                            <td><fmt:formatNumber value="${facture.quantite}" maxFractionDigits="0"/></td>
                            <td><fmt:formatNumber value="${facture.montant}" maxFractionDigits="0"/> Ar</td>
                            <td>
                                <c:choose>
                                    <c:when test="${facture.statut == 'Terminé'}"><span class="badge badge-green">${facture.statut}</span></c:when>
                                    <c:when test="${facture.statut == 'Annulé'}"><span class="badge badge-red">${facture.statut}</span></c:when>
                                    <c:otherwise><span class="badge badge-blue">${facture.statut}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="row-actions">
                                    <a class="action-icon" href="${ctx}/distribution/facture/${facture.id}" aria-label="Voir le détail" title="Voir le détail">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                    </a>
                                    <c:if test="${facture.statut == 'En cours'}">
                                        <form method="post" action="${ctx}/distribution/terminerCommande/${facture.id}" style="display:inline">
                                            <%-- Protège le changement de statut déclenché depuis la liste. --%>
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                            <button type="submit" class="action-icon" aria-label="Terminer" title="Terminer">
                                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"/></svg>
                                            </button>
                                        </form>
                                        <button type="button" class="action-icon danger" aria-label="Annuler" title="Annuler"
                                                data-annuler-id="${facture.id}" data-annuler-ref="${facture.reference}">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
                                        </button>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>

    <%-- Pagination cote serveur : chaque bouton est un lien vers la page demandee --%>
    <c:set var="debutAffichage" value="${totalElements == 0 ? 0 : (pageCourante * taille) + 1}"/>
    <c:set var="finAffichageBrut" value="${(pageCourante + 1) * taille}"/>
    <c:set var="finAffichage" value="${finAffichageBrut > totalElements ? totalElements : finAffichageBrut}"/>
    <div class="dt-pagination">
        <span class="dt-info">${debutAffichage}-${finAffichage} sur ${totalElements}</span>
        <div class="dt-pages">
            <c:choose>
                <c:when test="${pageCourante <= 0}">
                    <span class="dt-page-btn" style="opacity:.35;cursor:not-allowed;">&laquo;</span>
                </c:when>
                <c:otherwise>
                    <c:url var="urlPrecedent" value="/distribution/listeFactures">
                        <c:param name="q" value="${q}"/><c:param name="statut" value="${statut}"/>
                        <c:param name="tri" value="${tri}"/><c:param name="dir" value="${dir}"/>
                        <c:param name="taille" value="${taille}"/><c:param name="page" value="${pageCourante - 1}"/>
                    </c:url>
                    <a class="dt-page-btn" href="${urlPrecedent}">&laquo;</a>
                </c:otherwise>
            </c:choose>

            <c:if test="${pageTotales > 0}">
                <c:forEach begin="0" end="${pageTotales - 1}" var="p">
                    <c:url var="urlPage" value="/distribution/listeFactures">
                        <c:param name="q" value="${q}"/><c:param name="statut" value="${statut}"/>
                        <c:param name="tri" value="${tri}"/><c:param name="dir" value="${dir}"/>
                        <c:param name="taille" value="${taille}"/><c:param name="page" value="${p}"/>
                    </c:url>
                    <a class="dt-page-btn ${p == pageCourante ? 'active' : ''}" href="${urlPage}">${p + 1}</a>
                </c:forEach>
            </c:if>

            <c:choose>
                <c:when test="${pageCourante >= pageTotales - 1}">
                    <span class="dt-page-btn" style="opacity:.35;cursor:not-allowed;">&raquo;</span>
                </c:when>
                <c:otherwise>
                    <c:url var="urlSuivant" value="/distribution/listeFactures">
                        <c:param name="q" value="${q}"/><c:param name="statut" value="${statut}"/>
                        <c:param name="tri" value="${tri}"/><c:param name="dir" value="${dir}"/>
                        <c:param name="taille" value="${taille}"/><c:param name="page" value="${pageCourante + 1}"/>
                    </c:url>
                    <a class="dt-page-btn" href="${urlSuivant}">&raquo;</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%-- Formulaire cache reutilise pour l'annulation, apres confirmation via la modale --%>
<form id="form-annuler" method="post" style="display:none">
    <%-- Ce formulaire reçoit son URL en JavaScript, mais son jeton reste rendu côté serveur. --%>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
</form>

<script>
  document.querySelectorAll('[data-annuler-id]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var id = btn.getAttribute('data-annuler-id');
      var ref = btn.getAttribute('data-annuler-ref');
      openConfirmModal({
        title: 'Annuler cette distribution ?',
        message: 'La commande ' + ref + ' sera marquée comme annulée.',
        confirmLabel: 'Annuler la commande',
        onConfirm: function () {
          var form = document.getElementById('form-annuler');
          form.action = '${ctx}/distribution/annulerCommande/' + id;
          form.submit();
        }
      });
    });
  });
</script>


    <jsp:include page="../include/footer.jsp"/>
</div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>
