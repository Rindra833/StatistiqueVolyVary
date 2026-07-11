package com.volyVary.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.volyVary.model.Employee;
import com.volyVary.model.Utilisateur;
import com.volyVary.repository.EmployeeRepository;
import com.volyVary.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder encodeurMotDePasse;

    /**
     * Regroupe les dépendances nécessaires à la gestion des comptes : stockage des utilisateurs,
     * consultation des employés associés et encodage sécurisé des mots de passe.
     */
    public UtilisateurService(
        UtilisateurRepository utilisateurRepository,
        EmployeeRepository employeeRepository,
        BCryptPasswordEncoder encodeurMotDePasse
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.employeeRepository = employeeRepository;
        this.encodeurMotDePasse = encodeurMotDePasse;
    }

    /**
     * Retourne les comptes dont le nom correspond à la recherche et dont le rôle correspond
     * au filtre facultatif. Les résultats sont ordonnés pour stabiliser l'affichage JSP.
     */
    public List<Utilisateur> lister(String recherche, String role) {
        String texteRecherche = normaliser(recherche);
        String roleRecherche = normaliser(role);

        return utilisateurRepository.findAll().stream()
            .filter(utilisateur -> texteRecherche.isEmpty()
                || normaliser(utilisateur.getNom()).contains(texteRecherche))
            .filter(utilisateur -> roleRecherche.isEmpty()
                || normaliser(utilisateur.getRole()).equals(roleRecherche))
            .sorted(Comparator.comparing(Utilisateur::getNom, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    /**
     * Charge un compte existant pour le formulaire de modification et produit une erreur claire
     * lorsqu'un identifiant absent ou obsolète est reçu.
     */
    public Utilisateur obtenir(Integer id) {
        return utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
    }

    /**
     * Fournit la liste des employés disponibles afin que l'association soit choisie dans un
     * menu HTML plutôt que saisie manuellement sous la forme d'un identifiant technique.
     */
    public List<Employee> listerEmployees() {
        return employeeRepository.findAll().stream()
            .sorted(Comparator.comparing(Employee::getNom, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    /**
     * Retourne les rôles autorisés par l'interface. Cette liste unique évite de dupliquer les
     * valeurs dans les contrôleurs et facilite leur affichage dans les JSP.
     */
    public List<String> listerRoles() {
        return List.of(
            "Administrateur",
            "Responsable Transaction",
            "Responsable Collecte",
            "Responsable Transformation",
            "Responsable Distribution",
            "Responsable Statistiques"
        );
    }

    /**
     * Crée ou modifie un compte, résout l'employé éventuellement sélectionné et encode le mot
     * de passe seulement lorsqu'une nouvelle valeur a été saisie. Un nouveau compte doit toujours
     * recevoir un mot de passe afin de rester utilisable et sécurisé.
     */
    public void enregistrer(
        Integer id,
        String nom,
        String motDePasse,
        String role,
        Integer employeeId
    ) {
        Utilisateur utilisateur = id == null ? new Utilisateur() : obtenir(id);
        utilisateur.setNom(nom);
        utilisateur.setRole(role);
        utilisateur.setEmployee(resoudreEmployee(employeeId));

        if (id == null && (motDePasse == null || motDePasse.isBlank())) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire pour un nouveau compte.");
        }
        if (motDePasse != null && !motDePasse.isBlank()) {
            utilisateur.setMdp(encodeurMotDePasse.encode(motDePasse));
        }
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprime un compte lorsqu'il existe. Le contrôle préalable empêche une exception inutile
     * si le formulaire de suppression est envoyé plusieurs fois.
     */
    public void supprimer(Integer id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
        }
    }

    /**
     * Convertit l'identifiant facultatif du formulaire en entité Employee persistante. Une valeur
     * nulle signifie simplement qu'aucun employé n'est encore lié au compte.
     */
    private Employee resoudreEmployee(Integer employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employé introuvable : " + employeeId));
    }

    /**
     * Uniformise les textes utilisés par les filtres en gérant les valeurs nulles et la casse.
     */
    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().toLowerCase();
    }
}
