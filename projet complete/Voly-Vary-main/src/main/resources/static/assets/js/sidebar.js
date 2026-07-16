// icones SVG (stroke, coherent avec le reste de l'UI)
const NAV_ICONS = {
  dashboard: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="9" rx="1.5"/><rect x="14" y="3" width="7" height="5" rx="1.5"/><rect x="14" y="12" width="7" height="9" rx="1.5"/><rect x="3" y="16" width="7" height="5" rx="1.5"/></svg>',
  transaction: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 7h13l-3-3M21 17H8l3 3"/></svg>',
  collecte: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>',
  transformation: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 2l4 4-4 4M3 12v-2a4 4 0 014-4h14M7 22l-4-4 4-4M21 12v2a4 4 0 01-4 4H3"/></svg>',
  distribution: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="7" width="15" height="13"/><path d="M16 11h4l3 4v5h-7z"/><circle cx="5.5" cy="20.5" r="1.5"/><circle cx="18.5" cy="20.5" r="1.5"/></svg>',
  statistiques: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 3v18h18M8 17V9m5 8V5m5 12v-6"/></svg>',
  fournitures: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 8L12 3 3 8m18 0l-9 5m9-5v8l-9 5m0-5L3 8m9 5v8M3 8v8l9 5"/></svg>',
  livreurs: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a8 8 0 0116 0v1"/></svg>',
  utilisateurs: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>',
  configuration: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 11-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 11-2.83-2.83l.06-.06A1.65 1.65 0 004.6 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 112.83-2.83l.06.06A1.65 1.65 0 009 4.6a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 112.83 2.83l-.06.06A1.65 1.65 0 0019.4 9c.2.63.78 1.05 1.44 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>',
  profil: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 21v-1a8 8 0 0116 0v1"/></svg>',
};

const NAV_LABELS = {
  dashboard: 'Dashboard',
  transaction: 'Transaction',
  collecte: 'Collecte',
  transformation: 'Transformation',
  distribution: 'Distribution',
  statistiques: 'Statistiques',
  fournitures: 'Fournitures',
  livreurs: 'Livreurs',
  utilisateurs: 'Utilisateurs',
  configuration: 'Configuration',
  profil: 'Profil',
};

// chemin de chaque page relatif a la racine du projet
const NAV_PATHS = {
  dashboard: 'pages/dashboard/index.html',
  transaction: 'pages/transaction/index.html',
  collecte: 'pages/collecte/index.html',
  transformation: 'pages/transformation/index.html',
  distribution: 'pages/distribution/index.html',
  statistiques: 'pages/statistiques/index.html',
  fournitures: 'pages/admin/fournitures/index.html',
  livreurs: 'pages/admin/livreurs/index.html',
  utilisateurs: 'pages/admin/utilisateurs/index.html',
  configuration: 'pages/admin/configuration/index.html',
  profil: 'pages/profil/index.html',
};

function renderShell(activePage) {
  const user = guardPage(activePage);
  if (!user) return;
  const root = appRoot();
  const allowed = (ROLE_PAGES[user.role] || []).filter(id => id !== 'profil');
  const mainNav = allowed.filter(id => ['dashboard', 'transaction', 'collecte', 'transformation', 'distribution', 'statistiques'].includes(id));
  const adminNav = allowed.filter(id => ['fournitures', 'livreurs', 'utilisateurs', 'configuration'].includes(id));

  const navItem = id => `
    <a href="${root}${NAV_PATHS[id]}" class="sidebar-link ${id === activePage ? 'active' : ''}">
      ${NAV_ICONS[id]}<span>${NAV_LABELS[id]}</span>
    </a>`;

  const shell = document.createElement('div');
  shell.innerHTML = `
    <div class="app-shell" id="app-shell">
      <aside class="sidebar">
        <div class="sidebar-brand">
          <svg class="logo-mark" viewBox="0 0 32 32" fill="none"><rect width="32" height="32" rx="8" fill="#2563EB"/><path d="M9 11l7 12 7-12" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          <span class="logo-text">VOLY VARY</span>
        </div>
        <div class="sidebar-user">
          <div class="avatar">${initials(user.name)}</div>
          <div class="who"><strong>${user.name}</strong><span>${user.role}</span></div>
        </div>
        <nav class="sidebar-nav">
          ${mainNav.map(navItem).join('')}
          ${adminNav.length ? `<div style="padding:14px 12px 6px;font-size:11px;text-transform:uppercase;letter-spacing:.05em;color:var(--color-gray-500);font-weight:600">Administration</div>` : ''}
          ${adminNav.map(navItem).join('')}
        </nav>
        <div class="sidebar-footer">
          ${navItem('profil')}
          <button class="sidebar-link" id="btn-logout" style="width:100%">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/></svg>
            <span>Deconnexion</span>
          </button>
          <button class="sidebar-toggle" id="btn-collapse">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px"><path d="M15 18l-6-6 6-6"/></svg>
            <span id="collapse-label">Reduire</span>
          </button>
        </div>
      </aside>
      <div class="main-content">
        <header class="topbar">
          <button class="icon-btn menu-btn" id="btn-mobile-menu">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 12h18M3 6h18M3 18h18"/></svg>
          </button>
          <div class="topbar-search">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input type="text" placeholder="Rechercher..." />
          </div>
          <div class="topbar-actions">
            <button class="icon-btn" aria-label="Notifications">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8a6 6 0 00-12 0c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0"/></svg>
              <span class="dot"></span>
            </button>
            <div class="topbar-profile">
              <div class="avatar">${initials(user.name)}</div>
            </div>
          </div>
        </header>
        <main class="page-body" id="page-body"></main>
      </div>
    </div>`;
  document.body.prepend(shell.firstElementChild);

  document.getElementById('btn-logout').addEventListener('click', () => {
    openConfirmModal({
      title: 'Se deconnecter ?',
      message: 'Vous devrez vous reconnecter pour acceder a l\'application.',
      confirmLabel: 'Deconnexion',
      onConfirm: logout,
    });
  });
  document.getElementById('btn-collapse').addEventListener('click', () => {
    const el = document.getElementById('app-shell');
    el.classList.toggle('collapsed');
    document.getElementById('collapse-label').textContent = el.classList.contains('collapsed') ? 'Etendre' : 'Reduire';
  });
  document.getElementById('btn-mobile-menu').addEventListener('click', () => {
    document.getElementById('app-shell').classList.toggle('mobile-open');
  });

  return document.getElementById('page-body');
}
