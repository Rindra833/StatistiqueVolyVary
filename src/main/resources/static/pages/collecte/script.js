const pageBody = renderShell('collecte');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const SEED = [
    { id: 1, reference: 'LP001', refClient: 'CL001', nom: 'Rakotonirina', prenom: 'Jean', telephone: '032 00 000 00', quantite: 1000, humidite: 9, prixUnitaire: 1500, montant: 1500000, date: '2026-06-14', statut: 'Validee' },
    { id: 2, reference: 'LP002', refClient: 'CL006', nom: 'Rasoa', prenom: 'Marie', telephone: '033 98 765 43', quantite: 400, humidite: 12, prixUnitaire: 1000, montant: 400000, date: '2026-06-15', statut: 'Validee' },
    { id: 3, reference: 'LP003', refClient: 'CL002', nom: 'Andry', prenom: 'Paul', telephone: '032 44 556 78', quantite: 200, humidite: 17, prixUnitaire: 2000, montant: 400000, date: '2026-08-30', statut: 'Validee' },
  ];

  let items = storeLoad('vv_collectes', SEED);

  function renderStats() {
    const qteTotal = items.reduce((s, i) => s + i.quantite, 0);
    const recette = items.reduce((s, i) => s + i.montant, 0);
    const cards = [
      { icon: 'collecte', color: 'blue', value: items.length, label: 'Lots enregistres' },
      { icon: 'collecte', color: 'green', value: `${qteTotal.toLocaleString('fr-FR')} kg`, label: 'Quantite de lot de paddy collecte' },
      { icon: 'collecte', color: 'yellow', value: formatAr(recette), label: 'Recette totale obtenue lors de la collecte' },
      { icon: 'collecte', color: 'red', value: new Set(items.map(i => i.refClient)).size, label: 'Producteurs actifs' },
    ];
    document.getElementById('col-stats').innerHTML = cards.map(c => `
      <div class="stat-card"><div class="stat-top"><div class="stat-icon ${c.color}">${NAV_ICONS[c.icon]}</div></div>
      <div class="stat-value">${c.value}</div><div class="stat-label">${c.label}</div></div>`).join('');
  }

  let table;
  function refresh() { storeSave('vv_collectes', items); renderStats(); table.setData(items); }

  table = new DataTable({
    container: document.getElementById('col-table'),
    data: items, pageSize: 6, addLabel: 'Nouvelle collecte',
    searchKeys: ['reference', 'refClient', 'nom'],
    filters: [],
    columns: [
      { key: 'reference', label: 'Reference' },
      { key: 'refClient', label: 'Reference client' },
      { key: 'date', label: 'Date' },
      { key: 'quantite', label: 'Quantite', render: r => `${r.quantite} kg` },
      { key: 'prixUnitaire', label: 'Prix unitaire', render: r => formatAr(r.prixUnitaire) },
      { key: 'montant', label: 'Total', render: r => formatAr(r.montant) },
    ],
    actions: row => `
      <a class="action-icon" href="facture/index.html?id=${row.id}" aria-label="Voir detail"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg></a>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => { window.location.href = 'nouvelle/index.html'; },
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'refClient', label: 'Reference client' }, { key: 'nom', label: 'Nom' },
      { key: 'quantite', label: 'Quantite' }, { key: 'prixUnitaire', label: 'Prix unitaire' }, { key: 'montant', label: 'Total' }, { key: 'date', label: 'Date' },
    ], 'collectes'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'refClient', label: 'Reference client' }, { key: 'nom', label: 'Nom' },
      { key: 'quantite', label: 'Quantite' }, { key: 'prixUnitaire', label: 'Prix unitaire' }, { key: 'montant', label: 'Total' }, { key: 'date', label: 'Date' },
    ], 'Liste des lots de paddy'),
  });

  document.getElementById('col-table').addEventListener('click', e => {
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
