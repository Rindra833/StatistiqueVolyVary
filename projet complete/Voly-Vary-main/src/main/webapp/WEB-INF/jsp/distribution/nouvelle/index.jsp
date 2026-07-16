<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Distribution : prise de commande - VOLY VARY</title>
  <link rel="stylesheet" href="/assets/css/variables.css">
  <link rel="stylesheet" href="/assets/css/base.css">
  <link rel="stylesheet" href="/assets/css/components.css">
  <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<div class="app-shell">
  <jsp:include page="../../include/sidebar.jsp"><jsp:param name="activePage" value="distribution"/></jsp:include>
  <div class="main-content">
    <jsp:include page="../../include/recherche.jsp" />
    <main class="page-body">

      <div class="page-heading">
        <div>
          <h1>Distribution : prise de commande</h1>
          <p>Enregistrer une nouvelle commande client</p>
        </div>
        <a href="${ctx}/distribution/listeFactures" class="btn btn-outline btn-sm">Retour à la liste</a>
      </div>

<style>
    #line-items .line-item { grid-template-columns: 1.4fr 0.8fr auto; }
</style>
<div class="card flow-card" style="margin-bottom:16px;">
    <div class="card-body">
        <label>Importer depuis Excel</label>
        <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap;margin-top:6px;">
            <input type="file" id="fichier-excel" accept=".xlsx">
            <button type="button" class="btn btn-outline" onclick="importerExcel()">Remplir depuis Excel</button>
        </div>
        <div id="message-import" style="margin-top:10px;"></div>
        <p style="color:var(--color-gray-500);font-size:var(--fs-xs);margin-top:6px;">
            Colonnes attendues, dans l'ordre : Référence client, Nom, Prénom, Téléphone, Produit, Quantité, Lieu, Livreur.
        </p>
    </div>
</div>

<div class="card flow-card">
    <div class="card-body">
        <form method="post" action="${ctx}/distribution/validerCommande" id="form-distribution">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-field full">
                <label>Référence client</label>
                <input name="refClient" id="in-ref-client" placeholder="CL001" autocomplete="off" required>
                <small>Une référence inconnue créera automatiquement le client lors de la validation.</small>
                <small id="ref-client-hint" style="display:block;margin-top:4px;font-size:var(--fs-xs);color:var(--color-gray-500);"></small>
                <a class="btn btn-outline btn-sm" href="${ctx}/clients">Consulter le répertoire des clients</a>
            </div>

            <div class="client-box">
                <div class="form-field"><label>Nom</label><input name="nom" id="in-nom" required></div>
                <div class="form-field"><label>Prénom</label><input name="prenom" id="in-prenom" required></div>
                <div class="form-field"><label>Numéro téléphone</label><input name="tel" id="in-tel" placeholder="032 00 000 00" required></div>
            </div>

            <div class="line-items" id="line-items">
                <div class="line-item">
                    <div class="form-field">
                        <label>Produit</label>
                        <select name="idsProduits">
                            <c:forEach var="p" items="${produits}">
                                <option value="${p.idProduit}">${p.nomProduit}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>Quantité</label>
                        <input type="number" name="quantites" min="1" value="1" required>
                    </div>
                    <button type="button" class="line-remove" aria-label="Retirer cette ligne">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/></svg>
                    </button>
                </div>
            </div>
            <button type="button" class="line-add" id="btn-add-line">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
                Ajouter un produit
            </button>

            <div class="form-grid">
                <div class="form-field">
                    <label>Lieu</label>
                    <select name="idLieu" required>
                        <c:forEach var="lieu" items="${lieux}">
                            <option value="${lieu.id}">${lieu.nom}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-field">
                    <label>Livreur</label>
                    <select name="idLivreur" required>
                        <c:forEach var="livreur" items="${livreurs}">
                            <option value="${livreur.id}">${livreur.matriculeVehicule}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="flow-actions">
                <button type="submit" class="btn btn-primary">Valider</button>
            </div>
        </form>
    </div>
</div>

