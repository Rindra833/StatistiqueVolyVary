const pageBody = renderShell('distribution');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const wrap = document.getElementById('recap-wrap');
  const viewId = new URLSearchParams(location.search).get('id');

  if (viewId) {
    const saved = storeLoad('vv_distributions', []).find(d => String(d.id) === viewId);
    if (!saved) {
      wrap.innerHTML = `<div class="card-body flow-empty">Commande introuvable.<br><a class="detail-link" href="../index.html">Retour a la liste</a></div>`;
    } else {
      wrap.innerHTML = `
        <div class="card-head"><h3>Distribution ${saved.reference}</h3></div>
        <div class="card-body">
          <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(saved.date)}</span></div>
          <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value">${saved.refClient}</span></div>
          <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${saved.nom.toUpperCase()}</span></div>
          <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value">${saved.prenom}</span></div>
          <div class="recap-row"><span class="recap-label">Numero tel</span><span class="recap-value">${saved.telephone}</span></div>
          <div class="recap-row"><span class="recap-label">Lieu</span><span class="recap-value">${saved.lieu}</span></div>
          <div class="recap-row"><span class="recap-label">Livreur</span><span class="recap-value">${saved.livreur}</span></div>
          <div class="recap-row"><span class="recap-label">Statut</span><span class="recap-value">${saved.statut}</span></div>
          <table class="recap-table">
            <thead><tr><th>Produits</th><th>Quantite</th><th>Total</th></tr></thead>
            <tbody>
              ${saved.produits.map(p => `<tr><td>${p.produit}</td><td>${p.quantite}</td><td>${formatAr(p.montant)}</td></tr>`).join('')}
              <tr class="total-row"><td></td><td>Montant total</td><td>${formatAr(saved.montant)}</td></tr>
            </tbody>
          </table>
          <div class="flow-actions">
            <a href="../index.html" class="btn btn-outline">Retour a la liste</a>
            <button class="btn btn-primary" onclick="window.print()">Imprimer</button>
          </div>
        </div>`;
    }
  } else {
    const draft = draftLoad('vv_draft_distribution');
    if (!draft) {
      wrap.innerHTML = `<div class="card-body flow-empty">Aucune commande en cours.<br><a class="detail-link" href="../nouvelle/index.html">Nouvelle distribution</a></div>`;
    } else {
      wrap.innerHTML = `
        <div class="card-head"><h3>Distribution : facture</h3></div>
        <div class="card-body">
          <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(draft.dateHeure)}</span></div>
          <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value">${draft.refClient}</span></div>
          <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${draft.nom.toUpperCase()}</span></div>
          <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value">${draft.prenom}</span></div>
          <div class="recap-row"><span class="recap-label">Numero tel</span><span class="recap-value">${draft.telephone}</span></div>
          <div class="recap-row"><span class="recap-label">Lieu</span><span class="recap-value">${draft.lieu}</span></div>
          <div class="recap-row"><span class="recap-label">Livreur</span><span class="recap-value">${draft.livreur}</span></div>
          <table class="recap-table">
            <thead><tr><th>Produits</th><th>Quantite</th><th>Total</th></tr></thead>
            <tbody>
              ${draft.produits.map(p => `<tr><td>${p.produit}</td><td>${p.quantite}</td><td>${formatAr(p.montant)}</td></tr>`).join('')}
              <tr class="total-row"><td></td><td>Montant total</td><td>${formatAr(draft.total)}</td></tr>
            </tbody>
          </table>
          <div class="flow-actions">
            <button class="btn btn-outline" id="btn-annuler">Annuler</button>
            <button class="btn btn-primary" id="btn-terminer">Terminer la commande</button>
          </div>
        </div>`;

      document.getElementById('btn-annuler').addEventListener('click', () => {
        draftClear('vv_draft_distribution');
        toastQueue('Commande annulee', 'warning');
        window.location.href = '../index.html';
      });

      document.getElementById('btn-terminer').addEventListener('click', () => {
        const items = storeLoad('vv_distributions', []);
        const nextNum = items.length + 1;
        items.unshift({
          id: Date.now(),
          reference: `FC${String(nextNum).padStart(3, '0')}`,
          refClient: draft.refClient,
          nom: draft.nom,
          prenom: draft.prenom,
          telephone: draft.telephone,
          lieu: draft.lieu,
          livreur: draft.livreur,
          produits: draft.produits,
          quantite: draft.produits.reduce((s, p) => s + p.quantite, 0),
          montant: draft.total,
          date: draft.dateHeure.slice(0, 10),
          statut: 'En cours',
        });
        storeSave('vv_distributions', items);
        draftClear('vv_draft_distribution');
        toastQueue('Commande terminee avec succes', 'success');
        window.location.href = '../index.html';
      });
    }
  }
}
