/**
 * Exporte le contenu textuel d'un tableau JSP au format CSV. La fonction ne crée aucun élément
 * visuel : elle lit simplement les cellules déjà rendues côté serveur, ignore la colonne Actions
 * puis demande au navigateur d'enregistrer le résultat dans un fichier local.
 */
function exporterTableauCsv(identifiantTableau, nomFichier) {
  const tableau = document.getElementById(identifiantTableau);
  const lignes = Array.from(tableau.querySelectorAll('tr')).map(ligne => {
    const cellules = Array.from(ligne.querySelectorAll('th, td')).slice(0, -1);
    return cellules.map(cellule => `"${cellule.textContent.trim().replaceAll('"', '""')}"`).join(';');
  });
  const fichier = new Blob([`\uFEFF${lignes.join('\n')}`], { type: 'text/csv;charset=utf-8' });
  const lien = document.createElement('a');
  lien.href = URL.createObjectURL(fichier);
  lien.download = nomFichier;
  lien.click();
  URL.revokeObjectURL(lien.href);
}

/**
 * Ouvre la boîte d'impression native du navigateur. L'utilisateur peut imprimer la JSP telle
 * qu'elle est affichée ou choisir une imprimante PDF, sans reconstruire le document en JavaScript.
 */
function imprimerPage() {
  window.print();
}
