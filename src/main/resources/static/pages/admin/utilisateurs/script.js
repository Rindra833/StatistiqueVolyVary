const pageBody = renderShell('utilisateurs');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const ROLE_LIST = Object.values(ROLES);
  const ETATS = ['Actif', 'Inactif'];

  let items = [
    { id: 1, nom: 'Edinah R.', email: 'edinah@volyvary.mg', role: ROLES.ADMIN, etat: 'Actif', date: '2026-01-12' },
    { id: 2, nom: 'Johny F.', email: 'johny@volyvary.mg', role: ROLES.TRANSACTION, etat: 'Actif', date: '2026-02-03' },
    { id: 3, nom: 'Olivier M.', email: 'olivier@volyvary.mg', role: ROLES.COLLECTE, etat: 'Actif', date: '2026-02-10' },
    { id: 4, nom: 'Faneva R.', email: 'faneva@volyvary.mg', role: ROLES.TRANSFORMATION, etat: 'Inactif', date: '2026-03-01' },
    { id: 5, nom: 'Mpiaro T.', email: 'mpiaro@volyvary.mg', role: ROLES.DISTRIBUTION, etat: 'Actif', date: '2026-03-15' },
    { id: 6, nom: 'Jeannie H.', email: 'jeannie@volyvary.mg', role: ROLES.STATISTIQUES, etat: 'Actif', date: '2026-04-02' },
  ];
  let nextId = 7;

  function formHtml(row) {
    const d = row || {};
    return `
      <div class="form-grid">
        <div class="form-field full"><label>Nom complet</label><input name="nom" value="${d.nom || ''}" required></div>
        <div class="form-field full"><label>Email</label><input type="email" name="email" value="${d.email || ''}" required></div>
        <div class="form-field"><label>Role</label><select name="role">${ROLE_LIST.map(r => `<option ${d.role === r ? 'selected' : ''}>${r}</option>`).join('')}</select></div>
        <div class="form-field"><label>Etat</label><select name="etat">${ETATS.map(e => `<option ${d.etat === e ? 'selected' : ''}>${e}</option>`).join('')}</select></div>
        ${!row ? `<div class="form-field full"><label>Mot de passe temporaire</label><input type="password" name="password" required></div>` : ''}
      </div>`;
  }

  let table;
  function refresh() { table.setData(items); }

  table = new DataTable({
    container: document.getElementById('us-table'),
    data: items, pageSize: 6, addLabel: 'Creer un compte',
    searchKeys: ['nom', 'email'],
    filters: [
      { key: 'role', label: 'Role', options: ROLE_LIST.map(r => ({ value: r, label: r })) },
      { key: 'etat', label: 'Etat', options: ETATS.map(e => ({ value: e, label: e })) },
    ],
    columns: [
      { key: 'nom', label: 'Nom' },
      { key: 'email', label: 'Email' },
      { key: 'role', label: 'Role', render: r => `<span class="badge badge-blue">${r.role}</span>` },
      { key: 'date', label: 'Cree le' },
      { key: 'etat', label: 'Etat', render: r => `<span class="badge ${r.etat === 'Actif' ? 'badge-green' : 'badge-gray'}">${r.etat}</span>` },
    ],
    actions: row => `
      <button class="action-icon" data-edit="${row.id}" aria-label="Modifier"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 3a2.85 2.83 0 114 4L7.5 20.5 2 22l1.5-5.5z"/></svg></button>
      <button class="action-icon danger" data-delete="${row.id}" aria-label="Supprimer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18M8 6V4a2 2 0 012-2h4a2 2 0 012 2v2m3 0v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>`,
    onAdd: () => openFormModal({
      title: 'Creer un compte utilisateur', bodyHtml: formHtml(null), confirmLabel: 'Creer',
      onConfirm: data => { delete data.password; items.unshift({ id: nextId++, ...data, date: new Date().toISOString().slice(0,10) }); refresh(); toast('Compte cree', 'success'); },
    }),
    onExportExcel: rows => exportToExcel(rows, [
      { key: 'nom', label: 'Nom' }, { key: 'email', label: 'Email' }, { key: 'role', label: 'Role' },
      { key: 'date', label: 'Cree le' }, { key: 'etat', label: 'Etat' },
    ], 'utilisateurs'),
    onExportPdf: rows => exportToPdf(rows, [
      { key: 'nom', label: 'Nom' }, { key: 'email', label: 'Email' }, { key: 'role', label: 'Role' },
      { key: 'date', label: 'Cree le' }, { key: 'etat', label: 'Etat' },
    ], 'Liste des utilisateurs'),
  });

  document.getElementById('us-table').addEventListener('click', e => {
    const editId = e.target.closest('[data-edit]')?.dataset.edit;
    const delId = e.target.closest('[data-delete]')?.dataset.delete;
    if (editId) {
      const row = items.find(i => i.id == editId);
      openFormModal({
        title: `Modifier ${row.nom}`, bodyHtml: formHtml(row), confirmLabel: 'Enregistrer',
        onConfirm: data => { Object.assign(row, data); refresh(); toast('Compte mis a jour', 'success'); },
      });
    }
    if (delId) {
      const row = items.find(i => i.id == delId);
      openConfirmModal({
        title: 'Supprimer ce compte ?', message: `${row.nom} perdra l'acces a la plateforme.`,
        onConfirm: () => { items = items.filter(i => i.id != delId); refresh(); toast('Compte supprime', 'success'); },
      });
    }
  });
}
