<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouvelle collecte - VOLY VARY</title>
    <link rel="stylesheet" href="/assets/css/variables.css">
    <link rel="stylesheet" href="/assets/css/base.css">
    <link rel="stylesheet" href="/assets/css/components.css">
    <link rel="stylesheet" href="/assets/css/collecte-2.css">
</head>
<body>

<div class="app-shell">
    <jsp:include page="../include/sidebar.jsp" />
    <div class="main-content">
        <jsp:include page="../include/recherche.jsp" />
        <main class="page-body">

    <div class="page-heading">
        <div>
            <h1>Collecte : achat de paddy</h1>
            <p>Enregistrer un nouveau lot achete aupres d'un producteur</p>
        </div>
        <a href="/collectes/valides" class="btn btn-outline btn-sm">Retour a la liste</a>
    </div>

    <div class="section-card">
        <div class="card">
            <div class="card-body">

                <c:if test="${not empty erreur}">
                    <div class="alert alert-error">
                        <span>${erreur}</span>
                    </div>
                </c:if>

                <form action="/collectes/calculer" method="post" id="form-collecte">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="form-grid">
                        <div class="form-field full">
                            <label>Date et heure</label>
                            <input type="datetime-local" id="date-heure" name="dateHeure" required>
                        </div>
                    </div>

                    <div class="client-box">
                        <div class="form-field">
                            <label for="reference">Reference client :</label>
                            <input type="text" id="reference" name="reference" placeholder="${derniereReference}" required onblur="rechercherClient()">
                            <small>Utilisez une référence du répertoire ou saisissez-en une nouvelle pour créer le client avec cette collecte.</small>
                        </div>
                        <div class="form-field">
                            <label>Nom</label>
                            <input type="text" id="nom" name="nom" required>
                        </div>
                        <div class="form-field">
                            <label>Prenom</label>
                            <input type="text" id="prenom" name="prenom" required>
                        </div>
                        <div class="form-field">
                            <label>Numero telephone</label>
                            <input type="text" id="telephone" name="telephone" placeholder="032 00 000 00" required>
                        </div>
                        <a class="btn btn-outline btn-sm" href="/clients">Consulter le répertoire des clients</a>
                    </div>

                    <div class="form-grid">
                        <div class="form-field">
                            <label>Quantite de Paddy (kg)</label>
                            <input type="number" id="quantite" name="quantite" min="1" step="0.01" required>
                        </div>
                        <div class="form-field">
                            <label>Taux d'humidite (%)</label>
                            <input type="number" id="humidite" name="tauxHumidite" min="0" max="100" step="0.1" required>
                        </div>
                        <div class="form-field">
                            <label>Prix unitaire (Ar/kg)</label>
                            <input type="number" id="prix-unitaire" name="prixUnitaire" step="0.01" required>
                        </div>
                    </div>

                    <div class="flow-actions">
                        <button type="submit" class="btn btn-primary">Calculer et valider</button>
                    </div>
                </form>

                <div class="form-grid" style="margin-top: 2rem; padding-top: 2rem; border-top: 1px solid var(--color-gray-200);">
                    <div class="form-field full">
                        <label>Importer depuis Excel</label>
                        <div class="flow-actions">
                            <input type="file" id="fichier-excel" accept=".xlsx">
                            <button type="button" class="btn btn-outline" onclick="importerExcel()">Remplir depuis Excel</button>
                        </div>
                        <div id="message-import" style="margin-top: 0.75rem;"></div>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <script src="/assets/js/client.js"></script>
    <script src="/assets/js/collecte.js"></script>
        </main>
    </div>
</div>

<script src="/assets/js/sidebar.js"></script>
</body>
</html>
