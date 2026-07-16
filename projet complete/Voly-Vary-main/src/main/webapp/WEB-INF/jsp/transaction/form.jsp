<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Nouvelle transaction - VOLY VARY</title>
<link rel="stylesheet" href="/assets/css/variables.css">
<link rel="stylesheet" href="/assets/css/base.css">
<link rel="stylesheet" href="/assets/css/components.css">
</head>
<body>
    <div class="app-shell">
        <jsp:include page="../include/sidebar.jsp"><jsp:param name="activePage" value="nouvelle"/></jsp:include>
        <div class="main-content">
            <jsp:include page="../include/recherche.jsp" />
            <main class="page-body">
                <div class="page-heading">
                    <div><h1>Transaction de fourniture</h1><p>Nouvelle vente, vente a credit ou location</p></div>
                    <a href="/transactions" class="btn btn-outline btn-sm">Retour a la liste</a>
                </div>

                <c:if test="${not empty erreur}">
                    <div style="padding:12px 16px;border-radius:var(--radius-sm);background:var(--color-red-light);color:var(--color-red);font-size:var(--fs-sm);margin-bottom:16px;">
                    <c:out value="${erreur}"/>
                    </div>
                </c:if>

                <div class="card flow-card">
                    <div class="card-body">
                        <form method="post" action="/transaction/facture">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <input type="hidden" name="referenceTransaction" value="${referenceTransaction}">

                            <div class="form-field full">
                                <label>Type de transaction</label>
                                <div class="check-group">
                                    <c:forEach items="${listeTypesTransaction}" var="typeTransaction">
                                        <label class="check-option">
                                            <input type="radio" name="typeTransactionId" value="${typeTransaction.id}" <c:if test="${typeTransaction.id == typeTransactionId}">checked</c:if>>
                                            <c:out value="${typeTransaction.libelle}"/>
                                        </label>
                                    </c:forEach>
                                </div>
                            </div>

                            <div class="form-grid">
                                <div class="form-field full">
                                    <label>Date et heure</label>
                                    <input type="datetime-local" name="dateTransaction" value="${dateCourante}" required>
                                </div>
                                <div class="form-field full">
                                    <label>Reference transaction</label>
                                    <input type="text" value="${referenceTransaction}" readonly style="background:var(--color-gray-50);">
                                </div>
                            </div>

                            <div class="client-box">
                                <div class="form-field">
                                    <label for="reference">Reference client :</label>
                                    <input type="text" id="reference" name="referenceClient" value="<c:out value='${referenceClient}'/>" placeholder="Ex. CL001" required onblur="rechercherClient()">
                                    <small>Référence existante : les informations sont chargées. Nouvelle référence : le client sera créé à la confirmation.</small>
                                </div>
                                <div class="form-field">
                                    <label>Nom</label>
                                    <input type="text" id="nom" name="nomClient" value="<c:out value='${nomClient}'/>" required>
                                </div>
                                <div class="form-field">
                                    <label>Prenom</label>
                                    <input type="text" id="prenom" name="prenomClient" value="<c:out value='${prenomClient}'/>" required>
                                </div>
                                <div class="form-field">
                                    <label>Numero telephone</label>
                                    <input type="text" id="telephone" name="telephoneClient" value="<c:out value='${telephoneClient}'/>" placeholder="032 00 000 00" required>
                                </div>
                                <a class="btn btn-outline btn-sm" href="/clients">Consulter le répertoire des clients</a>
                            </div>

                            <div class="form-field full">
                            <label>Categorie</label>
                            <div style="display:flex;gap:8px;align-items:flex-end;">
                                <select name="categorieFournitureId" required style="flex:1;">
                                <option value="">Selectionner</option>
                                <c:forEach items="${listeCategoriesFourniture}" var="cat">
                                    <option value="${cat.id}" <c:if test="${cat.id == categorieFournitureId}">selected</c:if>><c:out value="${cat.libelle}"/></option>
                                </c:forEach>
                                </select>
                                <button type="submit" formaction="/transaction/charger-fournitures" class="btn btn-outline btn-sm">Charger</button>
                            </div>
                            </div>

                            <div class="line-items">
                            <c:forEach items="${formLignes}" var="ligne" varStatus="status">
                                <div class="line-item">
                                <div class="form-field">
                                    <label>Fourniture</label>
                                    <select name="idFourniture" class="line-fourniture" required>
                                    <option value="">Selectionner</option>
                                    <c:forEach items="${formFournitures}" var="f">
                                        <option value="${f.idFourniture}" <c:if test="${f.idFourniture == ligne.idFourniture}">selected</c:if>><c:out value="${f.referenceFourniture}"/> - <c:out value="${f.prixUnitaire}"/></option>
                                    </c:forEach>
                                    </select>
                                </div>
                                <div class="form-field">
                                    <label>Quantite</label>
                                    <input type="number" name="quantite" class="line-qte" min="1" value="${ligne.quantite}">
                                </div>
                                <button type="submit" formaction="/transaction/retirer-ligne" class="line-remove" name="removeLine" value="${status.index}" aria-label="Retirer">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/></svg>
                                </button>
                                </div>
                            </c:forEach>
                            </div>

                            <div style="display:flex;gap:var(--space-4);align-items:center;flex-wrap:wrap;">
                            <button type="submit" formaction="/transaction/ajouter-ligne" class="line-add">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
                                Ajouter une fourniture
                            </button>
                            </div>

                            <div class="flow-actions" style="margin-top:24px;">
                            <button type="submit" class="btn btn-primary">Faire la transaction</button>
                            </div>
                        </form>

                        <form method="post" action="/transaction/importer-fichier" enctype="multipart/form-data" style="margin-top:16px;">
                            <%-- L'import modifie les lignes de transaction et nécessite donc le jeton CSRF. --%>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <input type="hidden" name="referenceTransaction" value="${referenceTransaction}">
                            <input type="hidden" name="dateTransaction" value="${dateCourante}">
                            <input type="hidden" name="typeTransactionId" value="${typeTransactionId}">
                            <div style="display:flex;gap:8px;align-items:center;">
                            <input type="file" name="fichier" accept=".csv,.xlsx,.xls" required>
                            <button type="submit" class="line-add" style="color:var(--color-green);">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                                Importer un fichier
                            </button>
                            </div>
                        </form>
                    </div>
                </div>

        <jsp:include page="../include/footer.jsp"/>
    </div>
    <script src="/assets/js/client.js"></script>
</body>
</html>
