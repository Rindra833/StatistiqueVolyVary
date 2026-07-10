if (initStaticShell('fournitures')) {
  const API_URL = '/api/fournitures';
  const formModal = document.getElementById('fourniture-form-modal');
  const deleteModal = document.getElementById('fourniture-delete-modal');
  const form = document.getElementById('fourniture-form');
  let items = [];
  let editedId = null;
  let deletedId = null;

  function renderBadge(cell, value) {
    const badge = document.createElement('span');
    badge.className = 'badge badge-blue';
    badge.textContent = value || '';
    cell.appendChild(badge);
  }

  const table = new StaticDataTable({
    rootId: 'fournitures-table',
    pageSize: 6,
    actionsTemplateId: 'fourniture-actions-template',
    columns: [
      { key: 'nom' },
      { key: 'categorie', render: (cell, row) => renderBadge(cell, row.categorie) },
      { key: 'quantite', render: (cell, row) => {
        cell.textContent = row.quantite ?? 0;
        if (row.quantite < 50) {
          cell.style.color = 'var(--color-red)';
          cell.style.fontWeight = '600';
        }
      } },
      { key: 'prix', render: (cell, row) => {
        cell.textContent = `${Number(row.prix || 0).toLocaleString('fr-FR')} Ar`;
      } },
      { key: 'fournisseur' },
      { key: 'date' },
    ],
  });

  function openForm(fourniture = null) {
    editedId = fourniture?.id || null;
    document.getElementById('fourniture-form-title').textContent = fourniture
      ? `Modifier ${fourniture.nom}`
      : 'Ajouter une fourniture';
    form.reset();
    form.elements.nom.value = fourniture?.nom || '';
    form.elements.categorie.value = fourniture?.categorie || 'Emballage';
    form.elements.fournisseur.value = fourniture?.fournisseur || '';
    form.elements.quantite.value = fourniture?.quantite ?? '';
    form.elements.prix.value = fourniture?.prix ?? '';
    form.elements.date.value = fourniture?.date || new Date().toISOString().slice(0, 10);
    openStaticModal('fourniture-form-modal');
  }

  document.getElementById('add-fourniture').addEventListener('click', () => openForm());

  form.addEventListener('submit', async event => {
    event.preventDefault();
    const saveButton = document.getElementById('save-fourniture');
    saveButton.disabled = true;
    try {
      const data = Object.fromEntries(new FormData(form).entries());
      const payload = { ...data, quantite: Number(data.quantite), prix: Number(data.prix) };
      const saved = await apiRequest(editedId ? `${API_URL}/${editedId}` : API_URL, {
        method: editedId ? 'PUT' : 'POST',
        body: payload,
      });
      items = editedId
        ? items.map(item => item.id === editedId ? saved : item)
        : [saved, ...items];
      table.setData(items);
      closeStaticModal(formModal);
      toast(editedId ? 'Fourniture mise à jour' : 'Fourniture ajoutée', 'success');
    } catch (error) {
      handleProtectedApiError(error);
    } finally {
      saveButton.disabled = false;
    }
  });

  document.querySelector('#fournitures-table tbody').addEventListener('click', event => {
    const editId = Number(event.target.closest('[data-edit]')?.dataset.edit);
    const deleteId = Number(event.target.closest('[data-delete]')?.dataset.delete);
    if (editId) openForm(items.find(item => item.id === editId));
    if (deleteId) {
      deletedId = deleteId;
      const fourniture = items.find(item => item.id === deleteId);
      document.getElementById('fourniture-delete-message').textContent = `${fourniture.nom} sera définitivement supprimée.`;
      openStaticModal('fourniture-delete-modal');
    }
  });

  document.getElementById('confirm-delete-fourniture').addEventListener('click', async event => {
    event.currentTarget.disabled = true;
    try {
      await apiRequest(`${API_URL}/${deletedId}`, { method: 'DELETE' });
      items = items.filter(item => item.id !== deletedId);
      table.setData(items);
      closeStaticModal(deleteModal);
      toast('Fourniture supprimée', 'success');
    } catch (error) {
      handleProtectedApiError(error);
    } finally {
      event.currentTarget.disabled = false;
    }
  });

  document.getElementById('import-fournitures').addEventListener('click', () => {
    toast("L'import Excel nécessite un endpoint d'import dédié.", 'info');
  });
  document.getElementById('export-fournitures-excel').addEventListener('click', () => {
    exportToExcel(table.filteredSorted(), [
      { key: 'nom', label: 'Nom' }, { key: 'categorie', label: 'Catégorie' },
      { key: 'quantite', label: 'Quantité' }, { key: 'prix', label: 'Prix' },
      { key: 'date', label: 'Date' }, { key: 'fournisseur', label: 'Fournisseur' },
    ], 'fournitures');
  });
  document.getElementById('export-fournitures-pdf').addEventListener('click', () => {
    exportToPdf(table.filteredSorted(), [
      { key: 'nom', label: 'Nom' }, { key: 'categorie', label: 'Catégorie' },
      { key: 'quantite', label: 'Quantité' }, { key: 'prix', label: 'Prix' },
      { key: 'date', label: 'Date' }, { key: 'fournisseur', label: 'Fournisseur' },
    ], 'Liste des fournitures');
  });

  apiRequest(API_URL)
    .then(data => {
      items = data;
      table.setData(items);
    })
    .catch(handleProtectedApiError);
}
