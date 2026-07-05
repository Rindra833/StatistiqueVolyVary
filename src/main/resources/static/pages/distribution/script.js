const pageBody = renderShell('distribution');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const STATUTS = ['En attente', 'En cours', 'Livree', 'Annulee'];
  const STATUT_BADGE = { 'En attente': 'badge-yellow', 'En cours': 'badge-blue', 'Livree': 'badge-green', 'Annulee': 'badge-red' };

  const SEED = [
    { id: 1, reference: 'FC001', refClient: 'CL001', nom: 'Rakotonirina', prenom: 'Jean', telephone: '032 00 000 00', lieu: 'Andoharanofotsy', livreur: 'Rakoto Jean (7714 TAV)', quantite: 700, montant: 500000, date: '2026-06-14', statut: 'Livree', produits: [{ produit: 'Vary', quantite: 500, montant: 1500000 }, { produit: 'Tofom-bary', quantite: 200, montant: 500000 }] },
    { id: 2, reference: 'FC002', refClient: 'CL002', nom: 'Andry', prenom: 'Paul', telephone: '032 44 556 78', lieu: 'Ivato', livreur: 'Andry Paul (6390 TBM)', quantite: 400, montant: 400000, date: '2026-06-15', statut: 'En cours', produits: [{ produit: 'Akofo-bary', quantite: 400, montant: 400000 }] },
    { id: 3, reference: 'FC003', refClient: 'CL003', nom: 'Rasoa', prenom: 'Marie', telephone: '033 98 765 43', lieu: 'Itaosy', livreur: 'Rasoa Marie (8821 TBA)', quantite: 200, montant: 400000, date: '2026-08-30', statut: 'En attente', produits: [{ produit: 'Vary madinika', quantite: 200, montant: 400000 }] },
  ];

  let items = storeLoad('vv_distributions', SEED);

  function renderStats() {
    const qteTotal = items.reduce((s, i) => s + i.quantite, 0);
    const recette = items.reduce((s, i) => s + i.montant, 0);
    const counts = STATUTS.reduce((acc, s) => ({ ...acc, [s]: items.filter(i => i.statut === s).length }), {});
    const cards = [
      { icon: 'distribution', color: 'blue', value: items.length, label: 'Total distributions' },
      { icon: 'distribution', color: 'green', value: `${qteTotal.toLocaleString('fr-FR')}`, label: 'Quantite totale de produits vendus' },
      { icon: 'distribution', color: 'yellow', value: formatAr(recette), label: 'Recette totale obtenue' },
      { icon: 'distribution', color: 'red', value: counts['En attente'] + counts['En cours'], label: 'Commandes en cours / en attente' },
    ];
    document.getElementById('dist-stats').innerHTML = cards.map(c => `
      <div class="stat-card"><div class="stat-top"><div class="stat-icon ${c.color}">${NAV_ICONS[c.icon]}</div></div>
      <div class="stat-value">${c.value}</div><div class="stat-label">${c.label}</div></div>`).join('');
  }

  function statutFormHtml(row) {
    return `
      <div class="form-grid">
        <div class="form-field full"><label>Reference</label><input value="${row.reference}" disabled></div>
        <div class="form-field full"><label>Statut</label>
          <select name="statut">${STATUTS.map(s => `<option ${row.statut === s ? 'selected' : ''}>${s}</option>`).join('')}</select>
        </div>
      </div>`;
  }

  let table;
  function refresh() { storeSave('vv_distributions', items); renderStats(); table.setData(items); }

  table = new DataTable({
    container: document.getElementById('dist-table'),
    data: items, pageSize: 6, addLabel: 'Nouvelle distribution',
    searchKeys: ['reference', 'refClient', 'nom'],
    filters: [{ key: 'statut', label: 'Statut', options: STATUTS.map(s => ({ value: s, label: s })) }],
    columns: [
      { key: 'reference', label: 'Reference' },
      { key: 'refClient', label: 'Reference client' },
      { key: 'date', label: 'Date' },
      { key: 'quantite', label: 'Qte produits vendus' },
      { key: 'montant', label: 'Total', render: r => formatAr(r.montant) },
      { key: 'statut', label: 'Statut', render: r => `<span class="badge ${STATUT_BADGE[r.statut]}">${r.statut}</span>` },
    ],
    actions: row => `
      <a class="action-icon" href="facture/index.html?id=${row.id}" aria-label="Voir detail"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg></a>
      <button class="action-icon" data-edit="${row.id}" aria-label="Modifier le statut"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.85 2.83 0 114 4L7.5 20.5 2 22l1.5-5.5z"/></svg></button>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => { window.location.href = 'nouvelle/index.html'; },
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'refClient', label: 'Reference client' }, { key: 'nom', label: 'Nom' },
      { key: 'quantite', label: 'Quantite' }, { key: 'montant', label: 'Total' }, { key: 'date', label: 'Date' }, { key: 'statut', label: 'Statut' },
    ], 'distributions'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'reference', label: 'Reference' }, { key: 'refClient', label: 'Reference client' }, { key: 'nom', label: 'Nom' },
      { key: 'quantite', label: 'Quantite' }, { key: 'montant', label: 'Total' }, { key: 'date', label: 'Date' }, { key: 'statut', label: 'Statut' },
    ], 'Liste des factures de distribution'),
  });

  document.getElementById('dist-table').addEventListener('click', e => {
    const editId = e.target.closest('[data-edit]')?.dataset.edit;
    const delId = e.target.closest('[data-delete]')?.dataset.delete;
    if (editId) {
      const row = items.find(d => d.id == editId);
      openFormModal({
        title: `Modifier le statut - ${row.reference}`, bodyHtml: statutFormHtml(row), confirmLabel: 'Enregistrer',
        onConfirm: data => { row.statut = data.statut; refresh(); toast('Statut mis a jour', 'success'); },
      });
    }
    if (delId) {
      const row = items.find(d => d.id == delId);
      openConfirmModal({
        title: 'Supprimer cette distribution ?', message: `${row.reference} sera definitivement supprimee.`,
        onConfirm: () => { items = items.filter(d => d.id != delId); refresh(); toast('Distribution supprimee', 'success'); },
      });
    }
  });

  toastConsume();
  renderStats();
}
