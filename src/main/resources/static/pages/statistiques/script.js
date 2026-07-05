const pageBody = renderShell('statistiques');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));

  const cards = [
    { icon: 'transaction', color: 'blue', value: '1 284', label: 'Transactions' },
    { icon: 'collecte', color: 'green', value: '2 385 kg', label: 'Collecte totale' },
    { icon: 'transformation', color: 'yellow', value: '96', label: 'Transformations' },
    { icon: 'distribution', color: 'red', value: '58', label: 'Distributions' },
  ];
  document.getElementById('stat-cards').innerHTML = cards.map(c => `
    <div class="stat-card"><div class="stat-top"><div class="stat-icon ${c.color}">${NAV_ICONS[c.icon]}</div></div>
    <div class="stat-value">${c.value}</div><div class="stat-label">${c.label}</div></div>`).join('');

  new Chart(document.getElementById('chart-bar'), {
    type: 'bar',
    data: {
      labels: ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Jun'],
      datasets: [
        { label: 'Transactions', data: [180, 220, 195, 260, 240, 284], backgroundColor: '#2563EB', borderRadius: 6 },
        { label: 'Collectes', data: [150, 170, 210, 190, 230, 250], backgroundColor: '#16A34A', borderRadius: 6 },
      ],
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } }, scales: { y: { beginAtZero: true } } },
  });

  new Chart(document.getElementById('chart-pie'), {
    type: 'pie',
    data: {
      labels: ['Transaction', 'Collecte', 'Transformation', 'Distribution'],
      datasets: [{ data: [35, 27, 16, 22], backgroundColor: ['#2563EB', '#16A34A', '#D97706', '#DC2626'] }],
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } } },
  });

  new Chart(document.getElementById('chart-line'), {
    type: 'line',
    data: {
      labels: ['Jul', 'Aou', 'Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Jun'],
      datasets: [{ label: 'Chiffre d\'affaires (M Ar)', data: [12, 14, 13, 16, 18, 17, 19, 21, 20, 23, 25, 27], borderColor: '#2563EB', backgroundColor: 'rgba(37,99,235,0.08)', tension: 0.35, fill: true }],
    },
    options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } },
  });

  new Chart(document.getElementById('chart-doughnut'), {
    type: 'doughnut',
    data: {
      labels: ['Validee / Livree', 'En attente / En cours', 'Annulee / Rejetee'],
      datasets: [{ data: [64, 26, 10], backgroundColor: ['#16A34A', '#D97706', '#DC2626'] }],
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } } },
  });

  document.getElementById('stat-period').addEventListener('change', () => toast('Periode mise a jour', 'info', 1500));
}
