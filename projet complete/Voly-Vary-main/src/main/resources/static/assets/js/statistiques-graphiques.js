/*
 * Modifier uniquement cette valeur pendant une démonstration :
 * 'bar', 'pie' et 'doughnut' fonctionnent avec les mêmes données.
 */
const TYPE_GRAPHIQUE_TRANSACTIONS = 'bar';

/**
 * Lit les données déjà rendues par JSP dans le tableau HTML puis confie uniquement leur
 * représentation graphique à Chart.js. Cette fonction ne crée ni contenu métier ni formulaire :
 * le tableau reste la source visible et accessible même si JavaScript est désactivé.
 */
function afficherGraphiqueTransactions() {
  const canvas = document.getElementById('graphique-transactions');
  const lignes = document.querySelectorAll(
    '#tableau-statistiques-transactions tbody tr[data-statistique="transaction"]'
  );
  if (!canvas || lignes.length === 0 || typeof Chart === 'undefined') {
    return;
  }

  const libelles = Array.from(lignes, ligne => ligne.cells[0].textContent.trim());
  const nombres = Array.from(lignes, ligne => Number(ligne.dataset.nombre || 0));
  const couleurs = ['#2563eb', '#16a34a', '#d97706', '#7c3aed', '#dc2626'];
  const graphiqueCirculaire = ['pie', 'doughnut', 'polarArea'].includes(
    TYPE_GRAPHIQUE_TRANSACTIONS
  );

  new Chart(canvas, {
    type: TYPE_GRAPHIQUE_TRANSACTIONS,
    data: {
      labels: libelles,
      datasets: [{
        label: 'Nombre de transactions',
        data: nombres,
        backgroundColor: couleurs.slice(0, libelles.length),
        borderWidth: 0,
        borderRadius: graphiqueCirculaire ? 0 : 6,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: graphiqueCirculaire, position: 'bottom' },
      },
      scales: graphiqueCirculaire ? {} : { y: { beginAtZero: true, ticks: { precision: 0 } } },
    },
  });
}

document.addEventListener('DOMContentLoaded', afficherGraphiqueTransactions);
