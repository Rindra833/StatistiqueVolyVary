const pageBody = renderShell('transaction');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  // catalogue local des fournitures (a terme: GET /api/fournitures)
  const CATEGORIES = ['Materielle', 'Emballage', 'Intrant agricole', 'Equipement'];
  const FOURNITURES = [
    { nom: 'Sac 50kg', categorie: 'Emballage', prix: 1500 },
    { nom: 'Sac 25kg', categorie: 'Emballage', prix: 900 },
    { nom: 'Engrais NPK', categorie: 'Intrant agricole', prix: 42000 },
    { nom: 'Balance industrielle', categorie: 'Materielle', prix: 850000 },
    { nom: 'Palette bois', categorie: 'Materielle', prix: 25000 },
    { nom: 'Sechoir solaire', categorie: 'Equipement', prix: 1200000 },
  ];

  document.getElementById('in-date').value = new Date().toISOString().slice(0, 16);
  document.getElementById('in-categorie').innerHTML = CATEGORIES.map(c => `<option>${c}</option>`).join('');

  // les 3 cases se comportent comme un choix unique
  const typeGroup = document.getElementById('type-group');
  typeGroup.addEventListener('change', e => {
    typeGroup.querySelectorAll('input[type=checkbox]').forEach(cb => { if (cb !== e.target) cb.checked = false; });
    if (![...typeGroup.querySelectorAll('input')].some(cb => cb.checked)) e.target.checked = true;
  });

  const linesContainer = document.getElementById('line-items');
  let lineCount = 0;

  function addLine() {
    lineCount++;
    const row = document.createElement('div');
    row.className = 'line-item';
    row.innerHTML = `
      <div class="form-field"><label>Fourniture</label>
        <select name="fourniture" class="line-fourniture">${FOURNITURES.map(f => `<option value="${f.nom}" data-prix="${f.prix}">${f.nom}</option>`).join('')}</select>
      </div>
      <div class="form-field"><label>Quantite</label><input type="number" min="1" value="1" class="line-qte" required></div>
      <div class="form-field"><label>Montant</label><input type="number" class="line-montant" required></div>
      <button type="button" class="line-remove" aria-label="Retirer"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14"/></svg></button>`;
    linesContainer.appendChild(row);

    const select = row.querySelector('.line-fourniture');
    const qte = row.querySelector('.line-qte');
    const montant = row.querySelector('.line-montant');
    function recalc() {
      const prix = Number(select.selectedOptions[0].dataset.prix);
      montant.value = prix * Number(qte.value || 0);
    }
    select.addEventListener('change', recalc);
    qte.addEventListener('input', recalc);
    recalc();

    row.querySelector('.line-remove').addEventListener('click', () => {
      if (linesContainer.children.length > 1) row.remove();
      else toast('Au moins une fourniture est requise', 'warning');
    });
  }
  addLine();
  document.getElementById('btn-add-line').addEventListener('click', addLine);

  document.getElementById('form-transaction').addEventListener('submit', e => {
    e.preventDefault();
    const form = e.target;
    const type = typeGroup.querySelector('input:checked')?.value || 'Vente';
    const lignes = [...linesContainer.querySelectorAll('.line-item')].map(row => ({
      fourniture: row.querySelector('.line-fourniture').value,
      quantite: Number(row.querySelector('.line-qte').value),
      montant: Number(row.querySelector('.line-montant').value),
    }));
    const draft = {
      type,
      dateHeure: form.dateHeure.value,
      refClient: form.refClient.value,
      nom: form.nom.value,
      prenom: form.prenom.value,
      telephone: form.telephone.value,
      categorie: form.categorie.value,
      lignes,
      total: lignes.reduce((s, l) => s + l.montant, 0),
    };
    draftSave('vv_draft_transaction', draft);
    window.location.href = '../facture/index.html';
  });
}
