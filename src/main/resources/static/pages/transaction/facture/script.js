const pageBody = renderShell('transaction');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const wrap = document.getElementById('recap-wrap');
  const viewId = new URLSearchParams(location.search).get('id');

  if (viewId) {
    // consultation d'une transaction deja confirmee (lecture seule)
    const saved = storeLoad('vv_transactions', []).find(t => String(t.id) === viewId);
    if (!saved) {
      wrap.innerHTML = `<div class="card-body flow-empty">Transaction introuvable.<br><a class="detail-link" href="../index.html">Retour a la liste</a></div>`;
    } else {
      wrap.innerHTML = `
        <div class="card-head"><h3>Transaction ${saved.reference}</h3></div>
        <div class="card-body">
          <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(saved.date)}</span></div>
          <div class="recap-row"><span class="recap-label">Type</span><span class="recap-value">${saved.type}</span></div>
          <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value">${saved.refClient}</span></div>
          <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${saved.nom.toUpperCase()}</span></div>
          <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value">${saved.prenom}</span></div>
          <div class="recap-row"><span class="recap-label">Numero tel</span><span class="recap-value">${saved.telephone}</span></div>
          <div class="recap-row"><span class="recap-label">Statut</span><span class="recap-value">${saved.statut}</span></div>
          <table class="recap-table">
            <thead><tr><th>Fournitures</th><th>Quantite</th><th>Montant</th></tr></thead>
            <tbody>
              ${saved.lignes.map(l => `<tr><td>${l.fourniture}</td><td>${l.quantite}</td><td>${formatAr(l.montant)}</td></tr>`).join('')}
              <tr class="total-row"><td></td><td>Total</td><td>${formatAr(saved.montant)}</td></tr>
            </tbody>
          </table>
          <div class="flow-actions">
            <a href="../index.html" class="btn btn-outline">Retour a la liste</a>
            <button class="btn btn-primary" onclick="window.print()">Imprimer</button>
          </div>
        </div>`;
    }
  } else {

  const draft = draftLoad('vv_draft_transaction');

  if (!draft) {
    wrap.innerHTML = `<div class="card-body flow-empty">Aucune transaction en cours.<br><a class="detail-link" href="../nouvelle/index.html">Creer une nouvelle transaction</a></div>`;
  } else {
    wrap.innerHTML = `
      <div class="card-head"><h3>Transaction de fourniture : facture transaction</h3></div>
      <div class="card-body">
        <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(draft.dateHeure)}</span></div>
        <div class="recap-row"><span class="recap-label">Type</span><span class="recap-value">${draft.type}</span></div>
        <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value">${draft.refClient}</span></div>
        <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${draft.nom.toUpperCase()}</span></div>
        <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value">${draft.prenom}</span></div>
        <div class="recap-row"><span class="recap-label">Numero tel</span><span class="recap-value">${draft.telephone}</span></div>
        <div class="recap-row"><span class="recap-label">Categorie</span><span class="recap-value">${draft.categorie}</span></div>

        <table class="recap-table">
          <thead><tr><th>Fournitures</th><th>Quantite</th><th>Montant</th></tr></thead>
          <tbody>
            ${draft.lignes.map(l => `<tr><td>${l.fourniture}</td><td>${l.quantite}</td><td>${formatAr(l.montant)}</td></tr>`).join('')}
            <tr class="total-row"><td></td><td>Total</td><td>${formatAr(draft.total)}</td></tr>
          </tbody>
        </table>

        <div class="flow-actions">
          <button class="btn btn-outline" id="btn-annuler">Annuler la transaction</button>
          <button class="btn btn-primary" id="btn-confirmer">Confirmer la transaction</button>
        </div>
      </div>`;

    document.getElementById('btn-annuler').addEventListener('click', () => {
      draftClear('vv_draft_transaction');
      toastQueue('Transaction annulee', 'warning');
      window.location.href = '../index.html';
    });

    document.getElementById('btn-confirmer').addEventListener('click', () => {
      const items = storeLoad('vv_transactions', []);
      const nextNum = items.length + 1;
      const record = {
        id: Date.now(),
        reference: `T${String(nextNum).padStart(3, '0')}`,
        refClient: draft.refClient,
        nom: draft.nom,
        prenom: draft.prenom,
        telephone: draft.telephone,
        type: draft.type,
        categorie: draft.categorie,
        lignes: draft.lignes,
        quantite: draft.lignes.reduce((s, l) => s + l.quantite, 0),
        montant: draft.total,
        date: draft.dateHeure.slice(0, 10),
        statut: 'Validee',
      };
      items.unshift(record);
      storeSave('vv_transactions', items);
      draftClear('vv_draft_transaction');
      toastQueue('Transaction confirmee avec succes', 'success');
      window.location.href = '../index.html';
    });
  }
  }
}
