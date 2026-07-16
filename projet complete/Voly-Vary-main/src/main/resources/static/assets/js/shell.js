// Interactions de l'ossature de page (sidebar/topbar) : purement visuelles,
// aucune donnee ni authentification simulee ici (contrairement au sidebar.js
// du template qui s'appuyait sur auth.js/store.js).
document.addEventListener('DOMContentLoaded', function () {
  var shell = document.getElementById('app-shell');
  var collapseBtn = document.getElementById('btn-collapse');
  var collapseLabel = document.getElementById('collapse-label');
  var mobileBtn = document.getElementById('btn-mobile-menu');
  var logoutBtn = document.getElementById('btn-logout');

  if (collapseBtn) {
    collapseBtn.addEventListener('click', function () {
      shell.classList.toggle('collapsed');
      if (collapseLabel) {
        collapseLabel.textContent = shell.classList.contains('collapsed') ? 'Etendre' : 'Reduire';
      }
    });
  }

  if (mobileBtn) {
    mobileBtn.addEventListener('click', function () {
      shell.classList.toggle('mobile-open');
    });
  }

  // Pas d'authentification reelle cote backend pour l'instant : la "deconnexion"
  // ramene simplement vers l'accueil, apres confirmation (comme dans le template).
  if (logoutBtn && typeof openConfirmModal === 'function') {
    logoutBtn.addEventListener('click', function () {
      openConfirmModal({
        title: 'Se déconnecter ?',
        message: 'Vous serez redirigé vers l\'accueil.',
        confirmLabel: 'Déconnexion',
        onConfirm: function () {
          window.location.href = shell.dataset.ctx || '/';
        }
      });
    });
  }
});
