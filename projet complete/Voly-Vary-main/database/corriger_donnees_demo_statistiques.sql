-- Corrige uniquement le jeu de démonstration du module Statistiques.
-- Ce script est idempotent : il peut être relancé sans dupliquer les données de référence.

BEGIN;

-- La page Fournitures exige une catégorie, une référence et un prix non nuls.
INSERT INTO categorie_fourniture (libelle)
SELECT 'Démonstration statistiques'
WHERE NOT EXISTS (
    SELECT 1 FROM categorie_fourniture WHERE libelle = 'Démonstration statistiques'
);

UPDATE fourniture
SET id_categorie = (
        SELECT id
        FROM categorie_fourniture
        WHERE libelle = 'Démonstration statistiques'
        ORDER BY id
        LIMIT 1
    ),
    reference = 'DEMO-STAT-FOURNITURE',
    prix_unitaire = 2500.00
WHERE id IN (
    SELECT DISTINCT dt.id_fourniture
    FROM detail_transaction dt
    JOIN "transaction" t ON t.id = dt.id_transaction
    WHERE t.reference LIKE 'DEMO-STAT-%'
);

-- Les transactions de démonstration doivent posséder un client pour apparaître dans la liste.
INSERT INTO client (reference, nom, prenom, telephone, date)
SELECT 'DEMO-STAT-CLIENT', 'Client', 'Statistiques', '0340000000', DATE '2026-02-10'
WHERE NOT EXISTS (
    SELECT 1 FROM client WHERE reference = 'DEMO-STAT-CLIENT'
);

UPDATE "transaction"
SET id_client = (
    SELECT id FROM client WHERE reference = 'DEMO-STAT-CLIENT' ORDER BY id LIMIT 1
)
WHERE reference LIKE 'DEMO-STAT-%';

-- Le montant est calculé une fois avec le prix du jeu de test, comme lors d'une vraie transaction.
UPDATE detail_transaction dt
SET montant_ligne = dt.quantite * f.prix_unitaire
FROM fourniture f, "transaction" t
WHERE f.id = dt.id_fourniture
  AND t.id = dt.id_transaction
  AND t.reference LIKE 'DEMO-STAT-%';

UPDATE "transaction" t
SET montant_total = total.montant
FROM (
    SELECT dt.id_transaction, COALESCE(SUM(dt.montant_ligne), 0) AS montant
    FROM detail_transaction dt
    GROUP BY dt.id_transaction
) total
WHERE total.id_transaction = t.id
  AND t.reference LIKE 'DEMO-STAT-%';

-- La page Nouvelle distribution charge Produit dans une entité dont rendement est un double.
UPDATE produit
SET rendement = CASE nom
    WHEN 'Riz blanc' THEN 65.00
    WHEN 'Akofom-bary' THEN 20.00
    WHEN 'Tofom-bary' THEN 10.00
    WHEN 'Vary madinika' THEN 5.00
    ELSE rendement
END
WHERE id IN (
    SELECT DISTINCT dlt.id_produit
    FROM detail_lot_transforme dlt
    JOIN lot_paddy_transforme lpt ON lpt.id = dlt.id_lot_transforme
    WHERE lpt.reference LIKE 'DEMO-STAT-%'
);

-- Le prix unitaire permet à la statistique de retrouver la réduction réellement appliquée.
INSERT INTO collecte (prix_unitaire)
SELECT 1000.00
WHERE NOT EXISTS (
    SELECT 1 FROM collecte WHERE prix_unitaire = 1000.00
);

UPDATE lot_paddy
SET id_collecte = (
    SELECT id FROM collecte WHERE prix_unitaire = 1000.00 ORDER BY id LIMIT 1
)
WHERE reference LIKE 'DEMO-STAT-%';

COMMIT;
