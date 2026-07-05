// si deja connecte, on redirige direct vers sa page d'accueil
if (getUser()) window.location.href = roleHomePath();

const form = document.getElementById('login-form');
const toggleBtn = document.getElementById('toggle-pass');
const passInput = document.getElementById('password');

toggleBtn.addEventListener('click', () => {
  passInput.type = passInput.type === 'password' ? 'text' : 'password';
});

function setInvalid(fieldId, invalid) {
  document.getElementById(fieldId).classList.toggle('invalid', invalid);
}

form.addEventListener('submit', e => {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const password = passInput.value;
  const role = document.getElementById('role').value;

  const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  setInvalid('field-email', !emailValid);
  setInvalid('field-password', password.length === 0);
  setInvalid('field-role', role === '');

  if (!emailValid || password.length === 0 || role === '') {
    toast('Veuillez corriger les champs en rouge', 'error');
    return;
  }

  const btn = document.getElementById('login-submit');
  btn.disabled = true;
  btn.querySelector('span').textContent = 'Connexion...';

  // simulation appel reseau, a remplacer par POST /api/auth/login
  setTimeout(() => {
    login(email, password, role);
    toast('Connexion reussie', 'success', 1200);
    setTimeout(() => { window.location.href = roleHomePath(); }, 400);
  }, 500);
});

document.getElementById('forgot-link').addEventListener('click', e => {
  e.preventDefault();
  openFormModal({
    title: 'Mot de passe oublie',
    bodyHtml: `
      <p style="font-size:var(--fs-sm);color:var(--color-gray-500);margin-bottom:16px">
        Saisissez votre adresse email, un lien de reinitialisation vous sera envoye.
      </p>
      <div class="form-field full">
        <label for="reset-email">Email</label>
        <input type="email" name="reset-email" id="reset-email" placeholder="nom@volyvary.mg" required>
      </div>`,
    confirmLabel: 'Envoyer le lien',
    onConfirm: () => { toast('Lien de reinitialisation envoye', 'success'); },
  });
});
