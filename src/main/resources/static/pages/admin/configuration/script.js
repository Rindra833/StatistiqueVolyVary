const pageBody = renderShell('configuration');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const TOGGLES = [
    { key: 'notif-email', label: 'Notifications par email', desc: 'Recevoir un recapitulatif quotidien', checked: true },
    { key: 'notif-stock', label: 'Alertes de stock bas', desc: 'Etre alerte quand une fourniture est en rupture', checked: true },
    { key: 'notif-livraison', label: 'Alertes de livraison', desc: 'Etre notifie a chaque changement de statut', checked: false },
  ];
  document.getElementById('config-toggles').innerHTML = TOGGLES.map(t => `
    <div class="toggle-row">
      <div><p>${t.label}</p><span>${t.desc}</span></div>
      <label class="switch">
        <input type="checkbox" ${t.checked ? 'checked' : ''} data-toggle="${t.key}">
        <span class="switch-track"></span>
      </label>
    </div>`).join('');

  document.getElementById('config-toggles').addEventListener('change', () => toast('Preference enregistree', 'success', 1500));

  document.getElementById('form-entreprise').addEventListener('submit', e => { e.preventDefault(); toast('Informations enregistrees', 'success'); });
  document.getElementById('form-secu').addEventListener('submit', e => { e.preventDefault(); toast('Parametres de securite enregistres', 'success'); });
}
