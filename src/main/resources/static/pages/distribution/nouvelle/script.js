const pageBody = renderShell('distribution');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const PRODUITS = [
    { nom: 'Vary', prix: 3000 },
    { nom: 'Tofom-bary', prix: 2500 },
    { nom: 'Akofo-bary', prix: 2000 },
    { nom: 'Vary madinika', prix: 1500 },
  ];
  const LIEUX = ['Andoharanofotsy', 'Analakely', 'Ivato', 'Itaosy', 'Tanjombato'];
  const LIVREURS = [
    { nom: 'Rakoto Jean', matricule: '7714 TAV' },
    { nom: 'Rasoa Marie', matricule: '8821 TBA' },
    { nom: 'Andry Paul', matricule: '6390 TBM' },
  ];

  document.getElementById('in-date').value = new Date().toISOString().slice(0, 16);
  document.getElementById('in-lieu').innerHTML = LIEUX.map(l => `<option>${l}</option>`).join('');
  document.getElementById('in-livreur').innerHTML = LIVREURS.map(l => `<option value="${l.matricule}">${l.nom} (${l.matricule})</option>`).join('');

  const linesContainer = document.getElementById('line-items');

  function addLine() {
    const row = document.createElement('div');
    row.className = 'line-item';
    row.innerHTML = `
      <div class="form-field"><label>Produit</label>
        <select class="line-produit">${PRODUITS.map(p => `<option value="${p.nom}" data-prix="${p.prix}">${p.nom}</option>`).join('')}</select>
      </div>
      <div class="form-field"><label>Quantite</label><input type="number" min="1" value="1" class="line-qte" required></div>
      <div class="form-field"><label>Montant</label><input type="number" class="line-montant" required></div>
      <button type="button" class="line-remove" aria-label="Retirer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/></svg></button>`;
    linesContainer.appendChild(row);

    const select = row.querySelector('.line-produit');
    const qte = row.querySelector('.line-qte');
    const montant = row.querySelector('.line-montant');
    function recalc() { montant.value = Number(select.selectedOptions[0].dataset.prix) * Number(qte.value || 0); }
    select.addEventListener('change', recalc);
    qte.addEventListener('input', recalc);
    recalc();

    row.querySelector('.line-remove').addEventListener('click', () => {
      if (linesContainer.children.length > 1) row.remove();
      else toast('Au moins un produit est requis', 'warning');
    });
  }
  addLine();
  document.getElementById('btn-add-line').addEventListener('click', addLine);

  document.getElementById('form-distribution').addEventListener('submit', e => {
    e.preventDefault();
    const form = e.target;
    const produits = [...linesContainer.querySelectorAll('.line-item')].map(row => ({
      produit: row.querySelector('.line-produit').value,
      quantite: Number(row.querySelector('.line-qte').value),
      montant: Number(row.querySelector('.line-montant').value),
    }));
    const livreurOpt = document.getElementById('in-livreur').selectedOptions[0];
    const draft = {
      dateHeure: form.dateHeure.value,
      refClient: form.refClient.value,
      nom: form.nom.value,
      prenom: form.prenom.value,
      telephone: form.telephone.value,
      lieu: form.lieu.value,
      livreur: livreurOpt.textContent,
      argentPaye: Number(form.argentPaye.value),
      produits,
      total: produits.reduce((s, p) => s + p.montant, 0),
    };
    draftSave('vv_draft_distribution', draft);
    window.location.href = '../facture/index.html';
  });
}
