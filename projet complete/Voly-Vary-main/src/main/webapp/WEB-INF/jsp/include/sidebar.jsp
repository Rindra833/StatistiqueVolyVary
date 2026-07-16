<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%--
  La sidebar lit l'utilisateur de la session Spring Security. Les conditions ci-dessous ne
  remplacent pas la sécurité du serveur : elles cachent seulement les liens inutiles. SecurityConfig
  contrôle aussi chaque URL afin qu'un employé ne puisse pas contourner le menu manuellement.
--%>
<c:set var="racine" value="${pageContext.request.contextPath}" />
<c:set var="utilisateurConnecte" value="${pageContext.request.userPrincipal.principal}" />
<c:set var="nomConnecte" value="${empty utilisateurConnecte.nom ? pageContext.request.userPrincipal.name : utilisateurConnecte.nom}" />
<c:set var="roleConnecte" value="${utilisateurConnecte.role}" />
<c:set var="initialeConnectee" value="${empty nomConnecte ? 'U' : fn:toUpperCase(fn:substring(nomConnecte, 0, 1))}" />
<c:set var="pageDemandee" value="${empty param.activePage ? pageContext.request.requestURI : param.activePage}" />

<c:set var="estAdministrateur" value="${roleConnecte eq 'Administrateur'}" />
<c:set var="gereTransactions" value="${estAdministrateur or roleConnecte eq 'Responsable Transaction'}" />
<c:set var="gereCollectes" value="${estAdministrateur or roleConnecte eq 'Responsable Collecte'}" />
<c:set var="gereTransformations" value="${estAdministrateur or roleConnecte eq 'Responsable Transformation'}" />
<c:set var="gereDistributions" value="${estAdministrateur or roleConnecte eq 'Responsable Distribution'}" />
<c:set var="consulteStatistiques" value="${estAdministrateur or roleConnecte eq 'Responsable Statistiques'}" />
<c:set var="consulteClients" value="${gereTransactions or gereCollectes or gereDistributions}" />

