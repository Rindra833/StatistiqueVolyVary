const pageBody = renderShell('dashboard');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));
  const user = getUser();

  // cartes stats selon role (admin voit tout, sinon focus module)
  const STATS = [
    { icon: 'transaction', color: 'blue', value: '1 284', label: 'Transactions ce mois', delta: '+8.2%', up: true },
    { icon: 'collecte', color: 'green', value: '342 T', label: 'Collecte totale', delta: '+3.1%', up: true },
    { icon: 'transformation', color: 'yellow', value: '96', label: 'Lots transformes', delta: '-1.4%', up: false },
    { icon: 'distribution', color: 'red', value: '58', label: 'Livraisons en cours', delta: '+12%', up: true },
  ];
  document.getElementById('stat-grid').innerHTML = STATS.map(s => `
    <div class="stat-card">
      <div class="stat-top">
        <div class="stat-icon ${s.color}">${NAV_ICONS[s.icon]}</div>
      </div>
      <div class="stat-value">${s.value}</div>
      <div class="stat-label">${s.label}</div>
      <div class="stat-delta ${s.up ? 'up' : 'down'}">${s.up ? '▲' : '▼'} ${s.delta} vs mois dernier</div>
    </div>`).join('');

  // graphique activite (lignes)
  new Chart(document.getElementById('chart-activity'), {
    type: 'line',
    data: {
      labels: ['S1', 'S2', 'S3', 'S4', 'S5', 'S6', 'S7', 'S8'],
      datasets: [
        { label: 'Collecte', data: [40, 55, 48, 62, 58, 70, 65, 80], borderColor: '#16A34A', backgroundColor: 'rgba(22,163,74,0.08)', tension: 0.35, fill: true },
        { label: 'Distribution', data: [30, 38, 42, 40, 50, 48, 55, 60], borderColor: '#2563EB', backgroundColor: 'rgba(37,99,235,0.08)', tension: 0.35, fill: true },
      ],
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } }, scales: { y: { beginAtZero: true } } },
  });

  // graphique repartition (doughnut)
  new Chart(document.getElementById('chart-repartition'), {
    type: 'doughnut',
    data: {
      labels: ['Transaction', 'Collecte', 'Transformation', 'Distribution'],
      datasets: [{ data: [32, 28, 18, 22], backgroundColor: ['#2563EB', '#16A34A', '#D97706', '#DC2626'] }],
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } } },
  });

  // activites recentes
  const ACTIVITIES = [
    { icon: 'transaction', color: 'blue', text: 'Nouvelle transaction #TX-2481 enregistree', time: 'il y a 12 min' },
    { icon: 'collecte', color: 'green', text: 'Collecte de 4.2T validee a Antsirabe', time: 'il y a 45 min' },
    { icon: 'distribution', color: 'yellow', text: 'Livraison DIST-118 marquee en cours', time: 'il y a 1h' },
    { icon: 'transformation', color: 'blue', text: 'Lot TR-092 transforme avec succes', time: 'il y a 3h' },
  ];
  document.getElementById('activity-list').innerHTML = ACTIVITIES.map(a => `
    <div class="activity-item">
      <div class="activity-dot ${a.color}">${NAV_ICONS[a.icon]}</div>
      <div class="activity-text"><p>${a.text}</p><span>${a.time}</span></div>
    </div>`).join('');

  // notifications
  const NOTIFS = [
    { text: 'Stock de fournitures bas pour "Sac 50kg"', time: 'il y a 20 min', read: false },
    { text: 'Livreur RAKOTO indisponible aujourd\'hui', time: 'il y a 1h', read: false },
    { text: 'Rapport mensuel statistiques disponible', time: 'hier', read: true },
  ];
  document.getElementById('notif-list').innerHTML = NOTIFS.map(n => `
    <div class="notif-item ${n.read ? 'read' : ''}">
      <div class="notif-bullet"></div>
      <div><p>${n.text}</p><span>${n.time}</span></div>
    </div>`).join('');
}
