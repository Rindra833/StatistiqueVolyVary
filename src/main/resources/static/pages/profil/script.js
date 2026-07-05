const pageBody = renderShell('profil');
if (pageBody) {
  pageBody.appendChild(document.getElementById('tpl-page').content.cloneNode(true));
  const user = getUser();

  document.getElementById('profile-avatar').textContent = initials(user.name);
  document.getElementById('profile-name').textContent = user.name;
  document.getElementById('profile-role').textContent = user.role;
  document.getElementById('profile-email').textContent = user.email;
  document.getElementById('in-name').value = user.name;
  document.getElementById('in-email').value = user.email;

  document.getElementById('form-profile').addEventListener('submit', e => {
    e.preventDefault();
    toast('Profil mis a jour', 'success');
  });

  document.getElementById('form-password').addEventListener('submit', e => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target).entries());
    if (data.new !== data.confirm) { toast('Les mots de passe ne correspondent pas', 'error'); return; }
    toast('Mot de passe modifie', 'success');
    e.target.reset();
  });
}
