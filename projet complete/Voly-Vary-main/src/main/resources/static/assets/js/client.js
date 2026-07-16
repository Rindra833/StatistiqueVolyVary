/**
 * Recherche une référence existante sans créer de HTML. Si elle n'existe pas, les champs restent
 * modifiables : le module appelant créera le client lors de la validation de son formulaire.
 */
function rechercherClient() {
    const reference = document.getElementById('reference').value.trim();

    if (!reference) {
        document.getElementById('nom').value = '';
        document.getElementById('prenom').value = '';
        document.getElementById('telephone').value = '';
        return;
    }

    fetch("/client/rechercher/" + encodeURIComponent(reference))
        .then(response => response.json())
        .then(client => {
            if (client && client.nom) {
                document.getElementById('nom').value = client.nom || '';
                document.getElementById('prenom').value = client.prenom || '';
                document.getElementById('telephone').value = client.telephone || '';
            } else {
                document.getElementById('nom').value = '';
                document.getElementById('prenom').value = '';
                document.getElementById('telephone').value = '';
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            document.getElementById('nom').value = '';
            document.getElementById('prenom').value = '';
            document.getElementById('telephone').value = '';
        });
}
