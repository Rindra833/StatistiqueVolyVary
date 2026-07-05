INSERT INTO utilisateur (id, nom, mdp, role) VALUES
    (1, 'admin', '$2y$10$6hhTVQ7CwH5gILG/acoE.eilSoeeEwCbQTRMpbLCdqc9sxx.HW036', 'ROLE_ADMIN'),
    (2, 'demo',  '$2y$10$6hhTVQ7CwH5gILG/acoE.eilSoeeEwCbQTRMpbLCdqc9sxx.HW036', 'ROLE_USER');

INSERT INTO projet (id, nom, description, utilisateur_id) VALUES
    (1, 'Riziculture 2026', 'Suivi du rendement de la campagne principale', 1),
    (2, 'Compost organique', 'Projet secondaire de fertilisation', 1),
    (3, 'Parcelle demo', 'Projet de test pour le compte demo', 2);

INSERT INTO mouvement_projet (id, projet_id, date_operation, montant, nature, libelle) VALUES
    (1, 1, '2026-07-03', 120000, 'BENEFICE', 'Vente de la récolte'),
    (2, 1, '2026-07-04', 45000, 'PERTE', 'Achat de semences'),
    (3, 1, '2026-07-04', 180000, 'BENEFICE', 'Subvention coopérative'),
    (4, 1, '2026-07-05', 60000, 'BENEFICE', 'Commande locale'),
    (5, 1, '2026-07-05', 30000, 'PERTE', 'Transport vers le marché'),
    (6, 1, '2026-07-06', 95000, 'BENEFICE', 'Paiement client principal'),
    (7, 1, '2026-06-29', 40000, 'PERTE', 'Entretien du matériel'),
    (8, 2, '2026-07-03', 25000, 'BENEFICE', 'Vente compost'),
    (9, 2, '2026-07-04', 15000, 'PERTE', 'Emballages'),
    (10, 2, '2026-07-06', 52000, 'BENEFICE', 'Contrat ferme voisine'),
    (11, 3, '2026-07-03', 10000, 'BENEFICE', 'Test entrée positive'),
    (12, 3, '2026-07-05', 2000, 'PERTE', 'Test sortie négative');

ALTER TABLE utilisateur ALTER COLUMN id RESTART WITH 3;
ALTER TABLE projet ALTER COLUMN id RESTART WITH 4;
ALTER TABLE mouvement_projet ALTER COLUMN id RESTART WITH 13;
