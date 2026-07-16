// stockage local partage entre les pages d'un meme module (en attendant le backend)
// collections persistantes (liste des transactions, collectes, etc.)
function storeLoad(key, seed) {
  const raw = localStorage.getItem(key);
  if (raw) { try { return JSON.parse(raw); } catch { /* seed de secours */ } }
  localStorage.setItem(key, JSON.stringify(seed));
  return seed;
}
function storeSave(key, data) { localStorage.setItem(key, JSON.stringify(data)); }

// brouillon temporaire (formulaire "nouvelle" -> page "facture")
function draftSave(key, data) { sessionStorage.setItem(key, JSON.stringify(data)); }
function draftLoad(key) {
  const raw = sessionStorage.getItem(key);
  return raw ? JSON.parse(raw) : null;
}
function draftClear(key) { sessionStorage.removeItem(key); }

// toast differe: pose un message avant une redirection, consomme au chargement de la page suivante
function toastQueue(message, type) { sessionStorage.setItem('vv_toast', JSON.stringify({ message, type })); }
function toastConsume() {
  const raw = sessionStorage.getItem('vv_toast');
  if (!raw) return;
  sessionStorage.removeItem('vv_toast');
  const { message, type } = JSON.parse(raw);
  toast(message, type);
}

function formatAr(n) { return `${Number(n || 0).toLocaleString('fr-FR')} Ar`; }
function formatDateHeure(iso) {
  const d = iso ? new Date(iso) : new Date();
  return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' }) + ' a ' + d.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
}
