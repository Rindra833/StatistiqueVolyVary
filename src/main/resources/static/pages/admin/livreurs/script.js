if (initStaticShell('livreurs')) {
  const API_URL = '/api/livreurs';
  const formModal = document.getElementById('livreur-form-modal');
  const deleteModal = document.getElementById('livreur-delete-modal');
  const form = document.getElementById('livreur-form');
  let items = [];
  let editedId = null;
  let deletedId = null;

  function renderBadge(cell, value, className) {
    const badge = document.createElement('span');
    badge.className = `badge ${className}`;
    badge.textContent = value || '';
    cell.appendChild(badge);
  }

  const table = new StaticDataTable({
    rootId: 'livreurs-table',
    pageSize: 6,
    actionsTemplateId: 'livreur-actions-template',
    columns: [
      { key: 'nom' },
      { key: 'telephone' },
      { key: 'adresse' },
      { key: 'vehicule', render: (cell, row) => renderBadge(cell, row.vehicule, 'badge-blue') },
      { key: 'immatriculation' },
      { key: 'disponibilite', render: (cell, row) => renderBadge(cell, row.disponibilite, row.disponibilite === 'Disponible' ? 'badge-green' : 'badge-red') },
    ],
  });

  function openForm(livreur = null) {
    editedId = livreur?.id || null;
    document.getElementById('livreur-form-title').textContent = livreur
      ? `Modifier ${livreur.nom}`
      : 'Ajouter un livreur';
    form.reset();
    form.elements.nom.value = livreur?.nom || '';
    form.elements.telephone.value = livreur?.telephone || '';
    form.elements.vehicule.value = livreur?.vehicule || 'Moto';
    form.elements.immatriculation.value = livreur?.immatriculation || '';
    form.elements.disponibilite.value = livreur?.disponibilite || 'Disponible';
    form.elements.adresse.value = livreur?.adresse || '';
    openStaticModal('livreur-form-modal');
  }

  document.getElementById('add-livreur').addEventListener('click', () => openForm());

  form.addEventListener('submit', async event => {
    event.preventDefault();
    const saveButton = document.getElementById('save-livreur');
    saveButton.disabled = true;
    try {
      const payload = Object.fromEntries(new FormData(form).entries());
      const saved = await apiRequest(editedId ? `${API_URL}/${editedId}` : API_URL, {
        method: editedId ? 'PUT' : 'POST',
        body: payload,
      });
      items = editedId
        ? items.map(item => item.id === editedId ? saved : item)
        : [saved, ...items];
      table.setData(items);
      closeStaticModal(formModal);
      toast(editedId ? 'Livreur mis à jour' : 'Livreur ajouté', 'success');
    } catch (error) {
      handleProtectedApiError(error);
    } finally {
      saveButton.disabled = false;
    }
  });

  document.querySelector('#livreurs-table tbody').addEventListener('click', event => {
    const editId = Number(event.target.closest('[data-edit]')?.dataset.edit);
    const deleteId = Number(event.target.closest('[data-delete]')?.dataset.delete);
    if (editId) openForm(items.find(item => item.id === editId));
    if (deleteId) {
      deletedId = deleteId;
      const livreur = items.find(item => item.id === deleteId);
      document.getElementById('livreur-delete-message').textContent = `${livreur.nom} sera définitivement supprimé.`;
      openStaticModal('livreur-delete-modal');
    }
  });

  document.getElementById('confirm-delete-livreur').addEventListener('click', async event => {
    event.currentTarget.disabled = true;
    try {
      await apiRequest(`${API_URL}/${deletedId}`, { method: 'DELETE' });
      items = items.filter(item => item.id !== deletedId);
      table.setData(items);
      closeStaticModal(deleteModal);
      toast('Livreur supprimé', 'success');
    } catch (error) {
      handleProtectedApiError(error);
    } finally {
      event.currentTarget.disabled = false;
    }
  });

  document.getElementById('export-livreurs-excel').addEventListener('click', () => {
    exportToExcel(table.filteredSorted(), [
      { key: 'nom', label: 'Nom' }, { key: 'telephone', label: 'Téléphone' },
      { key: 'adresse', label: 'Adresse' }, { key: 'vehicule', label: 'Véhicule' },
      { key: 'immatriculation', label: 'Immatriculation' }, { key: 'disponibilite', label: 'Disponibilité' },
    ], 'livreurs');
  });
  document.getElementById('export-livreurs-pdf').addEventListener('click', () => {
    exportToPdf(table.filteredSorted(), [
      { key: 'nom', label: 'Nom' }, { key: 'telephone', label: 'Téléphone' },
      { key: 'adresse', label: 'Adresse' }, { key: 'vehicule', label: 'Véhicule' },
      { key: 'immatriculation', label: 'Immatriculation' }, { key: 'disponibilite', label: 'Disponibilité' },
    ], 'Liste des livreurs');
  });

  apiRequest(API_URL)
    .then(data => {
      items = data;
      table.setData(items);
    })
    .catch(handleProtectedApiError);
}
