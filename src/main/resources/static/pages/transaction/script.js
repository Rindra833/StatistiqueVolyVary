const pageBody = renderShell('transaction');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const TYPES = ['Vente', 'Vente a credit', 'Location'];
  const STATUTS = ['Validee', 'En attente', 'Annulee'];
  const BADGE = { Validee: 'badge-green', 'En attente': 'badge-yellow', Annulee: 'badge-red' };

  const SEED = [
    { id: 1, reference: 'T001', refClient: 'CL001', nom: 'Rakotonirina', prenom: 'Jean', telephone: '032 00 000 00', type: 'Vente', quantite: 1000, montant: 1500000, date: '2026-06-14', statut: 'Validee', lignes: [{ fourniture: 'Sac 50kg', quantite: 1000, montant: 1500000 }] },
    { id: 2, reference: 'T002', refClient: 'CL006', nom: 'Rasoa', prenom: 'Marie', telephone: '033 98 765 43', type: 'Vente a credit', quantite: 400, montant: 400000, date: '2026-06-15', statut: 'Validee', lignes: [{ fourniture: 'Engrais NPK', quantite: 400, montant: 400000 }] },
    { id: 3, reference: 'T003', refClient: 'CL002', nom: 'Andry', prenom: 'Paul', telephone: '032 44 556 78', type: 'Location', quantite: 200, montant: 400000, date: '2026-08-30', statut: 'En attente', lignes: [{ fourniture: 'Balance industrielle', quantite: 200, montant: 400000 }] },
  ];

  let items = storeLoad('vv_transactions', SEED);

  function renderStats() {
    const qteTotal = items.reduce((s, i) => s + i.quantite, 0);
    const recette = items.reduce((s, i) => s + i.montant, 0);
    const cards = [
      { icon: 'transaction', color: 'blue', value: items.length, label: 'Transactions totales' },
      { icon: 'transaction', color: 'green', value: qteTotal.toLocaleString('fr-FR'), label: 'Quantite totale de fournitures vendues' },
      { icon: 'transaction', color: 'yellow', value: formatAr(recette), label: 'Recette totale obtenue' },
      { icon: 'transaction', color: 'red', value: items.filter(i => i.statut === 'En attente').length, label: 'En attente de validation' },
    ];
    document.getElementById('tx-stats').innerHTML = cards.map(c => `
      <div class="stat-card"><div class="stat-top"><div class="stat-icon ${c.color}">${NAV_ICONS[c.icon]}</div></div>
      <div class="stat-value">${c.value}</div><div class="stat-label">${c.label}</div></div>`).join('');
  }

  let table;
  function refresh() { storeSave('vv_transactions', items); renderStats(); table.setData(items); }

  table = new DataTable({
    container: document.getElementById('tx-table'),
    data: items, pageSize: 6, addLabel: 'Nouvelle transaction',
    searchKeys: ['reference', 'refClient', 'nom'],
    filters: [
      { key: 'type', label: 'Type', options: TYPES.map(t => ({ value: t, label: t })) },
      { key: 'statut', label: 'Statut', options: STATUTS.map(s => ({ value: s, label: s })) },
    ],
    columns: [
      { key: 'reference', label: 'Reference' },
      { key: 'refClient', label: 'Reference client' },
      { key: 'date', label: 'Date' },
      { key: 'quantite', label: 'Quantite vendue' },
      { key: 'montant', label: 'Total', render: r => formatAr(r.montant) },
      { key: 'statut', label: 'Statut', render: r => `<span class="badge ${BADGE[r.statut]}">${r.statut}</span>` },
    ],
    actions: row => `
      <a class="action-icon" href="facture/index.html?id=${row.id}" aria-label="Voir detail"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg></a>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => { window.location.href = 'nouvelle/index.html'; },
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'refClient', label: 'Reference client' }, { key: 'nom', label: 'Nom' },
      { key: 'quantite', label: 'Quantite' }, { key: 'montant', label: 'Montant' }, { key: 'date', label: 'Date' }, { key: 'statut', label: 'Statut' },
    ], 'transactions'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'refClient', label: 'Reference client' }, { key: 'nom', label: 'Nom' },
      { key: 'quantite', label: 'Quantite' }, { key: 'montant', label: 'Montant' }, { key: 'date', label: 'Date' }, { key: 'statut', label: 'Statut' },
    ], 'Historique des transactions'),
  });

  document.getElementById('tx-table').addEventListener('click', e => {
    const delId = e.target.closest('[data-delete]')?.dataset.delete;
    if (delId) {
      const row = items.find(i => i.id == delId);
      openConfirmModal({
        title: 'Supprimer cette transaction ?', message: `${row.reference} sera definitivement supprimee.`,
        onConfirm: () => { items = items.filter(i => i.id != delId); refresh(); toast('Transaction supprimee', 'success'); },
      });
    }
  });

  toastConsume();
  renderStats();
}
