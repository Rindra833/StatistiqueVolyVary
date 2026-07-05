// systeme de notifications toast
const ICONS_TOAST = {
  success: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"/></svg>',
  error: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M15 9l-6 6M9 9l6 6"/></svg>',
  warning: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 9v4M12 17h.01M10.29 3.86l-8.18 14.14A2 2 0 0 0 3.82 21h16.36a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/></svg>',
  info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/></svg>',
};

function ensureToastStack() {
  let stack = document.querySelector('.toast-stack');
  if (!stack) {
    stack = document.createElement('div');
    stack.className = 'toast-stack';
    document.body.appendChild(stack);
  }
  return stack;
}

function toast(message, type = 'info', duration = 3500) {
  const stack = ensureToastStack();
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = `${ICONS_TOAST[type] || ICONS_TOAST.info}<span class="toast-text">${message}</span><button class="toast-close" aria-label="Fermer">&times;</button>`;
  stack.appendChild(el);
  requestAnimationFrame(() => el.classList.add('show'));

  const remove = () => {
    el.classList.remove('show');
    setTimeout(() => el.remove(), 200);
  };
  el.querySelector('.toast-close').addEventListener('click', remove);
  if (duration) setTimeout(remove, duration);
}
