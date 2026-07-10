function openStaticModal(modalId) {
  document.getElementById(modalId)?.classList.add('open');
}

function closeStaticModal(modal) {
  const element = typeof modal === 'string' ? document.getElementById(modal) : modal;
  element?.classList.remove('open');
}

function initStaticModals() {
  document.querySelectorAll('[data-modal-close]').forEach(button => {
    button.addEventListener('click', () => closeStaticModal(button.closest('.modal-overlay')));
  });

  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', event => {
      if (event.target === overlay) closeStaticModal(overlay);
    });
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
      document.querySelectorAll('.modal-overlay.open').forEach(closeStaticModal);
    }
  });
}

function initStaticShell(activePage) {
  const user = guardPage(activePage);
  if (!user) return null;

  document.querySelectorAll('[data-user-name]').forEach(element => {
    element.textContent = user.name || user.nom;
  });
  document.querySelectorAll('[data-user-role]').forEach(element => {
    element.textContent = user.role;
  });
  document.querySelectorAll('[data-user-initials]').forEach(element => {
    element.textContent = initials(user.name || user.nom);
  });

  const allowedPages = ROLE_PAGES[user.role] || [];
  document.querySelectorAll('[data-page-id]').forEach(link => {
    const pageId = link.dataset.pageId;
    link.hidden = pageId !== 'profil' && !allowedPages.includes(pageId);
    link.classList.toggle('active', pageId === activePage);
  });

  document.getElementById('btn-collapse')?.addEventListener('click', () => {
    const shell = document.getElementById('app-shell');
    shell.classList.toggle('collapsed');
    document.getElementById('collapse-label').textContent = shell.classList.contains('collapsed')
      ? 'Étendre'
      : 'Réduire';
  });

  document.getElementById('btn-mobile-menu')?.addEventListener('click', () => {
    document.getElementById('app-shell').classList.toggle('mobile-open');
  });

  document.getElementById('btn-logout')?.addEventListener('click', () => {
    openStaticModal('logout-modal');
  });
  document.getElementById('confirm-logout')?.addEventListener('click', logout);

  initStaticModals();
  return user;
}
