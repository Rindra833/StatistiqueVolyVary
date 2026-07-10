async function apiRequest(url, options = {}) {
  const config = {
    credentials: 'same-origin',
    ...options,
    headers: { ...(options.headers || {}) },
  };

  if (config.body && typeof config.body !== 'string' && !(config.body instanceof FormData)) {
    config.headers['Content-Type'] = 'application/json';
    config.body = JSON.stringify(config.body);
  }

  const response = await fetch(url, config);
  const contentType = response.headers.get('content-type') || '';
  const data = response.status === 204
    ? null
    : contentType.includes('application/json')
      ? await response.json()
      : await response.text();

  if (!response.ok) {
    const message = data?.message || data || `Erreur HTTP ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  return data;
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function handleProtectedApiError(error) {
  if (error.status === 401 || error.status === 403) {
    toast('Votre session a expiré. Veuillez vous reconnecter.', 'error');
    setTimeout(logout, 800);
    return;
  }
  toast(error.message || 'Une erreur est survenue', 'error');
}
