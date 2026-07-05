const pageBody = renderShell('collecte');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const PRIX_BASE = 1500; // Ar/kg, taux d'humidite de reference

  // grille de reduction selon le taux d'humidite (table "reduction" du modele)
  function calculerPrixKg(humidite) {
    if (humidite <= 13) return PRIX_BASE;
    if (humidite <= 16) return Math.round(PRIX_BASE * 0.95);
    return Math.round(PRIX_BASE * 0.9);
  }

  const wrap = document.getElementById('recap-wrap');
  const viewId = new URLSearchParams(location.search).get('id');

  if (viewId) {
    const saved = storeLoad('vv_collectes', []).find(c => String(c.id) === viewId);
    if (!saved) {
      wrap.innerHTML = `<div class="card-body flow-empty">Lot introuvable.<br><a class="detail-link" href="../index.html">Retour a la liste</a></div>`;
    } else {
      wrap.innerHTML = `
        <div class="card-head"><h3>Lot ${saved.reference}</h3></div>
        <div class="card-body">
          <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(saved.date)}</span></div>
          <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value">${saved.refClient}</span></div>
          <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${saved.nom.toUpperCase()}</span></div>
          <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value">${saved.prenom}</span></div>
          <div class="recap-row"><span class="recap-label">Numero tel</span><span class="recap-value">${saved.telephone}</span></div>
          <div class="recap-row"><span class="recap-label">Quantite de Paddy</span><span class="recap-value">${saved.quantite} kg</span></div>
          <div class="recap-row"><span class="recap-label">Taux d'humidite</span><span class="recap-value">${saved.humidite} %</span></div>
          <div class="recap-row"><span class="recap-label">Prix/kg</span><span class="recap-value">${formatAr(saved.prixUnitaire)}</span></div>
          <div class="recap-row"><span class="recap-label">Montant total</span><span class="recap-value">${formatAr(saved.montant)}</span></div>
          <div class="flow-actions">
            <a href="../index.html" class="btn btn-outline">Retour a la liste</a>
            <button class="btn btn-primary" onclick="window.print()">Imprimer</button>
          </div>
        </div>`;
    }
  } else {
    const draft = draftLoad('vv_draft_collecte');
    if (!draft) {
      wrap.innerHTML = `<div class="card-body flow-empty">Aucune collecte en cours.<br><a class="detail-link" href="../nouvelle/index.html">Faire une nouvelle collecte</a></div>`;
    } else {
      const prixKg = calculerPrixKg(draft.humidite);
      const montant = prixKg * draft.quantite;
      wrap.innerHTML = `
        <div class="card-head"><h3>Collecte : facture / detail lot de paddy</h3></div>
        <div class="card-body">
          <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(draft.dateHeure)}</span></div>
          <div class="recap-row"><span class="recap-label">Reference client</span><span class="recap-value">${draft.refClient}</span></div>
          <div class="recap-row"><span class="recap-label">Nom</span><span class="recap-value">${draft.nom.toUpperCase()}</span></div>
          <div class="recap-row"><span class="recap-label">Prenom</span><span class="recap-value">${draft.prenom}</span></div>
          <div class="recap-row"><span class="recap-label">Numero tel</span><span class="recap-value">${draft.telephone}</span></div>
          <div class="recap-row"><span class="recap-label">Quantite de Paddy</span><span class="recap-value">${draft.quantite} kg</span></div>
          <div class="recap-row"><span class="recap-label">Taux d'humidite</span><span class="recap-value">${draft.humidite} %</span></div>
          <div class="recap-row"><span class="recap-label">Prix/kg</span><span class="recap-value">${formatAr(prixKg)}</span></div>
          <div class="recap-row"><span class="recap-label">Montant total</span><span class="recap-value">${formatAr(montant)}</span></div>
          <div class="flow-actions">
            <button class="btn btn-outline" id="btn-annuler">Annuler l'achat</button>
            <button class="btn btn-primary" id="btn-payer">Payer</button>
          </div>
        </div>`;

      document.getElementById('btn-annuler').addEventListener('click', () => {
        draftClear('vv_draft_collecte');
        toastQueue('Achat annule', 'warning');
        window.location.href = '../index.html';
      });

      document.getElementById('btn-payer').addEventListener('click', () => {
        const items = storeLoad('vv_collectes', []);
        const nextNum = items.length + 1;
        items.unshift({
          id: Date.now(),
          reference: `LP${String(nextNum).padStart(3, '0')}`,
          refClient: draft.refClient,
          nom: draft.nom,
          prenom: draft.prenom,
          telephone: draft.telephone,
          quantite: draft.quantite,
          humidite: draft.humidite,
          prixUnitaire: prixKg,
          montant,
          date: draft.dateHeure.slice(0, 10),
          statut: 'Validee',
        });
        storeSave('vv_collectes', items);
        draftClear('vv_draft_collecte');
        toastQueue('Achat de paddy paye avec succes', 'success');
        window.location.href = '../index.html';
      });
    }
  }
}
