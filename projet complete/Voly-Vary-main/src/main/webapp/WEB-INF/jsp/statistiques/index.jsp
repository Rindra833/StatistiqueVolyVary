<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Statistiques - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/jsp.css">
</head>
<body>
  <c:set var="racine" value="${pageContext.request.contextPath}" />
  <div class="app-shell">
    <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="statistiques"/></jsp:include>
    <div class="main-content">
      <jsp:include page="../include/recherche.jsp" />
      <main class="page-body">
        <div class="page-heading">
          <div>
            <h1>Statistiques</h1>
            <p>Analyse des transactions, collectes et transformations de paddy</p>
          </div>
          <form method="get" action="${racine}/admin/statistiques" class="filtre-periode">
            <div><label for="debut">Du</label><input type="date" id="debut" name="debut" value="${tableau.periode.debut}"></div>
            <div><label for="fin">Au</label><input type="date" id="fin" name="fin" value="${tableau.periode.fin}"></div>
            <button type="submit" class="btn btn-primary">Filtrer</button>
            <a class="btn btn-outline" href="${racine}/admin/statistiques">Mois actuel</a>
          </form>
        </div>

        <c:if test="${not empty messageErreur}"><div class="alerte alerte-erreur"><c:out value="${messageErreur}"/></div></c:if>

        <p class="periode-affichee">
          <strong><c:out value="${tableau.periode.libelle}"/></strong>
          — du <c:out value="${debutFormate}"/> au <c:out value="${finFormate}"/>
        </p>

        <c:if test="${tableau.global.nombreTransactions eq 0
                      and tableau.global.paddyCollecte eq 0
                      and tableau.global.paddyTransforme eq 0}">
          <div class="alerte alerte-information">
            Aucune activité n'a été enregistrée entre le
            <c:out value="${debutFormate}" /> et le <c:out value="${finFormate}" />.
          </div>
        </c:if>

        <section aria-labelledby="titre-global">
          <div class="titre-section-statistique">
            <div><span>01</span><div><h2 id="titre-global">Statistique globale</h2><p>Vue synthétique de l'activité</p></div></div>
          </div>
          <div class="stat-grid">
            <article class="stat-card">
              <div class="stat-top"><span class="icone-statistique bleu">T</span></div>
              <div class="stat-value"><fmt:formatNumber value="${tableau.global.nombreTransactions}"/></div>
              <div class="stat-label">Transactions</div>
            </article>
            <article class="stat-card">
              <div class="stat-top"><span class="icone-statistique violet">Ar</span></div>
              <div class="stat-value"><fmt:formatNumber value="${tableau.global.chiffreAffaires}" maxFractionDigits="0"/> Ar</div>
              <div class="stat-label">Chiffre d'affaires</div>
            </article>
            <article class="stat-card">
              <div class="stat-top"><span class="icone-statistique vert">C</span></div>
              <div class="stat-value"><fmt:formatNumber value="${tableau.global.paddyCollecte}" maxFractionDigits="2"/> kg</div>
              <div class="stat-label">Paddy collecté</div>
            </article>
            <article class="stat-card">
              <div class="stat-top"><span class="icone-statistique orange">P</span></div>
              <div class="stat-value"><fmt:formatNumber value="${tableau.global.paddyTransforme}" maxFractionDigits="2"/> kg</div>
              <div class="stat-label">Paddy transformé</div>
            </article>
          </div>

          <article class="card carte-evolution">
            <div class="card-head"><h3>Évolution mensuelle</h3><span>Maximum 12 mois</span></div>
            <div class="card-body">
              <div class="legende-statistique">
                <span><i class="couleur-transaction"></i>Transactions</span>
                <span><i class="couleur-collecte"></i>Collecte</span>
                <span><i class="couleur-transformation"></i>Transformation</span>
              </div>
              <div class="evolution-liste">
                <c:forEach items="${tableau.evolution}" var="point">
                  <div class="evolution-ligne">
                    <strong><c:out value="${point.mois}"/></strong>
                    <div class="barres-mensuelles">
                      <div title="${point.transactions} transaction(s)"><span class="barre transaction" style="width:${point.largeurTransactions}%"></span></div>
                      <div title="${point.collecte} kg collectés"><span class="barre collecte" style="width:${point.largeurCollecte}%"></span></div>
                      <div title="${point.transformation} kg transformés"><span class="barre transformation" style="width:${point.largeurTransformation}%"></span></div>
                    </div>
                    <small><fmt:formatNumber value="${point.transactions}"/> / <fmt:formatNumber value="${point.collecte}" maxFractionDigits="0"/> / <fmt:formatNumber value="${point.transformation}" maxFractionDigits="0"/></small>
                  </div>
                </c:forEach>
              </div>
            </div>
          </article>
        </section>

        <div class="statistiques-grille">
          <section aria-labelledby="titre-transactions">
            <div class="titre-section-statistique">
              <div><span>02</span><div><h2 id="titre-transactions">Transactions de fournitures</h2><p>Résultats par type de paiement</p></div></div>
            </div>
            <article class="card">
              <div class="graphique-transactions">
                <canvas id="graphique-transactions" aria-label="Répartition des transactions par type"></canvas>
              </div>
              <div class="table-wrap">
                <table class="dt" id="tableau-statistiques-transactions">
                  <thead><tr><th>Type</th><th>Transactions</th><th>Quantité</th><th>Montant</th></tr></thead>
                  <tbody>
                    <c:forEach items="${tableau.transactions}" var="ligne">
                      <tr data-statistique="transaction" data-nombre="${ligne.nombre}">
                        <td><strong><c:out value="${ligne.type}"/></strong></td>
                        <td><fmt:formatNumber value="${ligne.nombre}"/></td>
                        <td><fmt:formatNumber value="${ligne.quantite}"/></td>
                        <td><fmt:formatNumber value="${ligne.montant}" maxFractionDigits="0"/> Ar</td>
                      </tr>
                    </c:forEach>
                    <c:if test="${empty tableau.transactions}"><tr><td colspan="4" class="dt-empty">Aucun type de transaction enregistré.</td></tr></c:if>
                  </tbody>
                </table>
              </div>
            </article>
          </section>

          <section aria-labelledby="titre-collectes">
            <div class="titre-section-statistique">
              <div><span>03</span><div><h2 id="titre-collectes">Collectes de paddy</h2><p>Volume, qualité et coût</p></div></div>
            </div>
            <article class="card card-body indicateurs-secondaires">
              <div><span>Lots réceptionnés</span><strong><fmt:formatNumber value="${tableau.collecte.nombreLots}"/></strong></div>
              <div><span>Quantité totale</span><strong><fmt:formatNumber value="${tableau.collecte.quantiteTotale}" maxFractionDigits="2"/> kg</strong></div>
              <div><span>Coût total</span><strong><fmt:formatNumber value="${tableau.collecte.coutTotal}" maxFractionDigits="0"/> Ar</strong></div>
              <div><span>Humidité moyenne</span><strong><fmt:formatNumber value="${tableau.collecte.humiditeMoyenne}" maxFractionDigits="2"/> %</strong></div>
              <div class="indicateur-alerte"><span>Réduction appliquée</span><strong><fmt:formatNumber value="${tableau.collecte.reductionEstimee}" maxFractionDigits="0"/> Ar</strong></div>
            </article>
          </section>
        </div>

        <section aria-labelledby="titre-transformations">
          <div class="titre-section-statistique">
            <div><span>04</span><div><h2 id="titre-transformations">Paddy transformé</h2><p>Production réelle comparée aux proportions attendues 65 / 20 / 10 / 5</p></div></div>
          </div>
          <div class="statistiques-grille transformation-grille">
            <article class="card card-body indicateurs-secondaires">
              <div><span>Lots transformés</span><strong><fmt:formatNumber value="${tableau.transformation.nombreLots}"/></strong></div>
              <div><span>Paddy consommé</span><strong><fmt:formatNumber value="${tableau.transformation.paddyTransforme}" maxFractionDigits="2"/> kg</strong></div>
              <div><span>Produits obtenus</span><strong><fmt:formatNumber value="${tableau.transformation.produitsObtenus}" maxFractionDigits="2"/> kg</strong></div>
              <div><span>Coût de transformation</span><strong><fmt:formatNumber value="${tableau.transformation.coutTransformation}" maxFractionDigits="0"/> Ar</strong></div>
            </article>
            <article class="card card-body repartition-produits">
              <c:forEach items="${tableau.transformation.repartition}" var="ligne">
                <div class="produit-ligne">
                  <div><strong><c:out value="${ligne.produit}"/></strong><span><fmt:formatNumber value="${ligne.quantite}" maxFractionDigits="2"/> kg — <fmt:formatNumber value="${ligne.pourcentage}" maxFractionDigits="1"/> %</span></div>
                  <div class="rail-produit"><span style="width:${ligne.pourcentage}%"></span></div>
                </div>
              </c:forEach>
              <c:if test="${empty tableau.transformation.repartition}"><p class="dt-empty">Aucun produit enregistré.</p></c:if>
            </article>
          </div>
        </section>
      <jsp:include page="../include/footer.jsp"/>
  </div>
  <script src="${racine}/webjars/chart.js/4.4.1/dist/chart.umd.js"></script>
  <script src="${racine}/assets/js/statistiques-graphiques.js"></script>
</body>
</html>
