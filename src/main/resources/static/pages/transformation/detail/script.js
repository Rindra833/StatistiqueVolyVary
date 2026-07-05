const pageBody = renderShell('transformation');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const wrap = document.getElementById('recap-wrap');
  const viewId = new URLSearchParams(location.search).get('id');
  const saved = viewId ? storeLoad('vv_transformations', []).find(t => String(t.id) === viewId) : null;

  if (!saved) {
    wrap.innerHTML = `<div class="card-body flow-empty">Transformation introuvable.<br><a class="detail-link" href="../index.html">Retour a la liste</a></div>`;
  } else {
    wrap.innerHTML = `
      <div class="card-head"><h3>Transformation : detail lot de paddy transforme</h3></div>
      <div class="card-body">
        <div class="recap-row"><span class="recap-label">Date</span><span class="recap-value">${formatDateHeure(saved.dateHeure)}</span></div>
        <div class="recap-row"><span class="recap-label">Reference</span><span class="recap-value">${saved.reference}</span></div>
        <div class="recap-row"><span class="recap-label">Quantite de Paddy</span><span class="recap-value">${saved.quantite} kg</span></div>

        <p style="font-size:var(--fs-sm);font-weight:600;color:var(--color-gray-700);margin-top:12px">Obtenues</p>
        <table class="recap-table">
          <tbody>
            ${saved.obtenues.map(o => `<tr><td>${o.produit}</td><td>${o.quantite} kg</td></tr>`).join('')}
          </tbody>
        </table>

        <div class="recap-row" style="margin-top:12px"><span class="recap-label">Prix/kg pour la transformation</span><span class="recap-value">${formatAr(saved.prixUnitaire)}</span></div>
        <div class="recap-row"><span class="recap-label">Montant total</span><span class="recap-value">${formatAr(saved.montant)}</span></div>

        <p style="font-size:var(--fs-sm);font-weight:600;color:var(--color-gray-700);margin-top:12px">Lots de Paddy</p>
        <table class="recap-table">
          <thead><tr><th>Reference</th><th>Quantite</th></tr></thead>
          <tbody>
            ${saved.lots.map(l => `<tr><td>${l.reference}</td><td>${l.quantite} kg</td></tr>`).join('')}
          </tbody>
        </table>

        <div class="flow-actions">
          <a href="../index.html" class="btn btn-outline">Retour a la liste</a>
          <button class="btn btn-primary" onclick="window.print()">Imprimer</button>
        </div>
      </div>`;
  }
}
