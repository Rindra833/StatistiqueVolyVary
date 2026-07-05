const pageBody = renderShell('transformation');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));
  document.getElementById('in-date').value = new Date().toISOString().slice(0, 16);

  // lots de paddy collectes et disponibles (non deja transformes)
  const collectes = storeLoad('vv_collectes', []);
  const dejaUtilises = new Set((storeLoad('vv_transformations', []) || []).flatMap(t => (t.lots || []).map(l => l.reference)));
  const disponibles = collectes.filter(c => !dejaUtilises.has(c.reference));

  const lotsList = document.getElementById('lots-list');
  if (disponibles.length === 0) {
    lotsList.innerHTML = `<div class="lots-empty">Aucun lot de paddy disponible. Enregistrez d'abord une collecte.</div>`;
  } else {
    lotsList.innerHTML = disponibles.map(c => `
      <label class="lot-row">
        <input type="checkbox" class="lot-check" value="${c.reference}" data-quantite="${c.quantite}">
        <span class="lot-ref">${c.reference}</span>
        <span class="lot-qte">${c.quantite} kg - ${c.nom} ${c.prenom}</span>
      </label>`).join('');
  }

  function recalcTotal() {
    const total = [...lotsList.querySelectorAll('.lot-check:checked')].reduce((s, cb) => s + Number(cb.dataset.quantite), 0);
    document.getElementById('in-quantite').value = total;
  }
  lotsList.addEventListener('change', recalcTotal);

  const PRIX_TRANSFORMATION_KG = 1000; // Ar/kg
  // repartition standard obtenue lors du decorticage (vary, akofo-bary, tofom-bary, vary madinika)
  const REPARTITION = [
    { produit: 'Vary', taux: 0.65 },
    { produit: 'Akofo-bary', taux: 0.20 },
    { produit: 'Tofom-bary', taux: 0.10 },
    { produit: 'Vary madinika', taux: 0.05 },
  ];

  document.getElementById('form-transformation').addEventListener('submit', e => {
    e.preventDefault();
    const selected = [...lotsList.querySelectorAll('.lot-check:checked')];
    if (selected.length === 0) { toast('Selectionnez au moins un lot de paddy', 'error'); return; }
    const lots = selected.map(cb => ({ reference: cb.value, quantite: Number(cb.dataset.quantite) }));
    const quantite = lots.reduce((s, l) => s + l.quantite, 0);
    const obtenues = REPARTITION.map(r => ({ produit: r.produit, quantite: Math.round(quantite * r.taux) }));

    const items = storeLoad('vv_transformations', []);
    const nextNum = items.length + 1;
    const record = {
      id: Date.now(),
      reference: `LPT${String(nextNum).padStart(3, '0')}`,
      dateHeure: e.target.dateHeure.value,
      date: e.target.dateHeure.value.slice(0, 10),
      quantite,
      lots,
      obtenues,
      prixUnitaire: PRIX_TRANSFORMATION_KG,
      montant: quantite * PRIX_TRANSFORMATION_KG,
      statut: 'Terminee',
    };
    items.unshift(record);
    storeSave('vv_transformations', items);
    toastQueue('Transformation effectuee avec succes', 'success');
    window.location.href = `../detail/index.html?id=${record.id}`;
  });
}
