const pageBody = renderShell('fournitures');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const CATEGORIES = ['Emballage', 'Intrant agricole', 'Materiel', 'Equipement'];

  let items = [
    { id: 1, nom: 'Sac 50kg', categorie: 'Emballage', quantite: 1200, prix: 1500, date: '2026-06-20', fournisseur: 'Packmada' },
    { id: 2, nom: 'Engrais NPK', categorie: 'Intrant agricole', quantite: 340, prix: 42000, date: '2026-06-18', fournisseur: 'Agri Import' },
    { id: 3, nom: 'Balance industrielle', categorie: 'Materiel', quantite: 8, prix: 850000, date: '2026-05-30', fournisseur: 'TechnoMada' },
    { id: 4, nom: 'Sac 25kg', categorie: 'Emballage', quantite: 45, prix: 900, date: '2026-06-25', fournisseur: 'Packmada' },
    { id: 5, nom: 'Palette bois', categorie: 'Materiel', quantite: 60, prix: 25000, date: '2026-06-15', fournisseur: 'BoisPro' },
    { id: 6, nom: 'Sechoir solaire', categorie: 'Equipement', quantite: 3, prix: 1200000, date: '2026-05-10', fournisseur: 'TechnoMada' },
  ];
  let nextId = 7;

  function formHtml(row) {
    const d = row || {};
    return `
      <div class="form-grid">
        <div class="form-field full"><label>Nom</label><input name="nom" value="${d.nom || ''}" required></div>
        <div class="form-field"><label>Categorie</label><select name="categorie">${CATEGORIES.map(c => `<option ${d.categorie === c ? 'selected' : ''}>${c}</option>`).join('')}</select></div>
        <div class="form-field"><label>Fournisseur</label><input name="fournisseur" value="${d.fournisseur || ''}" required></div>
        <div class="form-field"><label>Quantite</label><input type="number" name="quantite" value="${d.quantite || ''}" required></div>
        <div class="form-field"><label>Prix unitaire (Ar)</label><input type="number" name="prix" value="${d.prix || ''}" required></div>
        <div class="form-field full"><label>Date</label><input type="date" name="date" value="${d.date || new Date().toISOString().slice(0,10)}" required></div>
      </div>`;
  }

  let table;
  function refresh() { table.setData(items); }

  table = new DataTable({
    container: document.getElementById('fr-table'),
    data: items, pageSize: 6, addLabel: 'Ajouter une fourniture',
    searchKeys: ['nom', 'fournisseur'],
    filters: [{ key: 'categorie', label: 'Categorie', options: CATEGORIES.map(c => ({ value: c, label: c })) }],
    columns: [
      { key: 'nom', label: 'Nom' },
      { key: 'categorie', label: 'Categorie', render: r => `<span class="badge badge-blue">${r.categorie}</span>` },
      { key: 'quantite', label: 'Quantite', render: r => r.quantite < 50 ? `<span style="color:var(--color-red);font-weight:600">${r.quantite}</span>` : r.quantite },
      { key: 'prix', label: 'Prix unitaire', render: r => `${r.prix.toLocaleString('fr-FR')} Ar` },
      { key: 'fournisseur', label: 'Fournisseur' },
      { key: 'date', label: 'Date' },
    ],
    actions: row => `
      <button class="action-icon" data-edit="${row.id}" aria-label="Modifier"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.85 2.83 0 114 4L7.5 20.5 2 22l1.5-5.5z"/></svg></button>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => openFormModal({
      title: 'Ajouter une fourniture', bodyHtml: formHtml(null), confirmLabel: 'Creer',
      onConfirm: data => { items.unshift({ id: nextId++, ...data, quantite: Number(data.quantite), prix: Number(data.prix) }); refresh(); toast('Fourniture ajoutee', 'success'); },
    }),
    onImportExcel: () => {
      const input = document.createElement('input');
      input.type = 'file'; input.accept = '.csv,.xlsx';
      input.addEventListener('change', () => {
        if (input.files[0]) toast(`Fichier "${input.files[0].name}" pret a etre importe (connecter au backend)`, 'info');
      });
      input.click();
    },
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'nom', label: 'Nom' }, { key: 'categorie', label: 'Categorie' }, { key: 'quantite', label: 'Quantite' },
      { key: 'prix', label: 'Prix' }, { key: 'date', label: 'Date' }, { key: 'fournisseur', label: 'Fournisseur' },
    ], 'fournitures'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'nom', label: 'Nom' }, { key: 'categorie', label: 'Categorie' }, { key: 'quantite', label: 'Quantite' },
      { key: 'prix', label: 'Prix' }, { key: 'date', label: 'Date' }, { key: 'fournisseur', label: 'Fournisseur' },
    ], 'Liste des fournitures'),
  });

  document.getElementById('fr-table').addEventListener('click', e => {
    const editId = e.target.closest('[data-edit]')?.dataset.edit;
    const delId = e.target.closest('[data-delete]')?.dataset.delete;
    if (editId) {
      const row = items.find(i => i.id == editId);
      openFormModal({
        title: `Modifier ${row.nom}`, bodyHtml: formHtml(row), confirmLabel: 'Enregistrer',
        onConfirm: data => { Object.assign(row, data, { quantite: Number(data.quantite), prix: Number(data.prix) }); refresh(); toast('Fourniture mise a jour', 'success'); },
      });
    }
    if (delId) {
      const row = items.find(i => i.id == delId);
      openConfirmModal({
        title: 'Supprimer cette fourniture ?', message: `${row.nom} sera definitivement supprimee.`,
        onConfirm: () => { items = items.filter(i => i.id != delId); refresh(); toast('Fourniture supprimee', 'success'); },
      });
    }
  });
}
