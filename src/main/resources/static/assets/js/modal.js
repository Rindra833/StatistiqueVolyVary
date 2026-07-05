// gestion des modales generiques (formulaire + confirmation)
function ensureModalRoot() {
  let root = document.getElementById('modal-root');
  if (!root) {
    root = document.createElement('div');
    root.id = 'modal-root';
    document.body.appendChild(root);
  }
  return root;
}

function closeModal(overlay) {
  overlay.classList.remove('open');
  setTimeout(() => overlay.remove(), 200);
}

// options: { title, bodyHtml, confirmLabel, cancelLabel, onConfirm(formData), confirmClass }
function openFormModal(options) {
  const root = ensureModalRoot();
  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.innerHTML = `
    <div class="modal-box">
      <div class="modal-header">
        <h3>${options.title}</h3>
        <button class="modal-close" aria-label="Fermer">&times;</button>
      </div>
      <form class="modal-form">
        <div class="modal-body">${options.bodyHtml}</div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline" data-cancel>${options.cancelLabel || 'Annuler'}</button>
          <button type="submit" class="btn ${options.confirmClass || 'btn-primary'}">${options.confirmLabel || 'Enregistrer'}</button>
        </div>
      </form>
    </div>`;
  root.appendChild(overlay);
  requestAnimationFrame(() => overlay.classList.add('open'));

  overlay.querySelector('.modal-close').addEventListener('click', () => closeModal(overlay));
  overlay.querySelector('[data-cancel]').addEventListener('click', () => closeModal(overlay));
  overlay.addEventListener('click', e => { if (e.target === overlay) closeModal(overlay); });

  const form = overlay.querySelector('.modal-form');
  form.addEventListener('submit', e => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(form).entries());
    if (options.onConfirm) {
      const ok = options.onConfirm(data, form);
      if (ok !== false) closeModal(overlay);
    } else {
      closeModal(overlay);
    }
  });
  return overlay;
}

// options: { title, message, confirmLabel, onConfirm }
function openConfirmModal(options) {
  const root = ensureModalRoot();
  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.innerHTML = `
    <div class="modal-box modal-confirm">
      <div class="modal-body">
        <div class="confirm-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 9v4M12 17h.01M10.29 3.86l-8.18 14.14A2 2 0 0 0 3.82 21h16.36a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/></svg></div>
        <h3 style="margin-bottom:8px;font-size:var(--fs-lg)">${options.title || 'Confirmer'}</h3>
        <p style="color:var(--color-gray-500);font-size:var(--fs-sm)">${options.message || ''}</p>
      </div>
      <div class="modal-footer" style="justify-content:center">
        <button type="button" class="btn btn-outline" data-cancel>Annuler</button>
        <button type="button" class="btn btn-danger" data-confirm>${options.confirmLabel || 'Supprimer'}</button>
      </div>
    </div>`;
  root.appendChild(overlay);
  requestAnimationFrame(() => overlay.classList.add('open'));

  overlay.querySelector('[data-cancel]').addEventListener('click', () => closeModal(overlay));
  overlay.addEventListener('click', e => { if (e.target === overlay) closeModal(overlay); });
  overlay.querySelector('[data-confirm]').addEventListener('click', () => {
    if (options.onConfirm) options.onConfirm();
    closeModal(overlay);
  });
  return overlay;
}
