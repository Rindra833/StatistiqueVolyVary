// Si une session frontend existe déjà, rediriger vers la page autorisée.
if (getUser()) window.location.href = roleHomePath();

const form = document.getElementById('login-form');
const toggleBtn = document.getElementById('toggle-pass');
const passInput = document.getElementById('password');
initStaticModals();

toggleBtn.addEventListener('click', () => {
  passInput.type = passInput.type === 'password' ? 'text' : 'password';
});

function setInvalid(fieldId, invalid) {
  document.getElementById(fieldId).classList.toggle('invalid', invalid);
}

form.addEventListener('submit', async event => {
  event.preventDefault();

  const nom = document.getElementById('nom').value.trim();
  const mdp = passInput.value;

  setInvalid('field-nom', nom.length === 0);
  setInvalid('field-password', mdp.length === 0);

  if (!nom || !mdp) {
    toast('Veuillez corriger les champs en rouge', 'error');
    return;
  }

  const button = document.getElementById('login-submit');
  const label = button.querySelector('span');
  button.disabled = true;
  label.textContent = 'Connexion...';

  try {
    const authenticatedUser = await apiRequest('/api/auth/login', {
      method: 'POST',
      body: { nom, mdp },
    });

    login(authenticatedUser);
    toast('Connexion réussie', 'success', 1200);
    setTimeout(() => {
      window.location.href = roleHomePath();
    }, 400);
  } catch (error) {
    toast(error.message || 'Connexion impossible', 'error');
    button.disabled = false;
    label.textContent = 'Se connecter';
  }
});

document.getElementById('forgot-link').addEventListener('click', event => {
  event.preventDefault();
  openStaticModal('forgot-password-modal');
});
