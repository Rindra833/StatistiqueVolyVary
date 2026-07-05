const pageBody = renderShell('livreurs');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const VEHICULES = ['Moto', 'Camionnette', 'Camion'];
  const DISPO = ['Disponible', 'Indisponible'];

  let items = [
    { id: 1, nom: 'Rakoto Jean', telephone: '034 12 345 67', adresse: 'Analakely, Antananarivo', vehicule: 'Camionnette', disponibilite: 'Disponible' },
    { id: 2, nom: 'Rasoa Marie', telephone: '033 98 765 43', adresse: 'Ankorondrano, Antananarivo', vehicule: 'Moto', disponibilite: 'Disponible' },
    { id: 3, nom: 'Andry Paul', telephone: '032 44 556 78', adresse: 'Ivato, Antananarivo', vehicule: 'Camion', disponibilite: 'Indisponible' },
    { id: 4, nom: 'Voahangy Lala', telephone: '034 55 667 89', adresse: 'Itaosy, Antananarivo', vehicule: 'Moto', disponibilite: 'Disponible' },
    { id: 5, nom: 'Hery Tiana', telephone: '033 11 223 45', adresse: 'Tanjombato, Antananarivo', vehicule: 'Camionnette', disponibilite: 'Indisponible' },
  ];
  let nextId = 6;

  function formHtml(row) {
    const d = row || {};
    return `
      <div class="form-grid">
        <div class="form-field full"><label>Nom complet</label><input name="nom" value="${d.nom || ''}" required></div>
        <div class="form-field"><label>Telephone</label><input name="telephone" value="${d.telephone || ''}" required></div>
        <div class="form-field"><label>Vehicule</label><select name="vehicule">${VEHICULES.map(v => `<option ${d.vehicule === v ? 'selected' : ''}>${v}</option>`).join('')}</select></div>
        <div class="form-field full"><label>Adresse</label><input name="adresse" value="${d.adresse || ''}" required></div>
        <div class="form-field"><label>Disponibilite</label><select name="disponibilite">${DISPO.map(v => `<option ${d.disponibilite === v ? 'selected' : ''}>${v}</option>`).join('')}</select></div>
      </div>`;
  }

  let table;
  function refresh() { table.setData(items); }

  table = new DataTable({
    container: document.getElementById('lv-table'),
    data: items, pageSize: 6, addLabel: 'Ajouter un livreur',
    searchKeys: ['nom', 'telephone', 'adresse'],
    filters: [
      { key: 'vehicule', label: 'Vehicule', options: VEHICULES.map(v => ({ value: v, label: v })) },
      { key: 'disponibilite', label: 'Disponibilite', options: DISPO.map(v => ({ value: v, label: v })) },
    ],
    columns: [
      { key: 'nom', label: 'Nom' },
      { key: 'telephone', label: 'Telephone' },
      { key: 'adresse', label: 'Adresse' },
      { key: 'vehicule', label: 'Vehicule', render: r => `<span class="badge badge-blue">${r.vehicule}</span>` },
      { key: 'disponibilite', label: 'Statut', render: r => `<span class="badge ${r.disponibilite === 'Disponible' ? 'badge-green' : 'badge-red'}">${r.disponibilite}</span>` },
    ],
    actions: row => `
      <button class="action-icon" data-edit="${row.id}" aria-label="Modifier"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.85 2.83 0 114 4L7.5 20.5 2 22l1.5-5.5z"/></svg></button>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => openFormModal({
      title: 'Ajouter un livreur', bodyHtml: formHtml(null), confirmLabel: 'Creer',
      onConfirm: data => { items.unshift({ id: nextId++, ...data }); refresh(); toast('Livreur ajoute', 'success'); },
    }),
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'nom', label: 'Nom' }, { key: 'telephone', label: 'Telephone' }, { key: 'adresse', label: 'Adresse' },
      { key: 'vehicule', label: 'Vehicule' }, { key: 'disponibilite', label: 'Disponibilite' },
    ], 'livreurs'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'nom', label: 'Nom' }, { key: 'telephone', label: 'Telephone' }, { key: 'adresse', label: 'Adresse' },
      { key: 'vehicule', label: 'Vehicule' }, { key: 'disponibilite', label: 'Disponibilite' },
    ], 'Liste des livreurs'),
  });

  document.getElementById('lv-table').addEventListener('click', e => {
    const editId = e.target.closest('[data-edit]')?.dataset.edit;
    const delId = e.target.closest('[data-delete]')?.dataset.delete;
    if (editId) {
      const row = items.find(i => i.id == editId);
      openFormModal({
        title: `Modifier ${row.nom}`, bodyHtml: formHtml(row), confirmLabel: 'Enregistrer',
        onConfirm: data => { Object.assign(row, data); refresh(); toast('Livreur mis a jour', 'success'); },
      });
    }
    if (delId) {
      const row = items.find(i => i.id == delId);
      openConfirmModal({
        title: 'Supprimer ce livreur ?', message: `${row.nom} sera definitivement supprime.`,
        onConfirm: () => { items = items.filter(i => i.id != delId); refresh(); toast('Livreur supprime', 'success'); },
      });
    }
  });
}