<script>
  (function () {
    var linesContainer = document.getElementById('line-items');
    var produitsOptions = document.querySelector('#line-items select[name="idsProduits"]').innerHTML;

    function bindRemove(row) {
      row.querySelector('.line-remove').addEventListener('click', function () {
        if (linesContainer.children.length > 1) {
          row.remove();
        } else {
          toast('Au moins un produit est requis', 'warning');
        }
      });
    }

    document.querySelectorAll('#line-items .line-item').forEach(bindRemove);

    document.getElementById('btn-add-line').addEventListener('click', function () {
      var row = document.createElement('div');
      row.className = 'line-item';
      row.innerHTML =
        '<div class="form-field"><label>Produit</label><select name="idsProduits">' + produitsOptions + '</select></div>' +
        '<div class="form-field"><label>Quantité</label><input type="number" name="quantites" min="1" value="1" required></div>' +
        '<button type="button" class="line-remove" aria-label="Retirer cette ligne">' +
        '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/></svg></button>';
      linesContainer.appendChild(row);
      bindRemove(row);
    });

    document.getElementById('form-distribution').addEventListener('submit', function (e) {
      if (!linesContainer.querySelector('.line-item')) {
        e.preventDefault();
        toast('Ajoutez au moins un produit', 'warning');
      }
    });
  })();

  (function () {
    var refInput = document.getElementById('in-ref-client');
    var nomInput = document.getElementById('in-nom');
    var prenomInput = document.getElementById('in-prenom');
    var telInput = document.getElementById('in-tel');
    var hint = document.getElementById('ref-client-hint');
    var ctx = '${ctx}';
    var debounce;

    function chercherClient() {
      var ref = refInput.value.trim();
      if (!ref) {
        hint.textContent = '';
        return;
      }
      fetch(ctx + '/distribution/rechercherClient?ref=' + encodeURIComponent(ref))
        .then(function (response) {
          if (response.status === 404) {
            hint.textContent = 'Nouveau client : renseignez ses informations ci-dessous.';
            hint.style.color = 'var(--color-gray-500)';
            return null;
          }
          return response.json();
        })
        .then(function (client) {
          if (client) {
            nomInput.value = client.nom || '';
            prenomInput.value = client.prenom || '';
            telInput.value = client.telephone || '';
            hint.textContent = 'Client existant chargé automatiquement.';
            hint.style.color = 'var(--color-green)';
            toast('Client ' + ref + ' trouvé', 'success');
          }
        })
        .catch(function () {
          hint.textContent = '';
        });
    }

    refInput.addEventListener('blur', chercherClient);
    refInput.addEventListener('input', function () {
      clearTimeout(debounce);
      debounce = setTimeout(chercherClient, 500);
    });
  })();

  function selectionnerParTexte(select, texte) {
    if (!texte) return false;
    var texteNormalise = texte.trim().toLowerCase();
    for (var i = 0; i < select.options.length; i++) {
      if (select.options[i].text.trim().toLowerCase() === texteNormalise) {
        select.selectedIndex = i;
        return true;
      }
    }
    return false;
  }

  function importerExcel() {
    var fichier = document.getElementById('fichier-excel').files[0];
    var messageDiv = document.getElementById('message-import');
    var ctx = '${ctx}';

    if (!fichier) {
      messageDiv.innerHTML = '<span style="color:var(--color-red);">Veuillez choisir un fichier Excel</span>';
      return;
    }
    if (!fichier.name.endsWith('.xlsx')) {
      messageDiv.innerHTML = '<span style="color:var(--color-red);">Le fichier doit être au format .xlsx</span>';
      return;
    }

    var formData = new FormData();
    formData.append('fichier', fichier);
    messageDiv.innerHTML = '<span style="color:var(--color-gray-500);">Lecture du fichier en cours...</span>';

    /*
     * Cet import utilise fetch : le navigateur n'ajoute pas automatiquement le jeton du formulaire.
     * Sa copie dans FormData permet à Spring Security d'autoriser la requête multipart.
     */
    var jetonSecurite = document.querySelector('#form-distribution input[name="_csrf"]');
    if (jetonSecurite) {
      formData.append(jetonSecurite.name, jetonSecurite.value);
    }

    fetch(ctx + '/distribution/lire-excel', { method: 'POST', body: formData })
      .then(function (response) { return response.json(); })
      .then(function (data) {
        if (data.erreur) {
          messageDiv.innerHTML = '<span style="color:var(--color-red);">' + data.erreur + '</span>';
          return;
        }

        document.getElementById('in-ref-client').value = data.refClient || '';
        document.getElementById('in-nom').value = data.nom || '';
        document.getElementById('in-prenom').value = data.prenom || '';
        document.getElementById('in-tel').value = data.telephone || '';

        var premiereLigne = document.querySelector('#line-items .line-item');
        if (premiereLigne) {
          selectionnerParTexte(premiereLigne.querySelector('select[name="idsProduits"]'), data.produit);
          var qteInput = premiereLigne.querySelector('input[name="quantites"]');
          if (data.quantite) qteInput.value = data.quantite;
        }
        selectionnerParTexte(document.querySelector('select[name="idLieu"]'), data.lieu);
        selectionnerParTexte(document.querySelector('select[name="idLivreur"]'), data.livreur);

        messageDiv.innerHTML = '<span style="color:var(--color-green);">Formulaire rempli depuis le fichier Excel.</span>';
        toast('Formulaire rempli depuis Excel', 'success');
      })
      .catch(function () {
        messageDiv.innerHTML = '<span style="color:var(--color-red);">Erreur lors de la lecture du fichier</span>';
      });
  }
</script>

    <jsp:include page="../../include/footer.jsp"/>
</div>
<script src="/assets/js/toast.js"></script>
<script src="/assets/js/modal.js"></script>
<script src="/assets/js/sidebar.js"></script>
</body>
</html>