<aside class="sidebar">
  <div class="sidebar-brand">
    <svg class="logo-mark" viewBox="0 0 32 32" fill="none" aria-hidden="true"><rect width="32" height="32" rx="8" fill="#2563EB"/><path d="M9 11l7 12 7-12" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
    <span class="logo-text">VOLY VARY</span>
  </div>

  <div class="sidebar-user">
    <div class="avatar"><c:out value="${initialeConnectee}" /></div>
    <div class="who">
      <strong><c:out value="${nomConnecte}" /></strong>
      <span><c:out value="${roleConnecte}" /></span>
    </div>
  </div>

  <nav class="sidebar-nav" aria-label="Navigation principale">
    <c:if test="${consulteStatistiques}">
      <a href="${racine}/admin/statistiques" class="sidebar-link ${fn:contains(pageDemandee, 'statistiques') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M3 3v18h18M8 17V9m5 8V5m5 12v-6"/></svg>
        <span>Tableau de bord</span>
      </a>
    </c:if>

    <c:if test="${consulteClients}">
      <div class="titre-navigation">Référentiel</div>
      <a href="${racine}/clients" class="sidebar-link ${fn:contains(pageDemandee, 'clients') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><circle cx="9" cy="8" r="3"/><path d="M3 20a6 6 0 0112 0M16 4a3 3 0 010 6M17 14a5 5 0 014 5"/></svg>
        <span>Clients</span>
      </a>
    </c:if>

    <c:if test="${gereTransactions}">
      <div class="titre-navigation">Transactions</div>
      <a href="${racine}/transaction/form" class="sidebar-link ${fn:contains(pageDemandee, 'nouvelle') or fn:contains(pageDemandee, '/transaction/form') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>
        <span>Nouvelle transaction</span>
      </a>
      <a href="${racine}/transactions" class="sidebar-link ${fn:contains(pageDemandee, 'transactions') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M4 6h16M4 12h16M4 18h16"/></svg>
        <span>Voir les transactions</span>
      </a>
    </c:if>

    <c:if test="${gereCollectes}">
      <div class="titre-navigation">Collectes</div>
      <a href="${racine}/collectes/nouveau" class="sidebar-link ${fn:contains(pageDemandee, '/collectes/nouveau') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>
        <span>Nouvelle collecte</span>
      </a>
      <a href="${racine}/collectes/valides" class="sidebar-link ${fn:contains(pageDemandee, 'collecte') and not fn:contains(pageDemandee, 'nouveau') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M4 6h16M4 12h16M4 18h16"/></svg>
        <span>Voir les collectes</span>
      </a>
    </c:if>

    <c:if test="${gereTransformations}">
      <div class="titre-navigation">Transformations</div>
      <a href="${racine}/transformation/formulaireAjoutTransformation" class="sidebar-link ${fn:contains(pageDemandee, 'formulaireAjoutTransformation') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>
        <span>Nouvelle transformation</span>
      </a>
      <a href="${racine}/transformation/lotPaddyTransforme" class="sidebar-link ${fn:contains(pageDemandee, 'transformation') and not fn:contains(pageDemandee, 'formulaireAjoutTransformation') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M4 6h16M4 12h16M4 18h16"/></svg>
        <span>Voir les transformations</span>
      </a>
    </c:if>

    <c:if test="${gereDistributions}">
      <div class="titre-navigation">Distributions</div>
      <a href="${racine}/distribution/nouvelleDistribution" class="sidebar-link ${fn:contains(pageDemandee, 'nouvelleDistribution') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>
        <span>Nouvelle distribution</span>
      </a>
      <a href="${racine}/distribution/listeFactures" class="sidebar-link ${fn:contains(pageDemandee, 'distribution') and not fn:contains(pageDemandee, 'nouvelleDistribution') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M4 6h16M4 12h16M4 18h16"/></svg>
        <span>Voir les distributions</span>
      </a>
    </c:if>

    <c:if test="${estAdministrateur}">
      <div class="titre-navigation">Administration</div>
      <a href="${racine}/admin/fournitures" class="sidebar-link ${fn:contains(pageDemandee, 'fournitures') and not fn:contains(pageDemandee, 'categories-fournitures') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M20 7l-8-4-8 4v10l8 4 8-4V7zM4 7l8 4 8-4M12 11v10"/></svg>
        <span>Fournitures</span>
      </a>
      <a href="${racine}/admin/categories-fournitures" class="sidebar-link ${fn:contains(pageDemandee, 'categories-fournitures') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M20 13l-7 7-9-9V4h7l9 9z"/><circle cx="8.5" cy="8.5" r="1.5"/></svg>
        <span>Catégories</span>
      </a>
      <a href="${racine}/admin/livreurs" class="sidebar-link ${fn:contains(pageDemandee, 'livreurs') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M5 21a7 7 0 0114 0"/></svg>
        <span>Livreurs</span>
      </a>
      <a href="${racine}/admin/lieux" class="sidebar-link ${fn:contains(pageDemandee, 'lieux') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-6 7-12a7 7 0 10-14 0c0 6 7 12 7 12z"/><circle cx="12" cy="9" r="2"/></svg>
        <span>Lieux de livraison</span>
      </a>
      <a href="${racine}/admin/utilisateurs" class="sidebar-link ${fn:contains(pageDemandee, 'utilisateurs') ? 'active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M5 21a7 7 0 0114 0"/></svg>
        <span>Utilisateurs</span>
      </a>
    </c:if>
  </nav>

  <div class="sidebar-footer">
    <form method="post" action="${racine}/deconnexion">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <button type="submit" class="sidebar-link bouton-deconnexion">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M10 17l5-5-5-5M15 12H3M15 3h5a1 1 0 011 1v16a1 1 0 01-1 1h-5"/></svg>
        <span>Déconnexion</span>
      </button>
    </form>
  </div>
</aside>
