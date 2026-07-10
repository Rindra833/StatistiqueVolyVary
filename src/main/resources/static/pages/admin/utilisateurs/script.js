if (initStaticShell('utilisateurs')) {
  const API_URL = '/api/utilisateurs';
  const formModal = document.getElementById('utilisateur-form-modal');
  const deleteModal = document.getElementById('utilisateur-delete-modal');
  const form = document.getElementById('utilisateur-form');
  const passwordInput = document.getElementById('utilisateur-mdp');
  let items = [];
  let editedId = null;
  let deletedId = null;

  const table = new StaticDataTable({
    rootId: 'utilisateurs-table',
    pageSize: 6,
    actionsTemplateId: 'utilisateur-actions-template',
    columns: [
      { key: 'nom' },
      { key: 'role', render: (cell, row) => {
        const badge = document.createElement('span');
        badge.className = 'badge badge-blue';
        badge.textContent = row.role || '';
        cell.appendChild(badge);
      } },
      { key: 'employeeId' },
    ],
  });

  function openForm(utilisateur = null) {
    editedId = utilisateur?.id || null;
    document.getElementById('utilisateur-form-title').textContent = utilisateur
      ? `Modifier ${utilisateur.nom}`
      : 'Créer un compte utilisateur';
    document.getElementById('utilisateur-mdp-label').textContent = utilisateur
      ? 'Nouveau mot de passe (facultatif)'
      : 'Mot de passe temporaire';
    passwordInput.required = !utilisateur;
    form.reset();
    form.elements.nom.value = utilisateur?.nom || '';
    form.elements.role.value = utilisateur?.role || 'Administrateur';
    form.elements.employeeId.value = utilisateur?.employeeId || '';
    openStaticModal('utilisateur-form-modal');
  }

  document.getElementById('add-utilisateur').addEventListener('click', () => openForm());

  form.addEventListener('submit', async event => {
    event.preventDefault();
    const saveButton = document.getElementById('save-utilisateur');
    saveButton.disabled = true;
    try {
      const data = Object.fromEntries(new FormData(form).entries());
      const payload = {
        nom: data.nom,
        role: data.role,
        employeeId: data.employeeId ? Number(data.employeeId) : null,
        mdp: data.mdp || null,
      };
      const saved = await apiRequest(editedId ? `${API_URL}/${editedId}` : API_URL, {
        method: editedId ? 'PUT' : 'POST',
        body: payload,
      });
      items = editedId
        ? items.map(item => item.id === editedId ? saved : item)
        : [saved, ...items];
      table.setData(items);
      closeStaticModal(formModal);
      toast(editedId ? 'Compte mis à jour' : 'Compte créé', 'success');
    } catch (error) {
      handleProtectedApiError(error);
    } finally {
      saveButton.disabled = false;
    }
  });

  document.querySelector('#utilisateurs-table tbody').addEventListener('click', event => {
    const editId = Number(event.target.closest('[data-edit]')?.dataset.edit);
    const deleteId = Number(event.target.closest('[data-delete]')?.dataset.delete);
    if (editId) openForm(items.find(item => item.id === editId));
    if (deleteId) {
      deletedId = deleteId;
      const utilisateur = items.find(item => item.id === deleteId);
      document.getElementById('utilisateur-delete-message').textContent = `${utilisateur.nom} perdra l'accès à la plateforme.`;
      openStaticModal('utilisateur-delete-modal');
    }
  });

  document.getElementById('confirm-delete-utilisateur').addEventListener('click', async event => {
    event.currentTarget.disabled = true;
    try {
      await apiRequest(`${API_URL}/${deletedId}`, { method: 'DELETE' });
      items = items.filter(item => item.id !== deletedId);
      table.setData(items);
      closeStaticModal(deleteModal);
      toast('Compte supprimé', 'success');
    } catch (error) {
      handleProtectedApiError(error);
    } finally {
      event.currentTarget.disabled = false;
    }
  });

  document.getElementById('export-utilisateurs-excel').addEventListener('click', () => {
    exportToExcel(table.filteredSorted(), [
      { key: 'nom', label: "Nom d'utilisateur" },
      { key: 'role', label: 'Rôle' },
      { key: 'employeeId', label: 'ID employee' },
    ], 'utilisateurs');
  });
  document.getElementById('export-utilisateurs-pdf').addEventListener('click', () => {
    exportToPdf(table.filteredSorted(), [
      { key: 'nom', label: "Nom d'utilisateur" },
      { key: 'role', label: 'Rôle' },
      { key: 'employeeId', label: 'ID employee' },
    ], 'Liste des utilisateurs');
  });

  apiRequest(API_URL)
    .then(data => {
      items = data;
      table.setData(items);
    })
    .catch(handleProtectedApiError);
}
