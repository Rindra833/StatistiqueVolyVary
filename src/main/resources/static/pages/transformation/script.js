const pageBody = renderShell('transformation');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const SEED = [
    { id: 1, reference: 'LPT001', date: '2026-06-14', quantite: 500, prixUnitaire: 1000, montant: 500000, lots: [{ reference: 'LP001', quantite: 100 }, { reference: 'LP002', quantite: 400 }], obtenues: [{ produit: 'Vary', quantite: 325 }, { produit: 'Akofo-bary', quantite: 100 }, { produit: 'Tofom-bary', quantite: 50 }, { produit: 'Vary madinika', quantite: 25 }] },
    { id: 2, reference: 'LPT002', date: '2026-06-15', quantite: 400, prixUnitaire: 1000, montant: 400000, lots: [{ reference: 'LP002', quantite: 400 }], obtenues: [{ produit: 'Vary', quantite: 260 }, { produit: 'Akofo-bary', quantite: 80 }, { produit: 'Tofom-bary', quantite: 40 }, { produit: 'Vary madinika', quantite: 20 }] },
    { id: 3, reference: 'LPT003', date: '2026-08-30', quantite: 200, prixUnitaire: 2000, montant: 400000, lots: [{ reference: 'LP003', quantite: 200 }], obtenues: [{ produit: 'Vary', quantite: 130 }, { produit: 'Akofo-bary', quantite: 40 }, { produit: 'Tofom-bary', quantite: 20 }, { produit: 'Vary madinika', quantite: 10 }] },
  ];

  let items = storeLoad('vv_transformations', SEED);

  function renderStats() {
    const qteTotal = items.reduce((s, i) => s + i.quantite, 0);
    const depense = items.reduce((s, i) => s + i.montant, 0);
    const cards = [
      { icon: 'transformation', color: 'blue', value: items.length, label: 'Lots transformes' },
      { icon: 'transformation', color: 'green', value: `${qteTotal.toLocaleString('fr-FR')} kg`, label: 'Quantite de lot de paddy transforme' },
      { icon: 'transformation', color: 'yellow', value: formatAr(depense), label: 'Depense totale pour la transformation' },
      { icon: 'transformation', color: 'red', value: items.reduce((s, i) => s + i.lots.length, 0), label: 'Lots de paddy utilises' },
    ];
    document.getElementById('trf-stats').innerHTML = cards.map(c => `
      <div class="stat-card"><div class="stat-top"><div class="stat-icon ${c.color}">${NAV_ICONS[c.icon]}</div></div>
      <div class="stat-value">${c.value}</div><div class="stat-label">${c.label}</div></div>`).join('');
  }

  let table;
  function refresh() { storeSave('vv_transformations', items); renderStats(); table.setData(items); }

  table = new DataTable({
    container: document.getElementById('trf-table'),
    data: items, pageSize: 6, addLabel: 'Nouvelle transformation',
    searchKeys: ['reference'],
    filters: [],
    columns: [
      { key: 'reference', label: 'Reference' },
      { key: 'date', label: 'Date' },
      { key: 'quantite', label: 'Quantite', render: r => `${r.quantite} kg` },
      { key: 'prixUnitaire', label: 'Prix unitaire', render: r => formatAr(r.prixUnitaire) },
      { key: 'montant', label: 'Total', render: r => formatAr(r.montant) },
    ],
    actions: row => `
      <a class="action-icon" href="detail/index.html?id=${row.id}" aria-label="Voir detail"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg></a>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => { window.location.href = 'nouvelle/index.html'; },
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'date', label: 'Date' }, { key: 'quantite', label: 'Quantite' },
      { key: 'prixUnitaire', label: 'Prix unitaire' }, { key: 'montant', label: 'Total' },
    ], 'transformations'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'date', label: 'Date' }, { key: 'quantite', label: 'Quantite' },
      { key: 'prixUnitaire', label: 'Prix unitaire' }, { key: 'montant', label: 'Total' },
    ], 'Liste des lots de paddy transforme'),
  });

  document.getElementById('trf-table').addEventListener('click', e => {
    const delId = e.target.closest('[data-delete]')?.dataset.delete;
    if (delId) {
      const row = items.find(i => i.id == delId);
      openConfirmModal({
        title: 'Supprimer ce lot ?', message: `${row.reference} sera definitivement supprime.`,
        onConfirm: () => { items = items.filter(i => i.id != delId); refresh(); toast('Lot supprime', 'success'); },
      });
    }
  });

  toastConsume();
  renderStats();
}
