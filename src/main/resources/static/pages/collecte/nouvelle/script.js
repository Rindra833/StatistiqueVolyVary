const pageBody = renderShell('collecte');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));
  document.getElementById('in-date').value = new Date().toISOString().slice(0, 16);

  document.getElementById('form-collecte').addEventListener('submit', e => {
    e.preventDefault();
    const form = e.target;
    const draft = {
      dateHeure: form.dateHeure.value,
      refClient: form.refClient.value,
      nom: form.nom.value,
      prenom: form.prenom.value,
      telephone: form.telephone.value,
      quantite: Number(form.quantite.value),
      humidite: Number(form.humidite.value),
    };
    draftSave('vv_draft_collecte', draft);
    window.location.href = '../facture/index.html';
  });
}
