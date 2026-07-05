// config des roles et pages autorisees par role
const ROLES = {
  ADMIN: 'Administrateur',
  TRANSACTION: 'Responsable Transaction',
  COLLECTE: 'Responsable Collecte',
  TRANSFORMATION: 'Responsable Transformation',
  DISTRIBUTION: 'Responsable Distribution',
  STATISTIQUES: 'Responsable Statistiques',
};

// pages accessibles par role (identifiant de page, pas le chemin complet)
// le dashboard est reserve a l'administrateur ; les autres roles atterrissent sur leur module
const ROLE_PAGES = {
  [ROLES.ADMIN]: ['dashboard', 'transaction', 'collecte', 'transformation', 'distribution', 'statistiques', 'fournitures', 'livreurs', 'utilisateurs', 'configuration', 'profil'],
  [ROLES.TRANSACTION]: ['transaction', 'profil'],
  [ROLES.COLLECTE]: ['collecte', 'profil'],
  [ROLES.TRANSFORMATION]: ['transformation', 'profil'],
  [ROLES.DISTRIBUTION]: ['distribution', 'profil'],
  [ROLES.STATISTIQUES]: ['statistiques', 'profil'],
};

// page d'accueil de chaque role apres connexion (ou en cas d'acces refuse)
const ROLE_HOME = {
  [ROLES.ADMIN]: 'dashboard',
  [ROLES.TRANSACTION]: 'transaction',
  [ROLES.COLLECTE]: 'collecte',
  [ROLES.TRANSFORMATION]: 'transformation',
  [ROLES.DISTRIBUTION]: 'distribution',
  [ROLES.STATISTIQUES]: 'statistiques',
};

// racine relative pour rejoindre /pages/* depuis n'importe quelle profondeur
function appRoot() {
  const depth = window.APP_DEPTH || 1;
  return '../'.repeat(depth);
}

// chemin absolu (relatif a la racine du projet) vers la page d'accueil du role connecte
function roleHomePath() {
  const user = getUser();
  const home = user ? ROLE_HOME[user.role] : 'login';
  return appRoot() + 'pages/' + home + '/index.html';
}

function login(email, password, role) {
  // simulation locale, a remplacer par appel Spring Boot /api/auth/login
  const user = { email, role, name: email.split('@')[0] };
  sessionStorage.setItem('vv_user', JSON.stringify(user));
  return user;
}

function logout() {
  sessionStorage.removeItem('vv_user');
  window.location.href = appRoot() + 'pages/login/index.html';
}

function getUser() {
  const raw = sessionStorage.getItem('vv_user');
  return raw ? JSON.parse(raw) : null;
}

function canAccess(pageId) {
  const user = getUser();
  if (!user) return false;
  const allowed = ROLE_PAGES[user.role] || [];
  return allowed.includes(pageId);
}

// a appeler en haut de chaque page protegee, avec l'id de la page courante
function guardPage(pageId) {
  const user = getUser();
  if (!user) {
    window.location.href = appRoot() + 'pages/login/index.html';
    return null;
  }
  if (!canAccess(pageId)) {
    window.location.href = roleHomePath();
    return null;
  }
  return user;
}

function initials(name) {
  return name.split(/[\s.@]+/).filter(Boolean).slice(0, 2).map(s => s[0].toUpperCase()).join('');
}
